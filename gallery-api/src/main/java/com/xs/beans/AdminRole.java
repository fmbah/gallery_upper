package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(value = "AdminRole",description = "后台角色模型")
@Table(name = "tb_admin_role")
public class AdminRole extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    @NotBlank(message = "角色名称不能为空!")
    @Length(min=1,max=20,message = "角色名称不能超过20个字符!")
    private String name;

    /**
     * 简介
     */
    @ApiModelProperty(value = "角色简介")
//    @NotBlank(message = "角色简介不能为空!")
    @Length(max=20,message = "角色简介不能超过20个字符!")
    private String descri;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

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
     * 获取角色名称
     *
     * @return name - 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     *
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取简介
     *
     * @return descri - 简介
     */
    public String getDescri() {
        return descri;
    }

    /**
     * 设置简介
     *
     * @param descri 简介
     */
    public void setDescri(String descri) {
        this.descri = descri;
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
}