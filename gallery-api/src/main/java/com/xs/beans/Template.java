package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "tb_template")
public class Template extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "新增时id不用传")
    private Integer id;

    @Column(name = "category_id")
    @ApiModelProperty(value = "分类id")
    @NotNull(message = "分类id不能为空")
    private Integer categoryId;

    @Column(name = "brand_id")
    @ApiModelProperty(value = "品牌id")
    @NotNull(message = "品牌id不能为空")
    private Integer brandId;

    /**
     * 1- 1:1; 2- 4:3;  3- 16:9; 
     */
    @ApiModelProperty(value = "比例")
    @NotNull(message = "比例不能为空")
    private Byte ratio;

    @Column(name = "is_enabled")
    @ApiModelProperty(value = "是否启用")
    @NotNull(message = "是否启用不能为空")
    private Boolean enabled;

    @Column(name = "gratis")
    @ApiModelProperty(value = "是否免费")
    @NotNull(message = "是否免费不能为空")
    private Boolean gratis;

    @Column(name = "preview_image_url")
    @ApiModelProperty(value = "预览图")
    @NotBlank(message = "预览图不能为空!")
    private String previewImageUrl;
    @Column(name = "phone_preview_image_url")
    @ApiModelProperty(value = "手机端预览图不能为空")
//    @NotBlank(message = "手机端预览图不能为空!")
    private String phonePreviewImageUrl;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @NotBlank(message = "模板名称不能为空!")
    @Length(min=1,max=24,message = "模板名称不能超过24个字符!")
    private String name;

    @ApiModelProperty(value = "简介")
    @NotBlank(message = "简介不能为空!")
    private String descri;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

    @Transient
    @ApiModelProperty(value = "分类名称")
    private String categoryName;
    @Transient
    @ApiModelProperty(value = "品牌名称")
    private String brandName;

    @Transient
    @ApiModelProperty(value = "模板标签id集合")
    private String labelIds;

    @Transient
    @ApiModelProperty(value = "是否收藏")
    private Boolean hasCollection;

    @Transient
    @ApiModelProperty(value = "是否可用")
    private Boolean canUse;

    @Transient
    @ApiModelProperty(value = "模板分类")
    private TemplateCategory templateCategory;

    @Transient
    @ApiModelProperty(value = "模板/图片")
    private String tpText;


    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return category_id
     */
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * @param categoryId
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * @return brand_id
     */
    public Integer getBrandId() {
        return brandId;
    }

    /**
     * @param brandId
     */
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    /**
     * 获取1- 1:1; 2- 4:3;  3- 16:9; 
     *
     * @return ratio - 1- 1:1; 2- 4:3;  3- 16:9; 
     */
    public Byte getRatio() {
        return ratio;
    }

    /**
     * 设置1- 1:1; 2- 4:3;  3- 16:9; 
     *
     * @param ratio 1- 1:1; 2- 4:3;  3- 16:9; 
     */
    public void setRatio(Byte ratio) {
        this.ratio = ratio;
    }

    /**
     * @return preview_image_url
     */
    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    /**
     * @param previewImageUrl
     */
    public void setPreviewImageUrl(String previewImageUrl) {
        this.previewImageUrl = previewImageUrl;
    }

    /**
     * 获取模板名称
     *
     * @return name - 模板名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置模板名称
     *
     * @param name 模板名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return descri
     */
    public String getDescri() {
        return descri;
    }

    /**
     * @param descri
     */
    public void setDescri(String descri) {
        this.descri = descri;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(String labelIds) {
        this.labelIds = labelIds;
    }

    public Boolean getHasCollection() {
        return hasCollection;
    }

    public void setHasCollection(Boolean hasCollection) {
        this.hasCollection = hasCollection;
    }

    public Boolean getCanUse() {
        return canUse;
    }

    public void setCanUse(Boolean canUse) {
        this.canUse = canUse;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public TemplateCategory getTemplateCategory() {
        return templateCategory;
    }

    public void setTemplateCategory(TemplateCategory templateCategory) {
        this.templateCategory = templateCategory;
    }

    public Boolean getGratis() {
        return gratis;
    }

    public void setGratis(Boolean gratis) {
        this.gratis = gratis;
    }

    public String getTpText() {
        return tpText;
    }

    public void setTpText(String tpText) {
        this.tpText = tpText;
    }

    public String getPhonePreviewImageUrl() {
        return phonePreviewImageUrl;
    }

    public void setPhonePreviewImageUrl(String phonePreviewImageUrl) {
        this.phonePreviewImageUrl = phonePreviewImageUrl;
    }
}
