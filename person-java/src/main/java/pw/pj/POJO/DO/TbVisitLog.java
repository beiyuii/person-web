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

}
