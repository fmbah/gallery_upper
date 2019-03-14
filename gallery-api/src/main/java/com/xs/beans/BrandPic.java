package com.xs.beans;

import com.alibaba.fastjson.JSONArray;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "tb_brand_pic")
public class BrandPic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 图片名称
     */
    @Column(name = "pic_name")
    @NotBlank(message = "图片名称不能为空!")
    @Length(max=24,message = "图片名称不能超过24个字符!")
    private String picName;

    /**
     * 品牌id
     */
    @Column(name = "brand_id")
    @NotNull(message = "品牌ID不能为空")
    private Integer brandId;

    @Column(name = "template_id")
    private String templateId;
    @Transient
    private JSONArray templateIds;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * 前端展示图
     */
    @Length(max=256,message = "前端展示图不能超过256个字符!")
    @Column(name = "miniapp_display_src")
    private String miniappDisplaySrc;
    @Transient
    private JSONArray miniappDisplaySrcs;

    /**
     * 最近申请图
     */
    @NotBlank(message = "最近申请图不能为空!")
    @Column(name = "latest_apply_src")
    private String latestApplySrc;
    @Transient
    private JSONArray latestApplySrcs;

    /**
     * 状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)
     */
    private Byte status;

    /**
     * 备注
     */
    @Length(max=32,message = "最近申请图不能超过32个字符!")
    private String remark;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

	@Column(name = "source")
	@NotNull(message = "来源不能为空")
	private Integer source;


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
     * 获取图片名称
     *
     * @return pic_name - 图片名称
     */
    public String getPicName() {
        return picName;
    }

    /**
     * 设置图片名称
     *
     * @param picName 图片名称
     */
    public void setPicName(String picName) {
        this.picName = picName;
    }

    /**
     * 获取品牌id
     *
     * @return brand_id - 品牌id
     */
    public Integer getBrandId() {
        return brandId;
    }

    /**
     * 设置品牌id
     *
     * @param brandId 品牌id
     */
    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    /**
     * 获取前端展示图
     *
     * @return miniapp_display_src - 前端展示图
     */
    public String getMiniappDisplaySrc() {
        return miniappDisplaySrc;
    }

    /**
     * 设置前端展示图
     *
     * @param miniappDisplaySrc 前端展示图
     */
    public void setMiniappDisplaySrc(String miniappDisplaySrc) {
        this.miniappDisplaySrc = miniappDisplaySrc;
    }

    /**
     * 获取最近申请图
     *
     * @return latest_apply_src - 最近申请图
     */
    public String getLatestApplySrc() {
        return latestApplySrc;
    }

    /**
     * 设置最近申请图
     *
     * @param latestApplySrc 最近申请图
     */
    public void setLatestApplySrc(String latestApplySrc) {
        this.latestApplySrc = latestApplySrc;
    }

    /**
     * 获取状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)
     *
     * @return status - 状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)
     *
     * @param status 状态(0: 审核中(提交方)/待审核(审核方) 1: 审核成功 2:审核失败)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }


	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	/**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public JSONArray getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(JSONArray templateIds) {
        this.templateIds = templateIds;
    }

    public JSONArray getMiniappDisplaySrcs() {
        return miniappDisplaySrcs;
    }

    public void setMiniappDisplaySrcs(JSONArray miniappDisplaySrcs) {
        this.miniappDisplaySrcs = miniappDisplaySrcs;
    }

    public JSONArray getLatestApplySrcs() {
        return latestApplySrcs;
    }

    public void setLatestApplySrcs(JSONArray latestApplySrcs) {
        this.latestApplySrcs = latestApplySrcs;
    }
}