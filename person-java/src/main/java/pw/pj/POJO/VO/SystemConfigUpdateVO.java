package pw.pj.POJO.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 系统配置更新视图对象
 * 用于接收更新系统配置的请求参数
 * 
 * @author PersonWeb开发团队
 * @version 1.0
 * @since 2024-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SystemConfigUpdateVO对象", description = "系统配置更新请求参数")
public class SystemConfigUpdateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置值", example = "个人博客系统")
    @Size(max = 2000, message = "配置值长度不能超过2000个字符")
    private String configValue;

    @ApiModelProperty(value = "配置名称", example = "网站名称")
    @Size(max = 100, message = "配置名称长度不能超过100个字符")
    private String configName;

    @ApiModelProperty(value = "配置描述", example = "网站的显示名称")
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    private String configDescription;

    @ApiModelProperty(value = "配置分类", example = "website")
    @Size(max = 50, message = "配置分类长度不能超过50个字符")
    private String category;

    @ApiModelProperty(value = "数据类型", example = "string", allowableValues = "string,number,boolean,json,array,date,datetime,url,email,password,text")
    @Size(max = 20, message = "数据类型长度不能超过20个字符")
    private String dataType;

    @ApiModelProperty(value = "默认值", example = "默认网站名称")
    @Size(max = 2000, message = "默认值长度不能超过2000个字符")
    private String defaultValue;

    @ApiModelProperty(value = "是否必填", example = "true")
    private Boolean required;

    @ApiModelProperty(value = "是否公开", example = "true")
    private Boolean publicConfig;

    @ApiModelProperty(value = "是否可编辑", example = "true")
    private Boolean editable;

    @ApiModelProperty(value = "排序值", example = "1")
    private Integer sortOrder;

    @ApiModelProperty(value = "验证规则", example = "^.{1,100}$")
    @Size(max = 500, message = "验证规则长度不能超过500个字符")
    private String validationRule;

    @ApiModelProperty(value = "配置选项（JSON格式）", example = "{\"options\": [\"选项1\", \"选项2\"]}")
    @Size(max = 1000, message = "配置选项长度不能超过1000个字符")
    private String configOptions;

    @ApiModelProperty(value = "配置状态", example = "1", allowableValues = "0,1")
    private Integer status;

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
            return true; // 允许不更新数据类型
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
            return true; // 允许不更新分类
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
        if (status == null) {
            return true; // 允许不更新状态
        }
        return status == 0 || status == 1;
    }

    /**
     * 获取标准化的数据类型
     * 
     * @return 标准化数据类型
     */
    public String getNormalizedDataType() {
        if (dataType == null) {
            return null;
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
            return null;
        }
        return category.toLowerCase().trim();
    }

    /**
     * 检查是否有任何字段需要更新
     * 
     * @return 是否有更新字段
     */
    public boolean hasUpdates() {
        return configValue != null || configName != null || configDescription != null ||
                category != null || dataType != null || defaultValue != null ||
                required != null || publicConfig != null || editable != null ||
                sortOrder != null || validationRule != null || configOptions != null ||
                status != null || remark != null;
    }

    /**
     * 验证所有字段
     * 
     * @return 验证结果消息，null表示验证通过
     */
    public String validateAll() {
        if (!hasUpdates()) {
            return "至少需要更新一个字段";
        }
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

    /**
     * 清理空字符串（转换为null）
     */
    public void cleanEmptyStrings() {
        if (configValue != null && configValue.trim().isEmpty()) {
            configValue = null;
        }
        if (configName != null && configName.trim().isEmpty()) {
            configName = null;
        }
        if (configDescription != null && configDescription.trim().isEmpty()) {
            configDescription = null;
        }
        if (category != null && category.trim().isEmpty()) {
            category = null;
        }
        if (dataType != null && dataType.trim().isEmpty()) {
            dataType = null;
        }
        if (defaultValue != null && defaultValue.trim().isEmpty()) {
            defaultValue = null;
        }
        if (validationRule != null && validationRule.trim().isEmpty()) {
            validationRule = null;
        }
        if (configOptions != null && configOptions.trim().isEmpty()) {
            configOptions = null;
        }
        if (remark != null && remark.trim().isEmpty()) {
            remark = null;
        }
    }

    /**
     * 获取更新字段数量
     * 
     * @return 更新字段数量
     */
    public int getUpdateFieldCount() {
        int count = 0;
        if (configValue != null)
            count++;
        if (configName != null)
            count++;
        if (configDescription != null)
            count++;
        if (category != null)
            count++;
        if (dataType != null)
            count++;
        if (defaultValue != null)
            count++;
        if (required != null)
            count++;
        if (publicConfig != null)
            count++;
        if (editable != null)
            count++;
        if (sortOrder != null)
            count++;
        if (validationRule != null)
            count++;
        if (configOptions != null)
            count++;
        if (status != null)
            count++;
        if (remark != null)
            count++;
        return count;
    }

    /**
     * 判断是否只更新配置值
     * 
     * @return 是否只更新配置值
     */
    public boolean isOnlyValueUpdate() {
        return configValue != null && getUpdateFieldCount() == 1;
    }

    /**
     * 判断是否包含敏感字段更新
     * 
     * @return 是否包含敏感字段
     */
    public boolean hasSensitiveUpdate() {
        return configValue != null &&
                (dataType != null && "password".equals(dataType.toLowerCase()));
    }
}