package pw.pj.service;

import pw.pj.POJO.DO.TbUser;
import pw.pj.POJO.VO.LoginVO;
import pw.pj.POJO.VO.RegisterVO;
import pw.pj.POJO.VO.UserVO;
import pw.pj.POJO.VO.UserUpdateVO;
import pw.pj.common.result.PageResult;
import pw.pj.POJO.VO.PageQueryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 提供用户注册、登录、资料管理、权限验证等核心功能
 * 
 * @author PersonWeb开发团队
 * @description 针对表【tb_user(用户表)】的数据库操作Service
 * @createDate 2025-07-01 16:44:02
 * @version 1.0
 * @since 2024-02-02
 */
public interface TbUserService extends IService<TbUser> {

    // ==================== 用户认证相关 ====================

    /**
     * 用户注册
     * 
     * @param registerVO 注册信息
     * @return 注册结果
     */
    Map<String, Object> register(RegisterVO registerVO);

    /**
     * 用户登录
     * 
     * @param loginVO 登录信息
     * @return 登录结果，包含用户信息和JWT令牌
     */
    Map<String, Object> login(LoginVO loginVO);

    /**
     * 用户登出
     * 
     * @param userId 用户ID
     * @param token  JWT令牌
     * @return 登出结果
     */
    Boolean logout(Long userId, String token);

    /**
     * 刷新JWT令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的JWT令牌
     */
    Map<String, Object> refreshToken(String refreshToken);

    // ==================== 用户信息管理 ====================

    /**
     * 根据用户ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息VO
     */
    UserVO getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息VO
     */
    UserVO getUserByUsername(String username);

    /**
     * 更新用户资料
     * 
     * @param userId       用户ID
     * @param userUpdateVO 更新信息
     * @return 更新结果
     */
    Boolean updateUserProfile(Long userId, UserUpdateVO userUpdateVO);

    /**
     * 修改用户密码
     * 
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    Boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码（管理员操作）
     * 
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    Boolean resetPassword(Long userId, String newPassword);

    /**
     * 更新用户头像
     * 
     * @param userId    用户ID
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    Boolean updateAvatar(Long userId, String avatarUrl);

    // ==================== 用户状态管理 ====================

    /**
     * 启用/禁用用户
     * 
     * @param userId 用户ID
     * @param status 状态：0-禁用，1-正常
     * @return 操作结果
     */
    Boolean updateUserStatus(Long userId, Integer status);

    /**
     * 检查用户是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    Boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    Boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     * 
     * @param phone 手机号
     * @return 是否存在
     */
    Boolean existsByPhone(String phone);

    // ==================== 用户查询和统计 ====================

    /**
     * 分页查询用户列表
     * 
     * @param pageQueryVO 分页查询条件
     * @return 分页结果
     */
    PageResult<UserVO> getUserList(PageQueryVO pageQueryVO);

    /**
     * 搜索用户
     * 
     * @param keyword     关键词
     * @param pageSize    每页大小
     * @param currentPage 当前页
     * @return 搜索结果
     */
    PageResult<UserVO> searchUsers(String keyword, Integer pageSize, Integer currentPage);

    /**
     * 获取用户统计信息
     * 
     * @param userId 用户ID
     * @return 统计信息
     */
    Map<String, Object> getUserStatistics(Long userId);

    // ==================== 权限验证相关 ====================

    /**
     * 验证用户权限
     * 
     * @param userId     用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    Boolean hasPermission(Long userId, String permission);

    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<String> getUserPermissions(Long userId);

    // ==================== 登录相关辅助方法 ====================

    /**
     * 更新登录信息
     * 
     * @param userId  用户ID
     * @param loginIp 登录IP
     * @return 更新结果
     */
    Boolean updateLoginInfo(Long userId, String loginIp);

    /**
     * 记录登录失败
     * 
     * @param username 用户名
     * @param ip       登录IP
     * @param reason   失败原因
     */
    void recordLoginFailure(String username, String ip, String reason);

    /**
     * 检查登录限制
     * 
     * @param username 用户名
     * @param ip       IP地址
     * @return 是否被限制
     */
    Boolean isLoginRestricted(String username, String ip);

    // ==================== 数据转换方法 ====================

    /**
     * DO转VO
     * 
     * @param user 用户DO对象
     * @return 用户VO对象
     */
    UserVO convertToVO(TbUser user);

    /**
     * DO列表转VO列表
     * 
     * @param users 用户DO列表
     * @return 用户VO列表
     */
    List<UserVO> convertToVOList(List<TbUser> users);
}
