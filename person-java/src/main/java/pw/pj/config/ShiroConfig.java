package pw.pj.config;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pw.pj.common.shiro.JwtAuthenticationFilter;
import pw.pj.common.shiro.UserRealm;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro安全框架配置类
 * 配置JWT认证和授权相关组件
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-01
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private UserRealm userRealm;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置Shiro过滤器工厂
     * 
     * @param securityManager 安全管理器
     * @return Shiro过滤器工厂Bean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 设置安全管理器
        factoryBean.setSecurityManager(securityManager);

        // 添加自定义过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", jwtAuthenticationFilter);
        factoryBean.setFilters(filterMap);

        // 配置过滤器链
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 公开接口 - 不需要认证
        filterChainDefinitionMap.put("/auth/login", "anon");
        filterChainDefinitionMap.put("/auth/register", "anon");
        filterChainDefinitionMap.put("/auth/captcha", "anon");
        filterChainDefinitionMap.put("/auth/forgot-password", "anon");
        filterChainDefinitionMap.put("/auth/reset-password", "anon");

        // 静态资源 - 不需要认证
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/robots.txt", "anon");

        // 健康检查 - 不需要认证
        filterChainDefinitionMap.put("/actuator/**", "anon");
        filterChainDefinitionMap.put("/health", "anon");

        // 公开文章接口 - 不需要认证
        filterChainDefinitionMap.put("/api/articles", "anon");
        filterChainDefinitionMap.put("/api/articles/*", "anon");
        filterChainDefinitionMap.put("/api/categories", "anon");
        filterChainDefinitionMap.put("/api/categories/*", "anon");
        filterChainDefinitionMap.put("/api/tags", "anon");
        filterChainDefinitionMap.put("/api/tags/*", "anon");

        // 文件上传下载 - 需要认证
        filterChainDefinitionMap.put("/api/files/upload", "jwt");
        filterChainDefinitionMap.put("/api/files/download/*", "anon");
        filterChainDefinitionMap.put("/api/files/view/*", "anon");

        // 评论相关 - 部分需要认证
        filterChainDefinitionMap.put("/api/comments", "anon");
        filterChainDefinitionMap.put("/api/comments/create", "jwt");
        filterChainDefinitionMap.put("/api/comments/update/*", "jwt");
        filterChainDefinitionMap.put("/api/comments/delete/*", "jwt");

        // 用户相关 - 需要认证
        filterChainDefinitionMap.put("/api/user/**", "jwt");

        // 管理员接口 - 需要认证和权限
        filterChainDefinitionMap.put("/api/admin/**", "jwt");

        // 系统配置 - 需要认证和权限
        filterChainDefinitionMap.put("/api/system/**", "jwt");

        // 测试接口 - 开发环境可以放开
        filterChainDefinitionMap.put("/test/**", "anon");

        // WebSocket连接 - 需要认证
        filterChainDefinitionMap.put("/websocket/**", "jwt");

        // Swagger接口文档 - 开发环境可以放开
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");

        // 其他所有接口 - 需要认证
        filterChainDefinitionMap.put("/**", "jwt");

        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return factoryBean;
    }

    /**
     * 配置安全管理器
     * 
     * @return 安全管理器Bean
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 设置自定义Realm
        securityManager.setRealm(userRealm);

        // 关闭Session存储，因为我们使用JWT无状态认证
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        // 禁用Session管理
        securityManager.setSessionManager(sessionManager());

        return securityManager;
    }

    /**
     * 配置Session管理器
     * 禁用Session功能，使用JWT无状态认证
     * 
     * @return Session管理器Bean
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 禁用Session
        sessionManager.setSessionIdCookieEnabled(false);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 配置Realm
     * 
     * @return Realm Bean
     */
    @Bean
    public Realm realm() {
        return userRealm;
    }
}