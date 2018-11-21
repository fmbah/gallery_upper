package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_brand_cdkey")
public class BrandCdkey extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "brand_id")
    private Integer brandId;

    /**
     * 激活码
     */
    private String code;

    @Column(name = "is_used")
    private Byte isUsed;

    @Column(name = "used_user_id")
    private Integer usedUserId;

    @Column(name = "used_time")
    private Date usedTime;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;


    @Transient
    @ApiModelProperty(value = "用户名称")
    private String usedUserName;
    @Transient
    @ApiModelProperty(value = "使用状态")
    private String isUsedStr;

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
     * 获取激活码
     *
     * @return code - 激活码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置激活码
     *
     * @param code 激活码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return is_used
     */
    public Byte getIsUsed() {
        return isUsed;
    }

    /**
     * @param isUsed
     */
    public void setIsUsed(Byte isUsed) {
        this.isUsed = isUsed;
    }

    /**
     * @return used_user_id
     */
    public Integer getUsedUserId() {
        return usedUserId;
    }

    /**
     * @param usedUserId
     */
    public void setUsedUserId(Integer usedUserId) {
        this.usedUserId = usedUserId;
    }

    /**
     * @return used_time
     */
    public Date getUsedTime() {
        return usedTime;
    }

    /**
     * @param usedTime
     */
    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
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

    public String getUsedUserName() {
        return usedUserName;
    }

    public void setUsedUserName(String usedUserName) {
        this.usedUserName = usedUserName;
    }

    public String getIsUsedStr() {
        return isUsedStr;
    }

    public void setIsUsedStr(String isUsedStr) {
        this.isUsedStr = isUsedStr;
    }
}