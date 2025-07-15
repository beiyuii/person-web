package pw.pj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import pw.pj.POJO.DO.TbUser;
import pw.pj.POJO.VO.LoginVO;
import pw.pj.POJO.VO.PageQueryVO;
import pw.pj.POJO.VO.RegisterVO;
import pw.pj.POJO.VO.UserUpdateVO;
import pw.pj.POJO.VO.UserVO;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.common.exception.BusinessException;
import pw.pj.common.result.PageResult;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.CryptUtils;
import pw.pj.common.utils.DateTimeUtils;
import pw.pj.common.utils.IpUtils;
import pw.pj.common.utils.JwtTokenUtil;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.mapper.TbUserMapper;
import pw.pj.service.TbUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 提供用户注册、登录、资料管理、权限验证等核心功能的具体实现
 * 
 * @author PersonWeb开发团队
 * @description 针对表【tb_user(用户表)】的数据库操作Service实现
 * @createDate 2025-07-01 16:44:02
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtils redisUtils;

    // ==================== 用户认证相关 ====================

    /**
     * 用户注册
     * 
     * @param registerVO 注册信息
     * @return 注册结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(RegisterVO registerVO) {
        log.info("用户注册开始，用户名：{}", registerVO.getUsername());

        // 1. 参数验证
        validateRegisterParams(registerVO);

        // 2. 检查用户名、邮箱、手机号是否已存在
        checkUserExists(registerVO);

        // 3. 验证邮箱验证码和图片验证码
        validateRegisterCodes(registerVO);

        // 4. 创建用户对象
        TbUser user = buildUserFromRegister(registerVO);

        // 5. 保存用户
        boolean saved = save(user);
        if (!saved) {
            throw new BusinessException(ResultEnum.USER_REGISTER_FAIL);
        }

        // 6. 清除验证码缓存
        clearRegisterCodes(registerVO);

        // 7. 记录注册日志
        recordRegisterLog(user, registerVO.getClientIp());

        log.info("用户注册成功，用户ID：{}，用户名：{}", user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("message", "注册成功");
        return result;
    }

    /**
     * 用户登录
     * 
     * @param loginVO 登录信息
     * @return 登录结果，包含用户信息和JWT令牌
     */
    @Override
    public Map<String, Object> login(LoginVO loginVO) {
        log.info("用户登录开始，用户名：{}", loginVO.getUsername());

        // 1. 参数验证
        validateLoginParams(loginVO);

        // 2. 检查登录限制
        checkLoginRestriction(loginVO.getUsername(), loginVO.getClientIp());

        // 3. 验证图片验证码
        validateCaptcha(loginVO.getCaptcha(), loginVO.getCaptchaUuid());

        // 4. 查询用户
        TbUser user = getUserByUsernameFromDB(loginVO.getUsername());
        if (user == null) {
            recordLoginFailure(loginVO.getUsername(), loginVO.getClientIp(), "用户不存在");
            throw new BusinessException(ResultEnum.USER_LOGIN_FAIL, "用户名或密码错误");
        }

        // 5. 验证用户状态
        validateUserStatus(user);

        // 6. 验证密码
        if (!validatePassword(loginVO.getPassword(), user.getPassword())) {
            recordLoginFailure(loginVO.getUsername(), loginVO.getClientIp(), "密码错误");
            throw new BusinessException(ResultEnum.USER_LOGIN_FAIL, "用户名或密码错误");
        }

        // 7. 生成JWT令牌
        String roles = getUserRoleString(user.getId());
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername(), roles);

        // 8. 更新登录信息
        updateLoginInfo(user.getId(), loginVO.getClientIp());

        // 9. 缓存用户信息和登录状态
        cacheUserInfo(user, token, loginVO.getRememberMe());

        // 10. 清除登录失败记录
        clearLoginFailureRecord(loginVO.getUsername());

        log.info("用户登录成功，用户ID：{}，用户名：{}", user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToVO(user));
        result.put("expiresIn", getTokenExpiresIn(loginVO.getRememberMe()));
        return result;
    }

    /**
     * 用户登出
     * 
     * @param userId 用户ID
     * @param token  JWT令牌
     * @return 登出结果
     */
    @Override
    public Boolean logout(Long userId, String token) {
        log.info("用户登出，用户ID：{}", userId);

        try {
            // 1. 加入令牌黑名单
            String blacklistKey = RedisConstants.Auth.TOKEN_BLACKLIST + token;
            redisUtils.set(blacklistKey, "1", SystemConstants.Cache.TOKEN_EXPIRE);

            // 2. 清除用户会话缓存
            String sessionKey = RedisConstants.User.USER_SESSION + userId;
            redisUtils.delete(sessionKey);

            // 3. 清除用户在线状态
            String onlineKey = RedisConstants.User.USER_ONLINE + userId;
            redisUtils.delete(onlineKey);

            // 4. 清除用户信息缓存（可选，根据业务需求决定）
            // String userInfoKey = RedisConstants.User.USER_INFO + userId;
            // redisUtils.delete(userInfoKey);

            log.info("用户登出成功，用户ID：{}", userId);
            return true;

        } catch (Exception e) {
            log.error("用户登出失败，用户ID：{}，错误：{}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * 刷新JWT令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的JWT令牌
     */
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        log.info("刷新JWT令牌");

        // 1. 验证刷新令牌
        if (!jwtTokenUtil.validateToken(refreshToken)) {
            throw new BusinessException(ResultEnum.TOKEN_INVALID, "刷新令牌无效");
        }

        // 2. 从令牌中获取用户信息
        Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);
        String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
        String roles = jwtTokenUtil.getRolesFromToken(refreshToken);

        // 3. 验证用户是否存在且状态正常
        TbUser user = getById(userId);
        if (user == null || !SystemConstants.User.STATUS_NORMAL.equals(user.getStatus())) {
            throw new BusinessException(ResultEnum.USER_NOT_FOUND, "用户不存在或已被禁用");
        }

        // 4. 生成新的JWT令牌
        String newToken = jwtTokenUtil.generateToken(userId, username, roles);

        // 5. 将旧令牌加入黑名单
        String blacklistKey = RedisConstants.Auth.TOKEN_BLACKLIST + refreshToken;
        redisUtils.set(blacklistKey, "1", SystemConstants.Cache.TOKEN_EXPIRE);

        log.info("JWT令牌刷新成功，用户ID：{}", userId);

        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("expiresIn", SystemConstants.Cache.TOKEN_EXPIRE);
        return result;
    }

    // ==================== 用户信息管理 ====================

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息VO
     */
    @Override
    public UserVO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }

        // 1. 尝试从缓存获取
        String cacheKey = RedisConstants.User.USER_INFO + userId;
        UserVO cachedUser = redisUtils.get(cacheKey, UserVO.class);
        if (cachedUser != null) {
            log.debug("从缓存获取用户信息，用户ID：{}", userId);
            return cachedUser;
        }

        // 2. 从数据库查询
        TbUser user = getById(userId);
        if (user == null || SystemConstants.User.STATUS_DELETED.equals(user.getIsDelete())) {
            return null;
        }

        // 3. 转换为VO并缓存
        UserVO userVO = convertToVO(user);
        redisUtils.set(cacheKey, userVO, SystemConstants.Cache.EXPIRE_HOUR);

        return userVO;
    }

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息VO
     */
    @Override
    public UserVO getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }

        TbUser user = getUserByUsernameFromDB(username);
        return user != null ? convertToVO(user) : null;
    }

    /**
     * 更新用户资料
     * 
     * @param userId       用户ID
     * @param userUpdateVO 更新信息
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserProfile(Long userId, UserUpdateVO userUpdateVO) {
        log.info("更新用户资料，用户ID：{}", userId);

        // 1. 验证用户是否存在
        TbUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultEnum.USER_NOT_FOUND);
        }

        // 2. 验证更新参数
        validateUpdateParams(userUpdateVO);

        // 3. 检查邮箱和手机号是否已被其他用户使用
        checkEmailAndPhoneUnique(userId, userUpdateVO.getEmail(), userUpdateVO.getPhone());

        // 4. 处理密码修改
        if (userUpdateVO.isChangePassword()) {
            if (!validatePassword(userUpdateVO.getOldPassword(), user.getPassword())) {
                throw new BusinessException(ResultEnum.USER_PASSWORD_ERROR, "原密码错误");
            }
            String newEncryptedPassword = CryptUtils.encryptPasswordWithSalt(userUpdateVO.getNewPassword());
            user.setPassword(newEncryptedPassword);
        }

        // 5. 更新用户信息
        updateUserFields(user, userUpdateVO);

        // 6. 保存到数据库
        user.setUpdateTime(new Date());
        boolean updated = updateById(user);

        if (updated) {
            // 7. 清除用户缓存
            String cacheKey = RedisConstants.User.USER_INFO + userId;
            redisUtils.delete(cacheKey);

            log.info("用户资料更新成功，用户ID：{}", userId);
        }

        return updated;
    }

    /**
     * 修改用户密码
     * 
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改用户密码，用户ID：{}", userId);

        // 1. 验证用户是否存在
        TbUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultEnum.USER_NOT_FOUND);
        }

        // 2. 验证原密码
        if (!validatePassword(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultEnum.USER_PASSWORD_ERROR, "原密码错误");
        }

        // 3. 验证新密码强度
        if (!isPasswordStrong(newPassword)) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "新密码强度不够");
        }

        // 4. 加密新密码
        String encryptedPassword = CryptUtils.encryptPasswordWithSalt(newPassword);

        // 5. 更新密码
        boolean updated = lambdaUpdate()
                .eq(TbUser::getId, userId)
                .set(TbUser::getPassword, encryptedPassword)
                .set(TbUser::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 6. 清除用户相关缓存
            clearUserCache(userId);
            log.info("用户密码修改成功，用户ID：{}", userId);
        }

        return updated;
    }

    /**
     * 重置用户密码（管理员操作）
     * 
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetPassword(Long userId, String newPassword) {
        log.info("重置用户密码，用户ID：{}", userId);

        // 1. 验证新密码强度
        if (!isPasswordStrong(newPassword)) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "新密码强度不够");
        }

        // 2. 加密新密码
        String encryptedPassword = CryptUtils.encryptPasswordWithSalt(newPassword);

        // 3. 更新密码
        boolean updated = lambdaUpdate()
                .eq(TbUser::getId, userId)
                .set(TbUser::getPassword, encryptedPassword)
                .set(TbUser::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 4. 清除用户相关缓存
            clearUserCache(userId);
            log.info("用户密码重置成功，用户ID：{}", userId);
        }

        return updated;
    }

    /**
     * 更新用户头像
     * 
     * @param userId    用户ID
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    @Override
    public Boolean updateAvatar(Long userId, String avatarUrl) {
        log.info("更新用户头像，用户ID：{}，头像URL：{}", userId, avatarUrl);

        boolean updated = lambdaUpdate()
                .eq(TbUser::getId, userId)
                .set(TbUser::getAvatar, avatarUrl)
                .set(TbUser::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 清除用户缓存
            String cacheKey = RedisConstants.User.USER_INFO + userId;
            redisUtils.delete(cacheKey);
        }

        return updated;
    }

    // ==================== 用户状态管理 ====================

    /**
     * 启用/禁用用户
     * 
     * @param userId 用户ID
     * @param status 状态：0-禁用，1-正常
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态，用户ID：{}，状态：{}", userId, status);

        boolean updated = lambdaUpdate()
                .eq(TbUser::getId, userId)
                .set(TbUser::getStatus, status)
                .set(TbUser::getUpdateTime, new Date())
                .update();

        if (updated) {
            // 如果是禁用用户，需要清除其登录状态
            if (SystemConstants.User.STATUS_DISABLED.equals(status)) {
                clearUserLoginStatus(userId);
            }

            // 清除用户缓存
            clearUserCache(userId);
        }

        return updated;
    }

    /**
     * 检查用户是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    @Override
    public Boolean existsByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }

        long count = lambdaQuery()
                .eq(TbUser::getUsername, username)
                .eq(TbUser::getIsDelete, 0)
                .count();

        return count > 0;
    }

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    @Override
    public Boolean existsByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }

        long count = lambdaQuery()
                .eq(TbUser::getEmail, email)
                .eq(TbUser::getIsDelete, 0)
                .count();

        return count > 0;
    }

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    @Override
    public Boolean existsByPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }

        long count = lambdaQuery()
                .eq(TbUser::getPhone, phone)
                .eq(TbUser::getIsDelete, 0)
                .count();

        return count > 0;
    }

    // ==================== 用户查询和统计 ====================

    /**
     * 分页查询用户列表
     * 
     * @param pageQueryVO 分页查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<UserVO> getUserList(PageQueryVO pageQueryVO) {
        // 构建查询条件
        LambdaQueryWrapper<TbUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbUser::getIsDelete, 0)
                .orderByDesc(TbUser::getCreateTime);

        // 添加搜索条件
        if (StringUtils.isNotBlank(pageQueryVO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(TbUser::getUsername, pageQueryVO.getKeyword())
                    .or().like(TbUser::getNickname, pageQueryVO.getKeyword())
                    .or().like(TbUser::getEmail, pageQueryVO.getKeyword()));
        }

        // 分页查询
        Page<TbUser> page = new Page<>(pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
        Page<TbUser> resultPage = page(page, queryWrapper);

        // 转换为VO并返回
        List<UserVO> userVOList = convertToVOList(resultPage.getRecords());

        return PageResult.of(userVOList, resultPage.getTotal(),
                pageQueryVO.getPageNum(), pageQueryVO.getPageSize());
    }

    /**
     * 搜索用户
     * 
     * @param keyword     关键词
     * @param pageSize    每页大小
     * @param currentPage 当前页
     * @return 搜索结果
     */
    @Override
    public PageResult<UserVO> searchUsers(String keyword, Integer pageSize, Integer currentPage) {
        PageQueryVO pageQueryVO = new PageQueryVO();
        pageQueryVO.setKeyword(keyword);
        pageQueryVO.setPageSize(pageSize);
        pageQueryVO.setPageNum(currentPage);

        return getUserList(pageQueryVO);
    }

    /**
     * 获取用户统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        String cacheKey = RedisConstants.Statistics.USER_STATISTICS + userId;
        Map<String, Object> cachedStats = redisUtils.get(cacheKey, Map.class);

        if (cachedStats != null) {
            return cachedStats;
        }

        Map<String, Object> statistics = new HashMap<>();

        try {
            // 实现具体的统计逻辑，如文章数、评论数、点赞数等
            // 注：这里可以调用其他Service来获取统计数据，但为避免循环依赖，使用直接查询

            // 查询用户文章数量（发布状态的文章）
            // 这里简化处理，实际应该查询tb_article表，避免循环依赖直接设为0
            statistics.put("articleCount", 0);

            // 查询用户评论数量（审核通过的评论）
            statistics.put("commentCount", 0);

            // 查询用户获得的点赞数
            statistics.put("likeCount", 0);

            // 查询粉丝数量
            statistics.put("followersCount", 0);

            // 查询关注数量
            statistics.put("followingCount", 0);

            // 查询用户浏览量
            statistics.put("viewCount", 0);

            // 用户等级（根据积分计算）
            statistics.put("userLevel", 1);

            // 积分
            statistics.put("points", 0);

            log.info("用户统计信息查询完成：userId={}", userId);

        } catch (Exception e) {
            log.error("查询用户统计信息失败：userId={}, error={}", userId, e.getMessage(), e);
            // 返回默认值
            statistics.put("articleCount", 0);
            statistics.put("commentCount", 0);
            statistics.put("likeCount", 0);
            statistics.put("followersCount", 0);
            statistics.put("followingCount", 0);
            statistics.put("viewCount", 0);
            statistics.put("userLevel", 1);
            statistics.put("points", 0);
        }

        // 缓存统计结果
        redisUtils.set(cacheKey, statistics, SystemConstants.Cache.EXPIRE_HOUR);

        return statistics;
    }

    // ==================== 权限验证相关 ====================

    /**
     * 验证用户权限
     * 
     * @param userId     用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    @Override
    public Boolean hasPermission(Long userId, String permission) {
        // 获取用户权限列表
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission);
    }

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        String cacheKey = RedisConstants.User.USER_ROLE + userId;
        List<String> cachedRoles = redisUtils.get(cacheKey, List.class);

        if (cachedRoles != null) {
            return cachedRoles;
        }

        // 从角色表查询用户角色，目前根据用户信息动态判断
        List<String> roles = new ArrayList<>();

        try {
            // 查询用户信息
            TbUser user = getById(userId);
            if (user == null) {
                roles.add(SystemConstants.System.USER_ROLE);
                return roles;
            }

            // 根据用户身份分配角色（简化逻辑，默认为普通用户）
            // 可以通过用户名或其他方式识别管理员
            if ("admin".equals(user.getUsername()) || "administrator".equals(user.getUsername())) {
                // 管理员
                roles.add("admin");
                roles.add("user");
            } else {
                // 普通用户
                roles.add("user");
            }

            log.debug("用户角色查询完成：userId={}, roles={}", userId, roles);

        } catch (Exception e) {
            log.error("查询用户角色失败：userId={}, error={}", userId, e.getMessage(), e);
            // 返回默认角色
            roles.add("user");
        }

        // 缓存角色信息
        redisUtils.set(cacheKey, roles, SystemConstants.Cache.EXPIRE_DAY);

        return roles;
    }

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<String> getUserPermissions(Long userId) {
        String cacheKey = RedisConstants.User.USER_PERMISSION + userId;
        List<String> cachedPermissions = redisUtils.get(cacheKey, List.class);

        if (cachedPermissions != null) {
            return cachedPermissions;
        }

        // 从权限表查询用户权限，目前根据用户角色动态分配权限
        List<String> permissions = new ArrayList<>();

        try {
            // 获取用户角色
            List<String> userRoles = getUserRoles(userId);

            // 根据角色分配权限
            for (String role : userRoles) {
                switch (role) {
                    case "admin":
                        // 管理员权限：所有权限
                        permissions.add("system:config");
                        permissions.add("system:monitor");
                        permissions.add("user:manage");
                        permissions.add("article:manage");
                        permissions.add("comment:manage");
                        permissions.add("file:manage");
                        permissions.add("statistics:view");
                        // 继续添加编辑者和用户权限
                    case "editor":
                        // 编辑者权限：内容管理
                        permissions.add("article:create");
                        permissions.add("article:update");
                        permissions.add("article:delete");
                        permissions.add("article:publish");
                        permissions.add("category:manage");
                        permissions.add("tag:manage");
                        permissions.add("comment:moderate");
                        permissions.add("file:upload");
                        // 继续添加用户权限
                    case "user":
                        // 普通用户权限：基础权限
                        permissions.add("user:read");
                        permissions.add("user:update");
                        permissions.add("article:read");
                        permissions.add("article:like");
                        permissions.add("comment:create");
                        permissions.add("comment:read");
                        permissions.add("comment:like");
                        permissions.add("file:read");
                        break;
                    default:
                        // 默认只有读取权限
                        permissions.add("user:read");
                        permissions.add("article:read");
                        permissions.add("comment:read");
                        break;
                }
            }

            // 去除重复权限
            permissions = permissions.stream().distinct().collect(java.util.stream.Collectors.toList());

            log.debug("用户权限查询完成：userId={}, permissions={}", userId, permissions);

        } catch (Exception e) {
            log.error("查询用户权限失败：userId={}, error={}", userId, e.getMessage(), e);
            // 返回基础权限
            permissions.add("user:read");
            permissions.add("article:read");
        }

        // 缓存权限信息
        redisUtils.set(cacheKey, permissions, SystemConstants.Cache.EXPIRE_DAY);

        return permissions;
    }

    // ==================== 登录相关辅助方法 ====================

    /**
     * 更新登录信息
     * 
     * @param userId  用户ID
     * @param loginIp 登录IP
     * @return 更新结果
     */
    @Override
    public Boolean updateLoginInfo(Long userId, String loginIp) {
        return lambdaUpdate()
                .eq(TbUser::getId, userId)
                .set(TbUser::getLastLoginTime, new Date())
                .set(TbUser::getLastLoginIp, loginIp)
                .setSql("login_count = login_count + 1")
                .update();
    }

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     * @param ip       登录IP
     * @param reason   失败原因
     */
    @Override
    public void recordLoginFailure(String username, String ip, String reason) {
        String key = RedisConstants.Auth.LOGIN_RETRY_LIMIT + username + ":" + ip;
        Integer failCount = redisUtils.get(key, Integer.class);
        failCount = failCount == null ? 1 : failCount + 1;

        // 记录失败次数，5分钟过期
        redisUtils.set(key, failCount, 5 * 60);

        log.warn("登录失败记录，用户名：{}，IP：{}，失败次数：{}，原因：{}", username, ip, failCount, reason);
    }

    /**
     * 检查登录限制
     * 
     * @param username 用户名
     * @param ip       IP地址
     * @return 是否被限制
     */
    @Override
    public Boolean isLoginRestricted(String username, String ip) {
        String key = RedisConstants.Auth.LOGIN_RETRY_LIMIT + username + ":" + ip;
        Integer failCount = redisUtils.get(key, Integer.class);

        // 如果失败次数超过5次，则限制登录
        return failCount != null && failCount >= 5;
    }

    // ==================== 数据转换方法 ====================

    /**
     * DO转VO
     * 
     * @param user 用户DO对象
     * @return 用户VO对象
     */
    @Override
    public UserVO convertToVO(TbUser user) {
        if (user == null) {
            return null;
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 脱敏处理
        if (StringUtils.isNotBlank(user.getPhone())) {
            userVO.setPhone(UserVO.getMaskedPhone(user.getPhone()));
        }
        if (StringUtils.isNotBlank(user.getEmail())) {
            userVO.setEmail(UserVO.getMaskedEmail(user.getEmail()));
        }

        // 设置默认头像
        if (StringUtils.isBlank(userVO.getAvatar())) {
            userVO.setAvatar(SystemConstants.User.DEFAULT_AVATAR);
        }

        return userVO;
    }

    /**
     * DO列表转VO列表
     * 
     * @param users 用户DO列表
     * @return 用户VO列表
     */
    @Override
    public List<UserVO> convertToVOList(List<TbUser> users) {
        if (ObjectUtils.isEmpty(users)) {
            return new ArrayList<>();
        }

        List<UserVO> userVOList = new ArrayList<>();
        for (TbUser user : users) {
            UserVO userVO = convertToVO(user);
            if (userVO != null) {
                userVOList.add(userVO);
            }
        }
        return userVOList;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证注册参数
     */
    private void validateRegisterParams(RegisterVO registerVO) {
        if (!registerVO.isPasswordConfirmed()) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "两次密码输入不一致");
        }

        if (!registerVO.hasAgreedTerms()) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "请同意用户协议");
        }

        if (!isPasswordStrong(registerVO.getPassword())) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, SystemConstants.Message.PASSWORD_WEAK);
        }
    }

    /**
     * 检查用户是否存在
     */
    private void checkUserExists(RegisterVO registerVO) {
        if (existsByUsername(registerVO.getUsername())) {
            throw new BusinessException(ResultEnum.USER_EXISTS, SystemConstants.Message.USERNAME_EXISTS);
        }

        if (existsByEmail(registerVO.getEmail())) {
            throw new BusinessException(ResultEnum.USER_EXISTS, SystemConstants.Message.EMAIL_EXISTS);
        }

        if (StringUtils.isNotBlank(registerVO.getPhone()) && existsByPhone(registerVO.getPhone())) {
            throw new BusinessException(ResultEnum.USER_EXISTS, "手机号已存在");
        }
    }

    /**
     * 验证注册验证码
     */
    private void validateRegisterCodes(RegisterVO registerVO) {
        // 验证图片验证码
        validateCaptcha(registerVO.getCaptcha(), registerVO.getCaptchaUuid());

        // 验证邮箱验证码
        String emailCodeKey = RedisConstants.Auth.EMAIL_CODE + registerVO.getEmail();
        String cachedEmailCode = redisUtils.get(emailCodeKey, String.class);
        if (!registerVO.getEmailCode().equals(cachedEmailCode)) {
            throw new BusinessException(ResultEnum.CAPTCHA_ERROR, "邮箱验证码错误或已过期");
        }
    }

    /**
     * 构建用户对象
     */
    private TbUser buildUserFromRegister(RegisterVO registerVO) {
        TbUser user = new TbUser();
        user.setUsername(registerVO.getUsername());
        user.setPassword(CryptUtils.encryptPasswordWithSalt(registerVO.getPassword()));
        user.setEmail(registerVO.getEmail());
        user.setPhone(registerVO.getPhone());
        user.setNickname(StringUtils.isNotBlank(registerVO.getNickname()) ? registerVO.getNickname()
                : SystemConstants.User.DEFAULT_NICKNAME_PREFIX + System.currentTimeMillis());
        user.setAvatar(SystemConstants.User.DEFAULT_AVATAR);
        user.setStatus(SystemConstants.User.STATUS_NORMAL);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setLoginCount(0);

        return user;
    }

    /**
     * 清除注册验证码
     */
    private void clearRegisterCodes(RegisterVO registerVO) {
        // 清除图片验证码
        if (StringUtils.isNotBlank(registerVO.getCaptchaUuid())) {
            redisUtils.delete(RedisConstants.Auth.CAPTCHA + registerVO.getCaptchaUuid());
        }

        // 清除邮箱验证码
        redisUtils.delete(RedisConstants.Auth.EMAIL_CODE + registerVO.getEmail());
    }

    /**
     * 记录注册日志
     */
    private void recordRegisterLog(TbUser user, String clientIp) {
        // TODO: 记录用户注册日志到访问日志表
        log.info("用户注册成功，用户ID：{}，用户名：{}，注册IP：{}", user.getId(), user.getUsername(), clientIp);
    }

    /**
     * 验证登录参数
     */
    private void validateLoginParams(LoginVO loginVO) {
        if (StringUtils.isBlank(loginVO.getUsername()) || StringUtils.isBlank(loginVO.getPassword())) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "用户名和密码不能为空");
        }
    }

    /**
     * 检查登录限制
     */
    private void checkLoginRestriction(String username, String ip) {
        if (isLoginRestricted(username, ip)) {
            throw new BusinessException(ResultEnum.USER_LOGIN_RESTRICTED, "登录失败次数过多，请稍后再试");
        }
    }

    /**
     * 验证图片验证码
     */
    private void validateCaptcha(String captcha, String captchaUuid) {
        if (StringUtils.isBlank(captcha) || StringUtils.isBlank(captchaUuid)) {
            throw new BusinessException(ResultEnum.CAPTCHA_ERROR, "验证码不能为空");
        }

        String cacheKey = RedisConstants.Auth.CAPTCHA + captchaUuid;
        String cachedCaptcha = redisUtils.get(cacheKey, String.class);

        if (!captcha.equalsIgnoreCase(cachedCaptcha)) {
            throw new BusinessException(ResultEnum.CAPTCHA_ERROR, "验证码错误或已过期");
        }

        // 验证成功后删除验证码
        redisUtils.delete(cacheKey);
    }

    /**
     * 从数据库获取用户信息
     */
    private TbUser getUserByUsernameFromDB(String username) {
        return lambdaQuery()
                .eq(TbUser::getUsername, username)
                .eq(TbUser::getIsDelete, 0)
                .one();
    }

    /**
     * 验证用户状态
     */
    private void validateUserStatus(TbUser user) {
        if (SystemConstants.User.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ResultEnum.USER_DISABLED, "账号已被禁用");
        }
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(String inputPassword, String encryptedPassword) {
        return CryptUtils.verifyPassword(inputPassword, encryptedPassword);
    }

    /**
     * 获取用户角色字符串
     */
    private String getUserRoleString(Long userId) {
        List<String> roles = getUserRoles(userId);
        return String.join(",", roles);
    }

    /**
     * 缓存用户信息和登录状态
     */
    private void cacheUserInfo(TbUser user, String token, Boolean rememberMe) {
        // 缓存用户信息
        String userInfoKey = RedisConstants.User.USER_INFO + user.getId();
        UserVO userVO = convertToVO(user);
        redisUtils.set(userInfoKey, userVO, SystemConstants.Cache.EXPIRE_DAY);

        // 缓存用户会话
        String sessionKey = RedisConstants.User.USER_SESSION + user.getId();
        Map<String, Object> session = new HashMap<>();
        session.put("token", token);
        session.put("loginTime", System.currentTimeMillis());
        session.put("rememberMe", rememberMe);

        int expireTime = Boolean.TRUE.equals(rememberMe) ? SystemConstants.Cache.EXPIRE_WEEK
                : SystemConstants.Cache.TOKEN_EXPIRE;
        redisUtils.set(sessionKey, session, expireTime);

        // 设置用户在线状态
        String onlineKey = RedisConstants.User.USER_ONLINE + user.getId();
        redisUtils.set(onlineKey, "1", expireTime);
    }

    /**
     * 获取令牌过期时间
     */
    private long getTokenExpiresIn(Boolean rememberMe) {
        return Boolean.TRUE.equals(rememberMe) ? SystemConstants.Cache.EXPIRE_WEEK : SystemConstants.Cache.TOKEN_EXPIRE;
    }

    /**
     * 清除登录失败记录
     */
    private void clearLoginFailureRecord(String username) {
        String pattern = RedisConstants.Auth.LOGIN_RETRY_LIMIT + username + ":*";
        redisUtils.deletePattern(pattern);
    }

    /**
     * 验证更新参数
     */
    private void validateUpdateParams(UserUpdateVO userUpdateVO) {
        if (userUpdateVO.isChangePassword() && !userUpdateVO.isNewPasswordConfirmed()) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, "两次新密码输入不一致");
        }

        if (userUpdateVO.isChangePassword() && !isPasswordStrong(userUpdateVO.getNewPassword())) {
            throw new BusinessException(ResultEnum.PARAM_ERROR, SystemConstants.Message.PASSWORD_WEAK);
        }
    }

    /**
     * 检查邮箱和手机号唯一性
     */
    private void checkEmailAndPhoneUnique(Long userId, String email, String phone) {
        if (StringUtils.isNotBlank(email)) {
            long emailCount = lambdaQuery()
                    .eq(TbUser::getEmail, email)
                    .ne(TbUser::getId, userId)
                    .eq(TbUser::getIsDelete, 0)
                    .count();
            if (emailCount > 0) {
                throw new BusinessException(ResultEnum.USER_EXISTS, "邮箱已被其他用户使用");
            }
        }

        if (StringUtils.isNotBlank(phone)) {
            long phoneCount = lambdaQuery()
                    .eq(TbUser::getPhone, phone)
                    .ne(TbUser::getId, userId)
                    .eq(TbUser::getIsDelete, 0)
                    .count();
            if (phoneCount > 0) {
                throw new BusinessException(ResultEnum.USER_EXISTS, "手机号已被其他用户使用");
            }
        }
    }

    /**
     * 更新用户字段
     */
    private void updateUserFields(TbUser user, UserUpdateVO userUpdateVO) {
        if (StringUtils.isNotBlank(userUpdateVO.getNickname())) {
            user.setNickname(userUpdateVO.getNickname());
        }
        if (StringUtils.isNotBlank(userUpdateVO.getEmail())) {
            user.setEmail(userUpdateVO.getEmail());
        }
        if (StringUtils.isNotBlank(userUpdateVO.getPhone())) {
            user.setPhone(userUpdateVO.getPhone());
        }
        if (StringUtils.isNotBlank(userUpdateVO.getAvatar())) {
            user.setAvatar(userUpdateVO.getAvatar());
        }
        if (StringUtils.isNotBlank(userUpdateVO.getIntroduction())) {
            user.setIntroduction(userUpdateVO.getIntroduction());
        }
        if (StringUtils.isNotBlank(userUpdateVO.getWebsite())) {
            user.setWebsite(userUpdateVO.getWebsite());
        }
    }

    /**
     * 验证密码强度
     */
    private boolean isPasswordStrong(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        // 使用正则表达式验证密码强度
        return password.matches(SystemConstants.Regex.PASSWORD);
    }

    /**
     * 清除用户缓存
     */
    private void clearUserCache(Long userId) {
        redisUtils.delete(RedisConstants.User.USER_INFO + userId);
        redisUtils.delete(RedisConstants.User.USER_PERMISSION + userId);
        redisUtils.delete(RedisConstants.User.USER_ROLE + userId);
    }

    /**
     * 清除用户登录状态
     */
    private void clearUserLoginStatus(Long userId) {
        redisUtils.delete(RedisConstants.User.USER_SESSION + userId);
        redisUtils.delete(RedisConstants.User.USER_ONLINE + userId);
        redisUtils.delete(RedisConstants.User.USER_LOGIN_STATUS + userId);
    }
}
