package pw.pj.POJO.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论表
 * @TableName tb_comment
 */
@TableName(value ="tb_comment")
@Data
public class TbCommentDTO implements Serializable {
    /**
     * 评论ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 父评论ID，0表示顶级评论
     */
    private Long parentId;

    /**
     * 回复的评论ID
     */
    private Long replyToId;

    /**
     * 评论层级
     */
    private Integer level;

    /**
     * 评论路径
     */
    private String path;

    /**
     * 评论者姓名
     */
    private String authorName;

    /**
     * 评论者邮箱
     */
    private String authorEmail;

    /**
     * 评论者网站
     */
    private String authorWebsite;

    /**
     * 评论者头像
     */
    private String authorAvatar;

    /**
     * 评论者IP
     */
    private String authorIp;

    /**
     * 评论者地理位置
     */
    private String authorLocation;

    /**
     * 用户代理信息
     */
    private String authorAgent;

    /**
     * 评论内容
     */
    private String content;

    /**
     * HTML格式内容
     */
    private String contentHtml;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 状态：0-待审核，1-已通过，2-已拒绝，3-垃圾评论
     */
    private Integer status;

    /**
     * 是否管理员评论：0-否，1-是
     */
    private Integer isAdmin;

    /**
     * 是否置顶：0-否，1-是
     */
    private Integer isSticky;

    /**
     * 邮件通知：0-否，1-是
     */
    private Integer notifyEmail;


}
