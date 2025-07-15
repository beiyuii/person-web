package pw.pj.common.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWT令牌类
 * 实现Shiro的AuthenticationToken接口，用于JWT认证
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-01
 */
public class JwtToken implements AuthenticationToken {

    private static final long serialVersionUID = 1L;

    /**
     * JWT令牌字符串
     */
    private String token;

    /**
     * 构造函数
     * 
     * @param token JWT令牌字符串
     */
    public JwtToken(String token) {
        this.token = token;
    }

    /**
     * 获取主体（用户标识）
     * 对于JWT，主体就是令牌本身
     * 
     * @return JWT令牌
     */
    @Override
    public Object getPrincipal() {
        return token;
    }

    /**
     * 获取凭证（认证凭据）
     * 对于JWT，凭证就是令牌本身
     * 
     * @return JWT令牌
     */
    @Override
    public Object getCredentials() {
        return token;
    }

    /**
     * 获取令牌字符串
     * 
     * @return JWT令牌字符串
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置令牌字符串
     * 
     * @param token JWT令牌字符串
     */
    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JwtToken{" +
                "token='" + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null") + '\'' +
                '}';
    }
}