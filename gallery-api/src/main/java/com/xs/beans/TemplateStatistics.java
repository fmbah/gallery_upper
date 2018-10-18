package com.xs.beans;

import com.xs.core.sbean.BaseBean;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_template_statistics")
public class TemplateStatistics extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "template_id")
    private Integer templateId;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 访问次数
     */
    @Column(name = "visitor_count")
    private Integer visitorCount;

    /**
     * 分享次数
     */
    @Column(name = "share_count")
    private Integer shareCount;

    @Column(name = "used_count")
    private Integer usedCount;


    @Transient
    private String templateCode;
    @Transient
    private String templateName;
    @Transient
    private String categoryName;
    @Transient
    private String brandName;

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
     * @return template_id
     */
    public Integer getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId
     */
    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
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
     * 获取访问次数
     *
     * @return visitor_count - 访问次数
     */
    public Integer getVisitorCount() {
        return visitorCount;
    }

    /**
     * 设置访问次数
     *
     * @param visitorCount 访问次数
     */
    public void setVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    /**
     * 获取分享次数
     *
     * @return share_count - 分享次数
     */
    public Integer getShareCount() {
        return shareCount;
    }

    /**
     * 设置分享次数
     *
     * @param shareCount 分享次数
     */
    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    /**
     * @return used_count
     */
    public Integer getUsedCount() {
        return usedCount;
    }

    /**
     * @param usedCount
     */
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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
}