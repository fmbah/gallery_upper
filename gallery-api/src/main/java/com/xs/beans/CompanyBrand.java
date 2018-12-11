package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@ApiModel(value = "CompanyBrand",description = "品牌")
@Table(name = "tb_company_brand")
public class CompanyBrand extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(value = "品牌个人号用户id")
    private Integer brandPersonalUserid;
    @Transient
    @ApiModelProperty(value = "品牌个人号用户名称")
    private String brandPersonalUserName;
    @Transient
    @ApiModelProperty(value = "品牌个人号用户头像")
    private String brandPersonalUserPic;

    @ApiModelProperty(value = "品牌名称")
    @NotBlank(message = "品牌名称不能为空!")
    @Length(min=1,max=45,message = "品牌名称不能超过45个字符!")
    private String name;

    @ApiModelProperty(value = "简介")
    @NotBlank(message = "简介不能为空!")
    @Length(min=1,max=255,message = "简介不能超过45个字符!")
    private String introduction;

    @Column(name = "contact_person")
    @ApiModelProperty(value = "联系人")
    @NotBlank(message = "联系人不能为空!")
    @Length(min=1,max=12,message = "联系人不能超过12个字符!")
    private String contactPerson;

    @Column(name = "contact_phone")
    @ApiModelProperty(value = "联系电话")
    @NotBlank(message = "联系电话不能为空!")
    @Length(min=1,max=12,message = "联系电话不能超过16个字符!")
    private String contactPhone;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

    /**
     * 品牌过期时间
     */
    @Column(name = "expired_time")
    @ApiModelProperty(value = "品牌过期时间,新增时默认一年后,修改必填")
    private Date expiredTime;


    @Transient
    @ApiModelProperty(value = "模板数量")
    private Integer templateCount;
    @Transient
    @ApiModelProperty(value = "模板数量")
    private Integer userCount;
    @Transient
    @ApiModelProperty(value = "模板集合")
    private List<Template> templateList;

    @Transient
    @ApiModelProperty(value = "激活码数量")
    private Integer ckdNum;

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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return contact_person
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * @param contactPerson
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * @return contact_phone
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * @param contactPhone
     */
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
     * 获取品牌过期时间
     *
     * @return expired_time - 品牌过期时间
     */
    public Date getExpiredTime() {
        return expiredTime;
    }

    /**
     * 设置品牌过期时间
     *
     * @param expiredTime 品牌过期时间
     */
    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Integer getTemplateCount() {
        return templateCount;
    }

    public void setTemplateCount(Integer templateCount) {
        this.templateCount = templateCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public List<Template> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<Template> templateList) {
        this.templateList = templateList;
    }

    public Integer getCkdNum() {
        return ckdNum;
    }

    public void setCkdNum(Integer ckdNum) {
        this.ckdNum = ckdNum;
    }

    public Integer getBrandPersonalUserid() {
        return brandPersonalUserid;
    }

    public void setBrandPersonalUserid(Integer brandPersonalUserid) {
        this.brandPersonalUserid = brandPersonalUserid;
    }

    public String getBrandPersonalUserName() {
        return brandPersonalUserName;
    }

    public void setBrandPersonalUserName(String brandPersonalUserName) {
        this.brandPersonalUserName = brandPersonalUserName;
    }

    public String getBrandPersonalUserPic() {
        return brandPersonalUserPic;
    }

    public void setBrandPersonalUserPic(String brandPersonalUserPic) {
        this.brandPersonalUserPic = brandPersonalUserPic;
    }
}