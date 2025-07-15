package pw.pj.POJO.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置视图对象
 * 用于前端展示系统配置信息
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemConfigVO对象", description = "系统配置视图对象")
public class SystemConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "配置键")
    private String configKey;

    @ApiModelProperty(value = "配置值")
    private String configValue;

    @ApiModelProperty(value = "配置名称")
    private String configName;

    @ApiModelProperty(value = "配置描述")
    private String configDescription;

    @ApiModelProperty(value = "配置分类")
    private String category;

    @ApiModelProperty(value = "数据类型")
    private String dataType;

    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @ApiModelProperty(value = "是否必填")
    private Boolean required;

    @ApiModelProperty(value = "是否公开")
    private Boolean publicConfig;

    @ApiModelProperty(value = "是否可编辑")
    private Boolean editable;

    @ApiModelProperty(value = "排序值")
    private Integer sortOrder;

    @ApiModelProperty(value = "验证规则")
    private String validationRule;

    @ApiModelProperty(value = "配置选项（JSON格式）")
    private String configOptions;

    @ApiModelProperty(value = "配置状态")
    private Integer status;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    /**
     * 获取配置状态描述
     * 
     * @return 状态描述
     */
    public String getStatusDescription() {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 1:
                return "正常";
            case 0:
                return "禁用";
            case -1:
                return "已删除";
            default:
                return "未知";
        }
    }

    /**
     * 获取数据类型描述
     * 
     * @return 数据类型描述
     */
    public String getDataTypeDescription() {
        if (dataType == null) {
            return "字符串";
        }
        switch (dataType.toLowerCase()) {
            case "string":
                return "字符串";
            case "number":
            case "int":
            case "integer":
                return "整数";
            case "decimal":
            case "float":
            case "double":
                return "小数";
            case "boolean":
                return "布尔值";
            case "json":
                return "JSON对象";
            case "array":
                return "数组";
            case "date":
                return "日期";
            case "datetime":
                return "日期时间";
            case "url":
                return "网址";
            case "email":
                return "邮箱";
            case "password":
                return "密码";
            case "text":
                return "文本";
            default:
                return "字符串";
        }
    }

    /**
     * 判断是否为敏感配置
     * 
     * @return 是否敏感
     */
    public Boolean isSensitive() {
        if (configKey == null) {
            return false;
        }
        String lowerKey = configKey.toLowerCase();
        return lowerKey.contains("password") ||
                lowerKey.contains("secret") ||
                lowerKey.contains("token") ||
                lowerKey.contains("key") ||
                "password".equals(dataType);
    }

    /**
     * 获取显示值（敏感信息脱敏）
     * 
     * @return 显示值
     */
    public String getDisplayValue() {
        if (Boolean.TRUE.equals(isSensitive()) && configValue != null && !configValue.isEmpty()) {
            if (configValue.length() <= 4) {
                return "****";
            } else {
                return configValue.substring(0, 2) + "****" + configValue.substring(configValue.length() - 2);
            }
        }
        return configValue;
    }

    /**
     * 判断配置是否有效
     * 
     * @return 是否有效
     */
    public Boolean isValid() {
        return status != null && status == 1;
    }

    /**
     * 获取配置分类显示名称
     * 
     * @return 分类显示名称
     */
    public String getCategoryDisplayName() {
        if (category == null) {
            return "默认分类";
        }
        switch (category) {
            case "system":
                return "系统设置";
            case "website":
                return "网站信息";
            case "email":
                return "邮件配置";
            case "storage":
                return "存储配置";
            case "security":
                return "安全配置";
            case "upload":
                return "上传配置";
            case "cache":
                return "缓存配置";
            case "api":
                return "API配置";
            default:
                return category;
        }
    }

    /**
     * 判断是否可以删除
     * 
     * @return 是否可删除
     */
    public Boolean isDeletable() {
        return Boolean.TRUE.equals(editable) && !Boolean.TRUE.equals(required);
    }

    /**
     * 获取格式化的创建时间
     * 
     * @return 格式化时间
     */
    public String getFormattedCreateTime() {
        if (createTime != null) {
            return createTime.toString().replace("T", " ");
        }
        return "";
    }

    /**
     * 获取格式化的更新时间
     * 
     * @return 格式化时间
     */
    public String getFormattedUpdateTime() {
        if (updateTime != null) {
            return updateTime.toString().replace("T", " ");
        }
        return "";
    }
}