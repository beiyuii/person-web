package pw.pj.common.shiro;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import pw.pj.common.result.ApiResponse;
import pw.pj.common.result.ResultEnum;
import pw.pj.common.utils.JwtTokenUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证过滤器
 * 拦截HTTP请求，验证JWT令牌
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-01
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends BasicHttpAuthenticationFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 识别请求头中的JWT令牌
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 认证令牌
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authHeader = httpRequest.getHeader(jwtTokenUtil.getTokenHeader());

            if (StringUtils.hasText(authHeader)) {
                String token = jwtTokenUtil.getTokenFromHeader(authHeader);
                if (StringUtils.hasText(token)) {
                    log.debug("从请求头中获取到JWT令牌");
                    return new JwtToken(token);
                }
            }

            log.debug("请求头中未找到有效的JWT令牌");
            return null;
        } catch (Exception e) {
            log.error("创建JWT令牌时发生异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断是否已经登录
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否已登录
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader(jwtTokenUtil.getTokenHeader());

        boolean hasToken = StringUtils.hasText(authHeader) &&
                authHeader.startsWith(jwtTokenUtil.getTokenPrefix());

        if (hasToken) {
            log.debug("检测到登录尝试");
        }

        return hasToken;
    }

    /**
     * 执行登录验证
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否登录成功
     * @throws Exception 认证异常
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        try {
            AuthenticationToken token = createToken(request, response);
            if (token == null) {
                log.warn("无法创建认证令牌");
                return false;
            }

            Subject subject = getSubject(request, response);
            subject.login(token);

            log.info("JWT令牌验证成功");
            return true;
        } catch (AuthenticationException e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 处理访问拒绝
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否允许访问
     * @throws Exception 处理异常
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.warn("访问被拒绝: {}", httpRequest.getRequestURI());

        // 检查是否是预检请求
        if (isPreflightRequest(httpRequest)) {
            log.debug("处理预检请求");
            return handlePreflightRequest(httpResponse);
        }

        // 尝试登录
        if (isLoginAttempt(request, response)) {
            try {
                boolean loginSuccess = executeLogin(request, response);
                if (loginSuccess) {
                    return true;
                }
            } catch (Exception e) {
                log.error("登录验证过程中发生异常: {}", e.getMessage());
            }
        }

        // 返回认证失败响应
        return sendAuthenticationFailureResponse(httpResponse);
    }

    /**
     * 处理登录失败
     * 
     * @param token    认证令牌
     * @param e        认证异常
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否继续处理
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
            ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.error("登录失败: {}", e.getMessage());

        try {
            return sendAuthenticationFailureResponse(httpResponse);
        } catch (IOException ioException) {
            log.error("发送认证失败响应时发生异常: {}", ioException.getMessage());
            return false;
        }
    }

    /**
     * 判断是否是预检请求
     * 
     * @param request HTTP请求
     * @return 是否是预检请求
     */
    private boolean isPreflightRequest(HttpServletRequest request) {
        return RequestMethod.OPTIONS.name().equals(request.getMethod());
    }

    /**
     * 处理预检请求
     * 
     * @param response HTTP响应
     * @return 是否处理成功
     */
    private boolean handlePreflightRequest(HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");
        return false;
    }

    /**
     * 发送认证失败响应
     * 
     * @param response HTTP响应
     * @return 是否发送成功
     * @throws IOException IO异常
     */
    private boolean sendAuthenticationFailureResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 设置CORS头部
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        ApiResponse<?> apiResponse = ApiResponse.error(ResultEnum.UNAUTHORIZED);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSON.toJSONString(apiResponse));
            writer.flush();
        }

        return false;
    }

    /**
     * 处理登录成功
     * 
     * @param token    认证令牌
     * @param subject  主体
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否处理成功
     * @throws Exception 处理异常
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
            ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        log.info("用户[{}]登录成功，访问路径: {}",
                subject.getPrincipal(), httpRequest.getRequestURI());

        return true;
    }

    /**
     * 应用前置处理
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 是否继续处理
     * @throws Exception 处理异常
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 设置CORS头部
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 处理预检请求
        if (isPreflightRequest(httpRequest)) {
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }

    /**
     * 应用后置处理
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @throws Exception 处理异常
     */
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) throws Exception {
        // 可以在此处添加后置处理逻辑
        super.postHandle(request, response);
    }
}