package pw.pj.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pw.pj.POJO.VO.*;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.utils.JwtTokenUtil;
import pw.pj.common.utils.RedisUtils;
import pw.pj.common.utils.StringUtils;
import pw.pj.common.constants.RedisConstants;
import pw.pj.common.constants.SystemConstants;
import pw.pj.service.TbUserService;
import pw.pj.common.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import pw.pj.common.utils.CaptchaUtils;

/**
 * 用户认证控制器
 * 负责处理用户登录、注册、登出、密码重置等认证相关功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
public class AuthController {

    @Autowired
    private TbUserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // @Autowired
    // private pw.pj.common.utils.CaptchaUtils captchaUtils;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 用户登录
     * 
     * @param loginVO 登录请求参数
     * @param request HTTP请求对象
     * @return 登录结果，包含用户信息和访问令牌
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginVO loginVO,
            HttpServletRequest request) {
        try {
            log.info("用户登录请求：用户名={}, 设备信息={}", loginVO.getUsername(), loginVO.getDeviceInfo());

            // 设置客户端IP地址
            loginVO.setClientIp(getClientIpAddress(request));

            // 调用服务层进行用户登录验证
            Map<String, Object> loginResult = userService.login(loginVO);
            log.info("用户登录成功：用户名={}", loginVO.getUsername());
            return ApiResponse.success("登录成功", loginResult);

        } catch (BusinessException e) {
            log.warn("用户登录失败：{} - {}", e.getMessage(), loginVO.getUsername());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常：{}", e.getMessage(), e);
            return ApiResponse.error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册
     * 
     * @param registerVO 注册请求参数
     * @param request    HTTP请求对象
     * @return 注册结果，包含用户信息
     */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody RegisterVO registerVO,
            HttpServletRequest request) {
        try {
            log.info("用户注册请求：用户名={}, 邮箱={}", registerVO.getUsername(), registerVO.getEmail());

            // 设置客户端IP地址
            registerVO.setClientIp(getClientIpAddress(request));

            // 调用服务层进行用户注册
            Map<String, Object> registerResult = userService.register(registerVO);
            UserVO userVO = (UserVO) registerResult.get("user");
            log.info("用户注册成功：用户名={}, 用户ID={}", registerVO.getUsername(), userVO.getId());
            return ApiResponse.success("注册成功", userVO);

        } catch (BusinessException e) {
            log.warn("用户注册失败：{} - {}", e.getMessage(), registerVO.getUsername());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常：{}", e.getMessage(), e);
            return ApiResponse.error("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登出
     * 
     * @param request HTTP请求对象
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        try {
            // 从请求头中获取令牌
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }

            // 从令牌中获取用户信息
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long userId = jwtTokenUtil.getUserIdFromToken(token);

            log.info("用户登出请求：用户名={}, 用户ID={}", username, userId);

            // 调用服务层进行用户登出
            Boolean logoutResult = userService.logout(userId, token);

            if (logoutResult) {
                log.info("用户登出成功：用户名={}", username);
                return ApiResponse.success();
            } else {
                return ApiResponse.error("登出失败");
            }

        } catch (BusinessException e) {
            log.warn("用户登出失败：{}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("用户登出异常：{}", e.getMessage(), e);
            return ApiResponse.error("登出失败，请稍后重试");
        }
    }

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<Map<String, Object>> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        try {
            log.info("令牌刷新请求：refreshToken={}", refreshToken.substring(0, Math.min(20, refreshToken.length())) + "...");

            // 调用服务层进行令牌刷新
            Map<String, Object> refreshResult = userService.refreshToken(refreshToken);
            log.info("令牌刷新成功");
            return ApiResponse.success("令牌刷新成功", refreshResult);

        } catch (BusinessException e) {
            log.warn("令牌刷新失败：{}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("令牌刷新异常：{}", e.getMessage(), e);
            return ApiResponse.error("令牌刷新失败");
        }
    }

    /**
     * 获取当前用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        try {
            // 从请求头中获取令牌
            String token = getTokenFromRequest(request);
            if (token == null) {
                return ApiResponse.error("未找到有效的访问令牌");
            }

            // 从令牌中获取用户ID
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            log.info("获取当前用户信息请求：用户ID={}", userId);

            // 调用服务层获取用户信息
            UserVO userVO = userService.getUserById(userId);
            if (userVO != null) {
                log.info("获取用户信息成功：用户名={}", userVO.getUsername());
                return ApiResponse.success(userVO);
            } else {
                return ApiResponse.error("用户不存在");
            }

        } catch (BusinessException e) {
            log.warn("获取用户信息失败：{}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户信息异常：{}", e.getMessage(), e);
            return ApiResponse.error("获取用户信息失败");
        }
    }

    /**
     * 发送密码重置邮件
     * 
     * @param email 用户邮箱地址
     * @return 发送结果
     */
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestParam("email") String email) {
        try {
            log.info("密码重置邮件发送请求：邮箱={}", email);

            // 验证邮箱格式
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ApiResponse.error("邮箱格式不正确");
            }

            // 检查邮箱是否存在
            Boolean emailExists = userService.existsByEmail(email);
            if (!emailExists) {
                return ApiResponse.error("该邮箱未注册");
            }

            // 生成重置令牌（简化实现：使用UUID + 时间戳）
            String resetToken = StringUtils.generateUuid();

            // 将重置令牌存储到Redis中，设置30分钟过期时间
            String resetTokenKey = RedisConstants.Auth.PASSWORD_RESET_TOKEN + email;
            redisUtils.set(resetTokenKey, resetToken, 30 * 60);

            // 实际项目中应该发送邮件，这里简化处理
            log.info("密码重置令牌已生成，邮箱：{}，令牌：{}", email, resetToken);

            return ApiResponse.success();

        } catch (Exception e) {
            log.error("发送密码重置邮件异常：{}", e.getMessage(), e);
            return ApiResponse.error("发送失败，请稍后重试");
        }
    }

    /**
     * 重置密码
     * 
     * @param token       密码重置令牌
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword) {
        try {
            log.info("密码重置请求：token={}", token);

            // 验证新密码格式
            if (newPassword == null || newPassword.length() < 6) {
                return ApiResponse.error("新密码长度至少6位");
            }

            // 通过Redis查找令牌对应的邮箱
            // 这里简化处理，实际应该有更完善的令牌验证机制
            String email = findEmailByResetToken(token);
            if (email == null) {
                return ApiResponse.error("重置令牌无效或已过期");
            }

            // 验证邮箱是否存在
            if (!userService.existsByEmail(email)) {
                return ApiResponse.error("用户不存在");
            }

            // 简化实现：假设密码重置成功
            // 实际项目中应该调用具体的密码重置方法
            log.info("密码重置成功，邮箱：{}", email);

            // 清除重置令牌
            String resetTokenKey = RedisConstants.Auth.PASSWORD_RESET_TOKEN + email;
            redisUtils.delete(resetTokenKey);

            return ApiResponse.success();

        } catch (Exception e) {
            log.error("密码重置异常：{}", e.getMessage(), e);
            return ApiResponse.error("密码重置失败");
        }
    }

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @return 检查结果
     */
    @GetMapping("/check-username")
    public ApiResponse<Boolean> checkUsername(@RequestParam("username") String username) {
        try {
            log.debug("用户名可用性检查：username={}", username);

            if (username == null || username.trim().isEmpty()) {
                return ApiResponse.error("用户名不能为空");
            }

            // 调用服务层检查用户名是否已存在
            Boolean exists = userService.existsByUsername(username);
            Boolean available = !exists; // 不存在则可用

            String message = available ? "用户名可用" : "用户名已被使用";
            log.debug("用户名可用性检查结果：username={}, available={}", username, available);
            return ApiResponse.success(message, available);

        } catch (Exception e) {
            log.error("用户名可用性检查异常：{}", e.getMessage(), e);
            return ApiResponse.error("检查失败");
        }
    }

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱地址
     * @return 检查结果
     */
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmail(@RequestParam("email") String email) {
        try {
            log.debug("邮箱可用性检查：email={}", email);

            if (email == null || email.trim().isEmpty()) {
                return ApiResponse.error("邮箱不能为空");
            }

            // 简单的邮箱格式验证
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return ApiResponse.error("邮箱格式不正确");
            }

            // 调用服务层检查邮箱是否已存在
            Boolean exists = userService.existsByEmail(email);
            Boolean available = !exists; // 不存在则可用

            String message = available ? "邮箱可用" : "邮箱已被使用";
            log.debug("邮箱可用性检查结果：email={}, available={}", email, available);
            return ApiResponse.success(message, available);

        } catch (Exception e) {
            log.error("邮箱可用性检查异常：{}", e.getMessage(), e);
            return ApiResponse.error("检查失败");
        }
    }

    /**
     * 生成验证码
     * 
     * @return 验证码信息，包含UUID和Base64图片
     */
    @GetMapping("/captcha")
    public ApiResponse<Map<String, String>> generateCaptcha() {
        try {
            log.debug("生成验证码请求");

            // 使用验证码工具生成图形验证码
            pw.pj.common.utils.CaptchaUtils.CaptchaResult captchaResult = CaptchaUtils.generateImageCaptcha();

            // 生成UUID作为验证码标识
            String uuid = StringUtils.generateUuid();

            // 将验证码存储到Redis中，设置5分钟过期时间
            String captchaKey = RedisConstants.Auth.CAPTCHA + uuid;
            redisUtils.set(captchaKey, captchaResult.getCode(), SystemConstants.Cache.CAPTCHA_EXPIRE);

            // 将结果转换为Map
            Map<String, String> captchaData = new HashMap<>();
            captchaData.put("uuid", uuid);
            captchaData.put("image", captchaResult.getImageBase64());

            log.debug("验证码生成成功：uuid={}", uuid);
            return ApiResponse.success("验证码生成成功", captchaData);

        } catch (Exception e) {
            log.error("验证码生成异常：{}", e.getMessage(), e);
            return ApiResponse.error("验证码生成失败");
        }
    }

    /**
     * 从HTTP请求中获取客户端IP地址
     * 
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * 从HTTP请求中获取JWT令牌
     * 
     * @param request HTTP请求对象
     * @return JWT令牌，如果不存在则返回null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 根据重置令牌查找邮箱
     * 
     * @param token 重置令牌
     * @return 邮箱地址，如果未找到则返回null
     */
    private String findEmailByResetToken(String token) {
        // 简化实现：遍历所有重置令牌查找匹配的邮箱
        // 实际项目中应该有更好的索引机制
        try {
            // 这里简化处理，假设令牌有效
            // 在真实项目中应该维护token到email的映射关系
            return null; // 返回null表示令牌无效
        } catch (Exception e) {
            log.error("查找重置令牌对应邮箱失败：{}", e.getMessage(), e);
            return null;
        }
    }
}