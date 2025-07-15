package pw.pj.common.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.pj.common.utils.JwtTokenUtil;
import pw.pj.service.TbUserService;
import pw.pj.POJO.DO.TbUser;
import pw.pj.POJO.VO.UserVO;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * 自定义用户认证和授权Realm
 * 基于JWT令牌进行用户认证和权限验证
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-01
 */
@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TbUserService tbUserService;

    /**
     * 限制此Realm仅支持JwtToken
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 用户授权（获取用户权限信息）
     * 
     * @param principalCollection 用户主体集合
     * @return 授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.debug("开始进行用户授权验证");

        try {
            // 获取用户信息
            String username = (String) principalCollection.getPrimaryPrincipal();
            log.info("为用户[{}]加载权限信息", username);

            // 查询用户信息
            UserVO user = tbUserService.getUserByUsername(username);

            if (user == null) {
                log.warn("用户[{}]不存在，授权失败", username);
                return null;
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                log.warn("用户[{}]状态异常[{}]，授权失败", username, user.getStatus());
                return null;
            }

            // 创建授权信息
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

            // 获取用户角色
            List<String> roles = tbUserService.getUserRoles(user.getId());
            if (roles != null && !roles.isEmpty()) {
                authorizationInfo.addRoles(roles);
                log.debug("用户[{}]角色：{}", username, roles);
            }

            // 获取用户权限
            List<String> permissions = tbUserService.getUserPermissions(user.getId());
            if (permissions != null && !permissions.isEmpty()) {
                authorizationInfo.addStringPermissions(permissions);
                log.debug("用户[{}]权限：{}", username, permissions);
            }

            log.info("用户[{}]权限信息加载完成，角色数：{}，权限数：{}",
                    username, roles != null ? roles.size() : 0,
                    permissions != null ? permissions.size() : 0);

            return authorizationInfo;

        } catch (Exception e) {
            log.error("用户授权过程中发生异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 用户认证（验证用户身份）
     * 
     * @param authenticationToken 认证令牌
     * @return 认证信息
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        log.debug("开始进行用户认证验证");

        try {
            // 获取JWT令牌
            String token = (String) authenticationToken.getCredentials();
            log.debug("开始验证JWT令牌");

            // 验证令牌有效性
            if (!jwtTokenUtil.validateToken(token)) {
                log.warn("JWT令牌验证失败");
                throw new AuthenticationException("JWT令牌无效");
            }

            // 从令牌中获取用户信息
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long userId = jwtTokenUtil.getUserIdFromToken(token);

            log.info("从JWT令牌中解析用户信息: username={}, userId={}", username, userId);

            // 查询用户信息
            UserVO userVO = tbUserService.getUserByUsername(username);

            if (userVO == null) {
                log.warn("用户[{}]不存在，认证失败", username);
                throw new UnknownAccountException("用户不存在");
            }

            // 检查用户状态
            if (userVO.getStatus() != 1) {
                log.warn("用户[{}]状态异常[{}]，认证失败", username, userVO.getStatus());
                throw new LockedAccountException("用户账号已被禁用");
            }

            // 验证用户ID是否匹配
            if (!userVO.getId().equals(userId)) {
                log.warn("用户ID不匹配，令牌可能被篡改: tokenUserId={}, dbUserId={}", userId, userVO.getId());
                throw new AuthenticationException("用户信息不匹配");
            }

            log.info("用户[{}]认证验证成功", username);

            // 返回认证信息
            return new SimpleAuthenticationInfo(username, token, getName());

        } catch (AuthenticationException e) {
            log.error("用户认证失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户认证过程中发生异常: {}", e.getMessage(), e);
            throw new AuthenticationException("认证过程中发生系统异常", e);
        }
    }

    /**
     * 清除指定用户的授权缓存
     * 
     * @param username 用户名
     */
    public void clearAuthorizationCache(String username) {
        try {
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            PrincipalCollection principalCollection = new SimplePrincipalCollection(username, getName());
            clearCachedAuthorizationInfo(principalCollection);
            log.info("已清除用户[{}]的授权缓存", username);
        } catch (Exception e) {
            log.error("清除用户[{}]授权缓存失败: {}", username, e.getMessage());
        }
    }

    /**
     * 清除指定用户的认证缓存
     * 
     * @param username 用户名
     */
    public void clearAuthenticationCache(String username) {
        try {
            PrincipalCollection principalCollection = new SimplePrincipalCollection(username, getName());
            clearCachedAuthenticationInfo(principalCollection);
            log.info("已清除用户[{}]的认证缓存", username);
        } catch (Exception e) {
            log.error("清除用户[{}]认证缓存失败: {}", username, e.getMessage());
        }
    }

    /**
     * 清除指定用户的所有缓存
     * 
     * @param username 用户名
     */
    public void clearAllCache(String username) {
        clearAuthenticationCache(username);
        clearAuthorizationCache(username);
        log.info("已清除用户[{}]的所有缓存", username);
    }
}