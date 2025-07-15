package pw.pj.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌工具类
 * 提供JWT令牌的生成、解析和验证功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-01
 */
@Slf4j
@Component
public class JwtTokenUtil {

    /**
     * JWT签名密钥
     */
    @Value("${jwt.secret:person-web-blog-jwt-secret-key}")
    private String secret;

    /**
     * JWT令牌过期时间（秒）
     */
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * JWT发行者
     */
    @Value("${jwt.issuer:person-web-blog}")
    private String issuer;

    /**
     * 令牌头部信息
     */
    private static final String TOKEN_HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 用户ID声明
     */
    private static final String CLAIM_USER_ID = "userId";

    /**
     * 用户名声明
     */
    private static final String CLAIM_USERNAME = "username";

    /**
     * 用户角色声明
     */
    private static final String CLAIM_ROLES = "roles";

    /**
     * 生成JWT令牌
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param roles    用户角色
     * @return JWT令牌字符串
     */
    public String generateToken(Long userId, String username, String roles) {
        try {
            // 当前时间
            Date now = new Date();
            // 过期时间
            Date expiryDate = new Date(now.getTime() + expiration * 1000);

            // 创建JWT构建器
            JWTCreator.Builder builder = JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(now)
                    .withExpiresAt(expiryDate)
                    .withClaim(CLAIM_USER_ID, userId)
                    .withClaim(CLAIM_USERNAME, username)
                    .withClaim(CLAIM_ROLES, roles);

            // 使用HS256算法进行签名
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = builder.sign(algorithm);

            log.info("为用户[{}]生成JWT令牌成功", username);
            return token;

        } catch (JWTCreationException e) {
            log.error("生成JWT令牌失败: {}", e.getMessage());
            throw new RuntimeException("JWT令牌生成失败", e);
        }
    }

    /**
     * 验证JWT令牌
     * 
     * @param token JWT令牌字符串
     * @return 验证结果
     */
    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();

            DecodedJWT jwt = verifier.verify(token);

            // 检查令牌是否过期
            Date expiresAt = jwt.getExpiresAt();
            if (expiresAt.before(new Date())) {
                log.warn("JWT令牌已过期");
                return false;
            }

            log.debug("JWT令牌验证成功");
            return true;

        } catch (JWTVerificationException e) {
            log.error("JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     * 
     * @param token JWT令牌字符串
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(CLAIM_USER_ID).asLong();
        } catch (JWTDecodeException e) {
            log.error("从令牌中获取用户ID失败: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 从令牌中获取用户名
     * 
     * @param token JWT令牌字符串
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(CLAIM_USERNAME).asString();
        } catch (JWTDecodeException e) {
            log.error("从令牌中获取用户名失败: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 从令牌中获取用户角色
     * 
     * @param token JWT令牌字符串
     * @return 用户角色
     */
    public String getRolesFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(CLAIM_ROLES).asString();
        } catch (JWTDecodeException e) {
            log.error("从令牌中获取用户角色失败: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 获取令牌过期时间
     * 
     * @param token JWT令牌字符串
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getExpiresAt();
        } catch (JWTDecodeException e) {
            log.error("从令牌中获取过期时间失败: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 判断令牌是否过期
     * 
     * @param token JWT令牌字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("检查令牌是否过期失败: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 刷新令牌
     * 
     * @param token 原始令牌
     * @return 新的令牌
     */
    public String refreshToken(String token) {
        try {
            if (isTokenExpired(token)) {
                throw new RuntimeException("令牌已过期，无法刷新");
            }

            Long userId = getUserIdFromToken(token);
            String username = getUsernameFromToken(token);
            String roles = getRolesFromToken(token);

            log.info("刷新用户[{}]的JWT令牌", username);
            return generateToken(userId, username, roles);

        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new RuntimeException("令牌刷新失败", e);
        }
    }

    /**
     * 从HTTP请求头中获取令牌
     * 
     * @param authHeader 认证头部信息
     * @return 清洁的令牌字符串
     */
    public String getTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 创建完整的令牌字符串（包含Bearer前缀）
     * 
     * @param token 纯令牌字符串
     * @return 完整的令牌字符串
     */
    public String createFullToken(String token) {
        return TOKEN_PREFIX + token;
    }

    /**
     * 获取令牌的所有声明信息
     * 
     * @param token JWT令牌字符串
     * @return 声明信息Map
     */
    public Map<String, Object> getClaimsFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Object> claims = new HashMap<>();

            claims.put(CLAIM_USER_ID, jwt.getClaim(CLAIM_USER_ID).asLong());
            claims.put(CLAIM_USERNAME, jwt.getClaim(CLAIM_USERNAME).asString());
            claims.put(CLAIM_ROLES, jwt.getClaim(CLAIM_ROLES).asString());
            claims.put("issuer", jwt.getIssuer());
            claims.put("issuedAt", jwt.getIssuedAt());
            claims.put("expiresAt", jwt.getExpiresAt());

            return claims;
        } catch (JWTDecodeException e) {
            log.error("获取令牌声明信息失败: {}", e.getMessage());
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 获取令牌头部信息名称
     * 
     * @return 令牌头部信息名称
     */
    public String getTokenHeader() {
        return TOKEN_HEADER;
    }

    /**
     * 获取令牌前缀
     * 
     * @return 令牌前缀
     */
    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }
}