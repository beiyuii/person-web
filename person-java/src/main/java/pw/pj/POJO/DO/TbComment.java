package pw.pj.POJO.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论表
 * @TableName tb_comment
 */
@TableName(value ="tb_comment")
@Data
public class TbComment implements Serializable {
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
        TbComment other = (TbComment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getArticleId() == null ? other.getArticleId() == null : this.getArticleId().equals(other.getArticleId()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getReplyToId() == null ? other.getReplyToId() == null : this.getReplyToId().equals(other.getReplyToId()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getAuthorName() == null ? other.getAuthorName() == null : this.getAuthorName().equals(other.getAuthorName()))
            && (this.getAuthorEmail() == null ? other.getAuthorEmail() == null : this.getAuthorEmail().equals(other.getAuthorEmail()))
            && (this.getAuthorWebsite() == null ? other.getAuthorWebsite() == null : this.getAuthorWebsite().equals(other.getAuthorWebsite()))
            && (this.getAuthorAvatar() == null ? other.getAuthorAvatar() == null : this.getAuthorAvatar().equals(other.getAuthorAvatar()))
            && (this.getAuthorIp() == null ? other.getAuthorIp() == null : this.getAuthorIp().equals(other.getAuthorIp()))
            && (this.getAuthorLocation() == null ? other.getAuthorLocation() == null : this.getAuthorLocation().equals(other.getAuthorLocation()))
            && (this.getAuthorAgent() == null ? other.getAuthorAgent() == null : this.getAuthorAgent().equals(other.getAuthorAgent()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getContentHtml() == null ? other.getContentHtml() == null : this.getContentHtml().equals(other.getContentHtml()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getReplyCount() == null ? other.getReplyCount() == null : this.getReplyCount().equals(other.getReplyCount()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIsAdmin() == null ? other.getIsAdmin() == null : this.getIsAdmin().equals(other.getIsAdmin()))
            && (this.getIsSticky() == null ? other.getIsSticky() == null : this.getIsSticky().equals(other.getIsSticky()))
            && (this.getNotifyEmail() == null ? other.getNotifyEmail() == null : this.getNotifyEmail().equals(other.getNotifyEmail()))
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
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getReplyToId() == null) ? 0 : getReplyToId().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getAuthorName() == null) ? 0 : getAuthorName().hashCode());
        result = prime * result + ((getAuthorEmail() == null) ? 0 : getAuthorEmail().hashCode());
        result = prime * result + ((getAuthorWebsite() == null) ? 0 : getAuthorWebsite().hashCode());
        result = prime * result + ((getAuthorAvatar() == null) ? 0 : getAuthorAvatar().hashCode());
        result = prime * result + ((getAuthorIp() == null) ? 0 : getAuthorIp().hashCode());
        result = prime * result + ((getAuthorLocation() == null) ? 0 : getAuthorLocation().hashCode());
        result = prime * result + ((getAuthorAgent() == null) ? 0 : getAuthorAgent().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getContentHtml() == null) ? 0 : getContentHtml().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getReplyCount() == null) ? 0 : getReplyCount().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIsAdmin() == null) ? 0 : getIsAdmin().hashCode());
        result = prime * result + ((getIsSticky() == null) ? 0 : getIsSticky().hashCode());
        result = prime * result + ((getNotifyEmail() == null) ? 0 : getNotifyEmail().hashCode());
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
        sb.append(", parentId=").append(parentId);
        sb.append(", replyToId=").append(replyToId);
        sb.append(", level=").append(level);
        sb.append(", path=").append(path);
        sb.append(", authorName=").append(authorName);
        sb.append(", authorEmail=").append(authorEmail);
        sb.append(", authorWebsite=").append(authorWebsite);
        sb.append(", authorAvatar=").append(authorAvatar);
        sb.append(", authorIp=").append(authorIp);
        sb.append(", authorLocation=").append(authorLocation);
        sb.append(", authorAgent=").append(authorAgent);
        sb.append(", content=").append(content);
        sb.append(", contentHtml=").append(contentHtml);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", replyCount=").append(replyCount);
        sb.append(", status=").append(status);
        sb.append(", isAdmin=").append(isAdmin);
        sb.append(", isSticky=").append(isSticky);
        sb.append(", notifyEmail=").append(notifyEmail);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}