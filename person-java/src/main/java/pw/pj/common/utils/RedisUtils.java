package pw.pj.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 设置缓存
     * 
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis设置缓存失败：key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置缓存并指定过期时间
     * 
     * @param key    键
     * @param value  值
     * @param expire 过期时间（秒）
     * @return 是否成功
     */
    public boolean set(String key, Object value, long expire) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("Redis设置缓存失败：key={}, expire={}, error={}", key, expire, e.getMessage());
            return false;
        }
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取缓存失败：key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 获取缓存并转换为指定类型
     * 
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   类型参数
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }

            if (clazz.isInstance(value)) {
                return (T) value;
            }

            // 如果是字符串类型，尝试转换
            if (value instanceof String) {
                String str = (String) value;

                if (clazz == String.class) {
                    return (T) str;
                } else if (clazz == Integer.class) {
                    return (T) Integer.valueOf(str);
                } else if (clazz == Long.class) {
                    return (T) Long.valueOf(str);
                } else if (clazz == Boolean.class) {
                    return (T) Boolean.valueOf(str);
                } else {
                    // 尝试JSON反序列化
                    return objectMapper.readValue(str, clazz);
                }
            }

            // 尝试使用ObjectMapper转换
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("Redis获取缓存并转换类型失败：key={}, class={}, error={}", key, clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 键
     * @return 是否成功
     */
    public boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis删除缓存失败：key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 根据模式删除缓存
     * 
     * @param pattern 模式（支持通配符*）
     * @return 删除的数量
     */
    public long deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long result = redisTemplate.delete(keys);
                return result != null ? result : 0;
            }
            return 0;
        } catch (Exception e) {
            log.error("Redis按模式删除缓存失败：pattern={}, error={}", pattern, e.getMessage());
            return 0;
        }
    }

    /**
     * 判断key是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis检查key存在性失败：key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     * 
     * @param key    键
     * @param expire 过期时间（秒）
     * @return 是否成功
     */
    public boolean expire(String key, long expire) {
        try {
            Boolean result = redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            return result != null && result;
        } catch (Exception e) {
            log.error("Redis设置过期时间失败：key={}, expire={}, error={}", key, expire, e.getMessage());
            return false;
        }
    }

    /**
     * 获取过期时间
     * 
     * @param key 键
     * @return 过期时间（秒），-1表示永不过期，-2表示key不存在
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败：key={}, error={}", key, e.getMessage());
            return -2;
        }
    }

    /**
     * 递增
     * 
     * @param key   键
     * @param delta 递增值
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Redis递增失败：key={}, delta={}, error={}", key, delta, e.getMessage());
            return 0;
        }
    }

    /**
     * 递减
     * 
     * @param key   键
     * @param delta 递减值
     * @return 递减后的值
     */
    public long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("Redis递减失败：key={}, delta={}, error={}", key, delta, e.getMessage());
            return 0;
        }
    }
}