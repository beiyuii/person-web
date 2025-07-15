package pw.pj.POJO.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 访问日志视图对象
 * 用于前端展示访问日志信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "VisitLogVO对象", description = "访问日志视图对象")
public class VisitLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "访客IP地址")
    private String visitorIp;

    @ApiModelProperty(value = "用户代理信息")
    private String userAgent;

    @ApiModelProperty(value = "请求URL")
    private String requestUrl;

    @ApiModelProperty(value = "请求方法")
    private String requestMethod;

    @ApiModelProperty(value = "页面标题")
    private String pageTitle;

    @ApiModelProperty(value = "来源页面")
    private String refererUrl;

    @ApiModelProperty(value = "浏览器名称")
    private String browserName;

    @ApiModelProperty(value = "浏览器版本")
    private String browserVersion;

    @ApiModelProperty(value = "操作系统")
    private String operatingSystem;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "屏幕分辨率")
    private String screenResolution;

    @ApiModelProperty(value = "访问地区")
    private String region;

    @ApiModelProperty(value = "访问城市")
    private String city;

    @ApiModelProperty(value = "ISP提供商")
    private String isp;

    @ApiModelProperty(value = "会话ID")
    private String sessionId;

    @ApiModelProperty(value = "用户ID（如果已登录）")
    private Long userId;

    @ApiModelProperty(value = "用户名（如果已登录）")
    private String username;

    @ApiModelProperty(value = "停留时间（秒）")
    private Integer stayTime;

    @ApiModelProperty(value = "访问次数")
    private Integer visitCount;

    @ApiModelProperty(value = "是否首次访问")
    private Boolean isFirstVisit;

    @ApiModelProperty(value = "是否移动端访问")
    private Boolean isMobile;

    @ApiModelProperty(value = "访问时间")
    private LocalDateTime visitTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 获取格式化的访问时间
     * 
     * @return 格式化的访问时间字符串
     */
    public String getFormattedVisitTime() {
        if (visitTime != null) {
            return visitTime.toString().replace("T", " ");
        }
        return "";
    }

    /**
     * 获取简化的用户代理信息
     * 
     * @return 简化的用户代理字符串
     */
    public String getSimplifiedUserAgent() {
        if (userAgent != null && userAgent.length() > 100) {
            return userAgent.substring(0, 100) + "...";
        }
        return userAgent;
    }

    /**
     * 获取地理位置信息
     * 
     * @return 地理位置字符串
     */
    public String getLocationInfo() {
        StringBuilder location = new StringBuilder();
        if (region != null && !region.isEmpty()) {
            location.append(region);
        }
        if (city != null && !city.isEmpty()) {
            if (location.length() > 0) {
                location.append(" - ");
            }
            location.append(city);
        }
        return location.toString();
    }

    /**
     * 获取设备信息
     * 
     * @return 设备信息字符串
     */
    public String getDeviceInfo() {
        StringBuilder device = new StringBuilder();
        if (operatingSystem != null && !operatingSystem.isEmpty()) {
            device.append(operatingSystem);
        }
        if (deviceType != null && !deviceType.isEmpty()) {
            if (device.length() > 0) {
                device.append(" (");
                device.append(deviceType);
                device.append(")");
            } else {
                device.append(deviceType);
            }
        }
        return device.toString();
    }

    /**
     * 获取浏览器信息
     * 
     * @return 浏览器信息字符串
     */
    public String getBrowserInfo() {
        StringBuilder browser = new StringBuilder();
        if (browserName != null && !browserName.isEmpty()) {
            browser.append(browserName);
        }
        if (browserVersion != null && !browserVersion.isEmpty()) {
            if (browser.length() > 0) {
                browser.append(" ");
                browser.append(browserVersion);
            } else {
                browser.append(browserVersion);
            }
        }
        return browser.toString();
    }

    /**
     * 判断是否为有效访问
     * 
     * @return 是否为有效访问
     */
    public Boolean isValidVisit() {
        return stayTime != null && stayTime > 0;
    }

    /**
     * 获取访问类型描述
     * 
     * @return 访问类型描述
     */
    public String getVisitTypeDescription() {
        if (Boolean.TRUE.equals(isFirstVisit)) {
            return "首次访问";
        } else {
            return "回访用户";
        }
    }

    /**
     * 获取平台类型
     * 
     * @return 平台类型
     */
    public String getPlatformType() {
        if (Boolean.TRUE.equals(isMobile)) {
            return "移动端";
        } else {
            return "桌面端";
        }
    }
}