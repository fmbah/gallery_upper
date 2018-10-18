package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "Admin",description = "后台帐号模型")
@Table(name = "tb_admin")
public class Admin extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 帐号
     */
    @ApiModelProperty(value = "帐号")
    @NotBlank(message = "帐号不能为空!")
    @Length(min=1,max=20,message = "帐号不能超过20个字符!")
    private String username;

    /**
     * 加密密码
     */
    @Column(name = "hashed_pwd")
    @ApiModelProperty(value = "加密密码")
    @Length(min=1,max=32,message = "加密密码不能超过32个字符!")
    private String hashedPwd;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

    @ApiModelProperty(value = "简介")
    @NotBlank(message = "简介不能为空!")
    @Length(min=1,max=256,message = "简介不能超过256个字符!")
    private String remark;

    /**
     * 角色id，默认为0
     */
    @Column(name = "role_id")
    @ApiModelProperty(value = "角色id")
    @NotNull(message = "角色id不能为空")
    private Long roleId;

    /**
     * 品牌id，默认为0
     */
    @Column(name = "brand_id")
    @ApiModelProperty(value = "品牌id")
    @NotNull(message = "品牌id不能为空")
    private Long brandId;

    @Transient
    @ApiModelProperty(value = "品牌名称")
    private String brandName;
    @Transient
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取帐号
     *
     * @return username - 帐号
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置帐号
     *
     * @param username 帐号
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取加密密码
     *
     * @return hashed_pwd - 加密密码
     */
    public String getHashedPwd() {
        return hashedPwd;
    }

    /**
     * 设置加密密码
     *
     * @param hashedPwd 加密密码
     */
    public void setHashedPwd(String hashedPwd) {
        this.hashedPwd = hashedPwd;
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
     * @return remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取角色id，默认为0
     *
     * @return role_id - 角色id，默认为0
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 设置角色id，默认为0
     *
     * @param roleId 角色id，默认为0
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * 获取品牌id，默认为0
     *
     * @return brand_id - 品牌id，默认为0
     */
    public Long getBrandId() {
        return brandId;
    }

    /**
     * 设置品牌id，默认为0
     *
     * @param brandId 品牌id，默认为0
     */
    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}