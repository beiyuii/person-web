package pw.pj.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * IP地址处理工具类
 * 提供IP地址的获取、验证和处理功能
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Slf4j
public class IpUtils {

    /**
     * 未知IP地址
     */
    private static final String UNKNOWN_IP = "unknown";

    /**
     * 本地IP地址
     */
    private static final String LOCAL_IP = "127.0.0.1";

    /**
     * IPv6本地地址
     */
    private static final String IPV6_LOCAL = "0:0:0:0:0:0:0:1";

    /**
     * IP地址正则表达式
     */
    private static final String IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    /**
     * 私有IP地址范围
     */
    private static final String[] PRIVATE_IP_RANGES = {
            "10.0.0.0/8",
            "172.16.0.0/12",
            "192.168.0.0/16",
            "127.0.0.0/8"
    };

    /**
     * 获取客户端真实IP地址
     * 
     * @param request HttpServletRequest对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return LOCAL_IP;
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isUnknownIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isUnknownIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP地址的情况，取第一个非unknown的IP
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            String[] ips = ip.split(",");
            for (String singleIp : ips) {
                singleIp = singleIp.trim();
                if (!isUnknownIp(singleIp)) {
                    ip = singleIp;
                    break;
                }
            }
        }

        // 如果是IPv6的本地地址，转换为IPv4的本地地址
        if (IPV6_LOCAL.equals(ip)) {
            ip = LOCAL_IP;
        }

        return StringUtils.defaultIfBlank(ip, LOCAL_IP);
    }

    /**
     * 判断IP地址是否为未知
     * 
     * @param ip IP地址
     * @return 是否为未知IP
     */
    private static boolean isUnknownIp(String ip) {
        return StringUtils.isBlank(ip) || UNKNOWN_IP.equalsIgnoreCase(ip);
    }

    /**
     * 验证IP地址格式是否正确
     * 
     * @param ip IP地址
     * @return 是否为有效的IP地址
     */
    public static boolean isValidIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return false;
        }
        return Pattern.matches(IP_REGEX, ip);
    }

    /**
     * 判断是否为内网IP地址
     * 
     * @param ip IP地址
     * @return 是否为内网IP
     */
    public static boolean isInternalIp(String ip) {
        if (!isValidIp(ip)) {
            return false;
        }

        long ipLong = ipToLong(ip);

        // 10.0.0.0/8 => 10.0.0.0 ~ 10.255.255.255
        if (ipLong >= ipToLong("10.0.0.0") && ipLong <= ipToLong("10.255.255.255")) {
            return true;
        }

        // 172.16.0.0/12 => 172.16.0.0 ~ 172.31.255.255
        if (ipLong >= ipToLong("172.16.0.0") && ipLong <= ipToLong("172.31.255.255")) {
            return true;
        }

        // 192.168.0.0/16 => 192.168.0.0 ~ 192.168.255.255
        if (ipLong >= ipToLong("192.168.0.0") && ipLong <= ipToLong("192.168.255.255")) {
            return true;
        }

        // 127.0.0.0/8 => 127.0.0.0 ~ 127.255.255.255
        if (ipLong >= ipToLong("127.0.0.0") && ipLong <= ipToLong("127.255.255.255")) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为外网IP地址
     * 
     * @param ip IP地址
     * @return 是否为外网IP
     */
    public static boolean isExternalIp(String ip) {
        return isValidIp(ip) && !isInternalIp(ip);
    }

    /**
     * 将IP地址转换为long类型
     * 
     * @param ip IP地址
     * @return long类型的IP地址
     */
    public static long ipToLong(String ip) {
        if (!isValidIp(ip)) {
            return 0;
        }

        String[] parts = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Long.parseLong(parts[i]);
        }
        return result;
    }

    /**
     * 将long类型转换为IP地址
     * 
     * @param ipLong long类型的IP地址
     * @return IP地址字符串
     */
    public static String longToIp(long ipLong) {
        return ((ipLong >> 24) & 0xFF) + "." +
                ((ipLong >> 16) & 0xFF) + "." +
                ((ipLong >> 8) & 0xFF) + "." +
                (ipLong & 0xFF);
    }

    /**
     * 获取本机IP地址
     * 
     * @return 本机IP地址
     */
    public static String getLocalIp() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("获取本机IP地址失败", e);
            return LOCAL_IP;
        }
    }

    /**
     * 获取本机主机名
     * 
     * @return 本机主机名
     */
    public static String getLocalHostName() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostName();
        } catch (UnknownHostException e) {
            log.error("获取本机主机名失败", e);
            return "localhost";
        }
    }

    /**
     * 根据IP地址获取主机名
     * 
     * @param ip IP地址
     * @return 主机名
     */
    public static String getHostNameByIp(String ip) {
        if (!isValidIp(ip)) {
            return ip;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            log.error("根据IP地址获取主机名失败: {}", ip, e);
            return ip;
        }
    }

    /**
     * 检查IP地址是否可达
     * 
     * @param ip      IP地址
     * @param timeout 超时时间（毫秒）
     * @return 是否可达
     */
    public static boolean isReachable(String ip, int timeout) {
        if (!isValidIp(ip)) {
            return false;
        }

        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(timeout);
        } catch (Exception e) {
            log.error("检查IP地址可达性失败: {}", ip, e);
            return false;
        }
    }

    /**
     * 检查IP地址是否可达（默认超时时间3秒）
     * 
     * @param ip IP地址
     * @return 是否可达
     */
    public static boolean isReachable(String ip) {
        return isReachable(ip, 3000);
    }

    /**
     * 掩码IP地址，用于日志记录等场景
     * 
     * @param ip IP地址
     * @return 掩码后的IP地址
     */
    public static String maskIp(String ip) {
        if (!isValidIp(ip)) {
            return ip;
        }

        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".*.*";
        }

        return ip;
    }

    /**
     * 判断IP地址是否在指定的IP段内
     * 
     * @param ip   IP地址
     * @param cidr CIDR格式的IP段（如：192.168.1.0/24）
     * @return 是否在指定IP段内
     */
    public static boolean isInRange(String ip, String cidr) {
        if (!isValidIp(ip) || StringUtils.isBlank(cidr)) {
            return false;
        }

        try {
            String[] parts = cidr.split("/");
            if (parts.length != 2) {
                return false;
            }

            String networkIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            if (!isValidIp(networkIp) || prefixLength < 0 || prefixLength > 32) {
                return false;
            }

            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(networkIp);
            long mask = (0xFFFFFFFFL << (32 - prefixLength)) & 0xFFFFFFFFL;

            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            log.error("判断IP地址是否在指定IP段内失败: ip={}, cidr={}", ip, cidr, e);
            return false;
        }
    }

    /**
     * 获取IP地址的地理位置信息（需要第三方服务支持）
     * 这里只是示例方法，实际使用时需要接入IP地理位置服务
     * 
     * @param ip IP地址
     * @return 地理位置信息
     */
    public static String getIpLocation(String ip) {
        if (!isValidIp(ip)) {
            return "未知";
        }

        // 本地IP
        if (isInternalIp(ip)) {
            return "内网IP";
        }

        // 这里应该调用第三方IP地理位置服务
        // 例如：高德地图、百度地图、IP138等提供的API
        // 由于需要具体的API接入，这里仅返回示例
        return "需要接入第三方IP地理位置服务";
    }

    /**
     * 获取User-Agent信息
     * 
     * @param request HttpServletRequest对象
     * @return User-Agent字符串
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(request.getHeader("User-Agent"), "");
    }

    /**
     * 获取Referer信息
     * 
     * @param request HttpServletRequest对象
     * @return Referer字符串
     */
    public static String getReferer(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        return StringUtils.defaultIfBlank(request.getHeader("Referer"), "");
    }

    /**
     * 获取客户端访问信息
     * 
     * @param request HttpServletRequest对象
     * @return 访问信息字符串
     */
    public static String getClientInfo(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        StringBuilder info = new StringBuilder();
        info.append("IP: ").append(getClientIp(request));
        info.append(", User-Agent: ").append(getUserAgent(request));
        info.append(", Referer: ").append(getReferer(request));

        return info.toString();
    }
}