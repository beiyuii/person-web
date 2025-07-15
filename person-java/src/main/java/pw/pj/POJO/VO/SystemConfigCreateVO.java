package pw.pj.POJO.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统配置创建视图对象
 * 用于接收创建系统配置的请求参数
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemConfigCreateVO对象", description = "系统配置创建请求参数")
public class SystemConfigCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置键", required = true, example = "site.name")
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]*$", message = "配置键只能包含字母、数字、点号、下划线和短横线，且必须以字母开头")
    private String configKey;

    @ApiModelProperty(value = "配置值", required = true, example = "个人博客系统")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 2000, message = "配置值长度不能超过2000个字符")
    private String configValue;

    @ApiModelProperty(value = "配置名称", required = true, example = "网站名称")
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100个字符")
    private String configName;

    @ApiModelProperty(value = "配置描述", example = "网站的显示名称")
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    private String configDescription;

    @ApiModelProperty(value = "配置分类", required = true, example = "website")
    @NotBlank(message = "配置分类不能为空")
    @Size(max = 50, message = "配置分类长度不能超过50个字符")
    private String category;

    @ApiModelProperty(value = "数据类型", example = "string", allowableValues = "string,number,boolean,json,array,date,datetime,url,email,password,text")
    @Size(max = 20, message = "数据类型长度不能超过20个字符")
    private String dataType = "string";

    @ApiModelProperty(value = "默认值", example = "默认网站名称")
    @Size(max = 2000, message = "默认值长度不能超过2000个字符")
    private String defaultValue;

    @ApiModelProperty(value = "是否必填", example = "true")
    private Boolean required = false;

    @ApiModelProperty(value = "是否公开", example = "true")
    private Boolean publicConfig = true;

    @ApiModelProperty(value = "是否可编辑", example = "true")
    private Boolean editable = true;

    @ApiModelProperty(value = "排序值", example = "1")
    private Integer sortOrder = 0;

    @ApiModelProperty(value = "验证规则", example = "^.{1,100}$")
    @Size(max = 500, message = "验证规则长度不能超过500个字符")
    private String validationRule;

    @ApiModelProperty(value = "配置选项（JSON格式）", example = "{\"options\": [\"选项1\", \"选项2\"]}")
    @Size(max = 1000, message = "配置选项长度不能超过1000个字符")
    private String configOptions;

    @ApiModelProperty(value = "配置状态", example = "1", allowableValues = "0,1")
    @NotNull(message = "配置状态不能为空")
    private Integer status = 1;

    @ApiModelProperty(value = "备注信息", example = "这是一个重要的配置项")
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

    /**
     * 验证数据类型是否有效
     * 
     * @return 是否有效
     */
    public boolean isValidDataType() {
        if (dataType == null) {
            return false;
        }
        String[] validTypes = { "string", "number", "int", "integer", "decimal", "float", "double",
                "boolean", "json", "array", "date", "datetime", "url", "email", "password", "text" };
        for (String type : validTypes) {
            if (type.equals(dataType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证配置分类是否有效
     * 
     * @return 是否有效
     */
    public boolean isValidCategory() {
        if (category == null) {
            return false;
        }
        String[] validCategories = { "system", "website", "email", "storage", "security",
                "upload", "cache", "api", "basic", "advanced" };
        for (String cat : validCategories) {
            if (cat.equals(category.toLowerCase())) {
                return true;
            }
        }
        return true; // 允许自定义分类
    }

    /**
     * 验证配置状态是否有效
     * 
     * @return 是否有效
     */
    public boolean isValidStatus() {
        return status != null && (status == 0 || status == 1);
    }

    /**
     * 获取标准化的配置键
     * 
     * @return 标准化配置键
     */
    public String getNormalizedConfigKey() {
        if (configKey == null) {
            return null;
        }
        return configKey.toLowerCase().trim();
    }

    /**
     * 获取标准化的数据类型
     * 
     * @return 标准化数据类型
     */
    public String getNormalizedDataType() {
        if (dataType == null) {
            return "string";
        }
        return dataType.toLowerCase().trim();
    }

    /**
     * 获取标准化的配置分类
     * 
     * @return 标准化配置分类
     */
    public String getNormalizedCategory() {
        if (category == null) {
            return "system";
        }
        return category.toLowerCase().trim();
    }

    /**
     * 判断是否为敏感配置
     * 
     * @return 是否敏感
     */
    public boolean isSensitiveConfig() {
        if (configKey == null) {
            return false;
        }
        String lowerKey = configKey.toLowerCase();
        return lowerKey.contains("password") ||
                lowerKey.contains("secret") ||
                lowerKey.contains("token") ||
                lowerKey.contains("key") ||
                "password".equals(getNormalizedDataType());
    }

    /**
     * 设置默认值
     */
    public void setDefaults() {
        if (dataType == null || dataType.trim().isEmpty()) {
            dataType = "string";
        }
        if (required == null) {
            required = false;
        }
        if (publicConfig == null) {
            publicConfig = true;
        }
        if (editable == null) {
            editable = true;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (status == null) {
            status = 1;
        }

        // 敏感配置默认不公开
        if (isSensitiveConfig() && publicConfig == null) {
            publicConfig = false;
        }
    }

    /**
     * 验证所有字段
     * 
     * @return 验证结果消息，null表示验证通过
     */
    public String validateAll() {
        if (!isValidDataType()) {
            return "数据类型无效";
        }
        if (!isValidCategory()) {
            return "配置分类无效";
        }
        if (!isValidStatus()) {
            return "配置状态无效";
        }
        return null;
    }
}