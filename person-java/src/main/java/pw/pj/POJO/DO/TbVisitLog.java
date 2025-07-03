package pw.pj.POJO.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 访问日志表
 * @TableName tb_visit_log
 */
@TableName(value ="tb_visit_log")
@Data
public class TbVisitLog implements Serializable {
    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 访问者IP
     */
    private String visitorIp;

    /**
     * 访问者地理位置
     */
    private String visitorLocation;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 设备类型
     */
    private String device;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 访问时间
     */
    private Date visitTime;

    /**
     * 停留时间（秒）
     */
    private Integer stayTime;

    /**
     * 是否移动端：0-否，1-是
     */
    private Integer isMobile;

    /**
     * 是否爬虫：0-否，1-是
     */
    private Integer isSpider;

    /**
     * 爬虫名称
     */
    private String spiderName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TbVisitLog other = (TbVisitLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getArticleId() == null ? other.getArticleId() == null : this.getArticleId().equals(other.getArticleId()))
            && (this.getVisitorIp() == null ? other.getVisitorIp() == null : this.getVisitorIp().equals(other.getVisitorIp()))
            && (this.getVisitorLocation() == null ? other.getVisitorLocation() == null : this.getVisitorLocation().equals(other.getVisitorLocation()))
            && (this.getUserAgent() == null ? other.getUserAgent() == null : this.getUserAgent().equals(other.getUserAgent()))
            && (this.getBrowser() == null ? other.getBrowser() == null : this.getBrowser().equals(other.getBrowser()))
            && (this.getBrowserVersion() == null ? other.getBrowserVersion() == null : this.getBrowserVersion().equals(other.getBrowserVersion()))
            && (this.getOs() == null ? other.getOs() == null : this.getOs().equals(other.getOs()))
            && (this.getDevice() == null ? other.getDevice() == null : this.getDevice().equals(other.getDevice()))
            && (this.getReferer() == null ? other.getReferer() == null : this.getReferer().equals(other.getReferer()))
            && (this.getRequestUrl() == null ? other.getRequestUrl() == null : this.getRequestUrl().equals(other.getRequestUrl()))
            && (this.getVisitTime() == null ? other.getVisitTime() == null : this.getVisitTime().equals(other.getVisitTime()))
            && (this.getStayTime() == null ? other.getStayTime() == null : this.getStayTime().equals(other.getStayTime()))
            && (this.getIsMobile() == null ? other.getIsMobile() == null : this.getIsMobile().equals(other.getIsMobile()))
            && (this.getIsSpider() == null ? other.getIsSpider() == null : this.getIsSpider().equals(other.getIsSpider()))
            && (this.getSpiderName() == null ? other.getSpiderName() == null : this.getSpiderName().equals(other.getSpiderName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getArticleId() == null) ? 0 : getArticleId().hashCode());
        result = prime * result + ((getVisitorIp() == null) ? 0 : getVisitorIp().hashCode());
        result = prime * result + ((getVisitorLocation() == null) ? 0 : getVisitorLocation().hashCode());
        result = prime * result + ((getUserAgent() == null) ? 0 : getUserAgent().hashCode());
        result = prime * result + ((getBrowser() == null) ? 0 : getBrowser().hashCode());
        result = prime * result + ((getBrowserVersion() == null) ? 0 : getBrowserVersion().hashCode());
        result = prime * result + ((getOs() == null) ? 0 : getOs().hashCode());
        result = prime * result + ((getDevice() == null) ? 0 : getDevice().hashCode());
        result = prime * result + ((getReferer() == null) ? 0 : getReferer().hashCode());
        result = prime * result + ((getRequestUrl() == null) ? 0 : getRequestUrl().hashCode());
        result = prime * result + ((getVisitTime() == null) ? 0 : getVisitTime().hashCode());
        result = prime * result + ((getStayTime() == null) ? 0 : getStayTime().hashCode());
        result = prime * result + ((getIsMobile() == null) ? 0 : getIsMobile().hashCode());
        result = prime * result + ((getIsSpider() == null) ? 0 : getIsSpider().hashCode());
        result = prime * result + ((getSpiderName() == null) ? 0 : getSpiderName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", articleId=").append(articleId);
        sb.append(", visitorIp=").append(visitorIp);
        sb.append(", visitorLocation=").append(visitorLocation);
        sb.append(", userAgent=").append(userAgent);
        sb.append(", browser=").append(browser);
        sb.append(", browserVersion=").append(browserVersion);
        sb.append(", os=").append(os);
        sb.append(", device=").append(device);
        sb.append(", referer=").append(referer);
        sb.append(", requestUrl=").append(requestUrl);
        sb.append(", visitTime=").append(visitTime);
        sb.append(", stayTime=").append(stayTime);
        sb.append(", isMobile=").append(isMobile);
        sb.append(", isSpider=").append(isSpider);
        sb.append(", spiderName=").append(spiderName);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}