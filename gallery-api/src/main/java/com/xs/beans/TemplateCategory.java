package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Table(name = "tb_template_category")
public class TemplateCategory extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "自增ID(新增时不用传)")
    private Integer id;

    /**
     * brand_center: 品牌中心
category: 普通类目
     */
    @ApiModelProperty(value = "类型, brand_center: 品牌中心；category: 普通类目")
    @NotBlank(message = "类型不能为空")
    private String type;

    @ApiModelProperty(value = "标题")
    @Length(max = 45,message = "返利名称最长45个字符!")
    @NotBlank(message = "标题不能为空")
    private String title;

    @ApiModelProperty(value = "权重")
    @NotNull(message = "权重不能为空")
    @Max(value = 100, message = "权重最大为100")
    @Min(value = 1, message = "权重最小为1")
    private Short weight;

    @Column(name = "is_hot")
    @ApiModelProperty(value = "是否热门")
    @NotNull(message = "是否热门不能为空")
    private Boolean isHot;

    @ApiModelProperty(value = "简介")
    @Length(max = 128,message = "简介最长128个字符!")
//    @NotBlank(message = "简介不能为空")
    private String introduction;

    /**
     * 分类背景图
     */
    @Column(name = "background_image_url")
    @ApiModelProperty(value = "分类背景图")
    @Length(max = 192,message = "分类背景图最长192个字符!")
//    @NotBlank(message = "分类背景图不能为空")
    private String backgroundImageUrl;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

    /**
     * 模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]
     */
    @Column(name = "template_filters")
    @ApiModelProperty(value = "模板滤镜，数组形式。如：[{\"num\":\"1\", \"url\": \"xxx\"}, {}...]")
//    @NotBlank(message = "模板滤镜不能为空")
    private String templateFilters;


    @Transient
    @ApiModelProperty(value = "模板数")
    private Integer templateCount;
    @Transient
    @ApiModelProperty(value = "滤镜数")
    private Integer filtersCount;


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
     * 获取brand_center: 品牌中心
category: 普通类目
     *
     * @return type - brand_center: 品牌中心
category: 普通类目
     */
    public String getType() {
        return type;
    }

    /**
     * 设置brand_center: 品牌中心
category: 普通类目
     *
     * @param type brand_center: 品牌中心
category: 普通类目
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return weight
     */
    public Short getWeight() {
        return weight;
    }

    /**
     * @param weight
     */
    public void setWeight(Short weight) {
        this.weight = weight;
    }

    /**
     * @return is_hot
     */
    public Boolean getIsHot() {
        return isHot;
    }

    /**
     * @param isHot
     */
    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }

    /**
     * @return introduction
     */
    public String getIntroduction() {
        return introduction;
    }

    /**
     * @param introduction
     */
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    /**
     * 获取分类背景图
     *
     * @return background_image_url - 分类背景图
     */
    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    /**
     * 设置分类背景图
     *
     * @param backgroundImageUrl 分类背景图
     */
    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    /**
     * @return gmt_create
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * @param gmtCreate
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * @return gmt_modified
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * @param gmtModified
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 获取模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]
     *
     * @return template_filters - 模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]
     */
    public String getTemplateFilters() {
        return templateFilters;
    }

    /**
     * 设置模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]
     *
     * @param templateFilters 模板滤镜，数组形式。如：[{"num":"1", "url": "xxx"}, {}...]
     */
    public void setTemplateFilters(String templateFilters) {
        this.templateFilters = templateFilters;
    }


    public Boolean getHot() {
        return isHot;
    }

    public void setHot(Boolean hot) {
        isHot = hot;
    }

    public Integer getTemplateCount() {
        return templateCount;
    }

    public void setTemplateCount(Integer templateCount) {
        this.templateCount = templateCount;
    }

    public Integer getFiltersCount() {
        return filtersCount;
    }

    public void setFiltersCount(Integer filtersCount) {
        this.filtersCount = filtersCount;
    }
}