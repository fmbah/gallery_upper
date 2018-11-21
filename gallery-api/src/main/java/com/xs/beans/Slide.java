package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ApiModel(description = "轮播图", value = "Slide")
@Table(name = "tb_slide")
public class Slide extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 位置（首页：1；分享获益：2；会员权益：3；）
     */
    @ApiModelProperty(value = "位置（首页：1；分享获益：2；会员权益：3；）")
    @NotNull
    private Byte type;

    @Column(name = "image_url")
    @ApiModelProperty(value = "图片地址")
    @NotBlank(message = "图片地址不能为空!")
    @Length(min=1,max=192,message = "图片地址不能超过192个字符!")
    private String imageUrl;

    @Column(name = "link_url")
    @ApiModelProperty(value = "链接地址")
    @NotBlank(message = "链接地址不能为空!")
    @Length(min=1,max=192,message = "链接地址不能超过192个字符!")
    private String linkUrl;

    @Column(name = "gmt_create")
    @ApiModelProperty(hidden = true)
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    @ApiModelProperty(hidden = true)
    private Date gmtModified;

    /**
     * 权重排序
     */
    @ApiModelProperty(value = "权重排序")
    @NotNull
    private Byte weight;

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
     * 获取位置（首页：1；分享获益：2；会员权益：3；）
     *
     * @return type - 位置（首页：1；分享获益：2；会员权益：3；）
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置位置（首页：1；分享获益：2；会员权益：3；）
     *
     * @param type 位置（首页：1；分享获益：2；会员权益：3；）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * @return image_url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return link_url
     */
    public String getLinkUrl() {
        return linkUrl;
    }

    /**
     * @param linkUrl
     */
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
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
     * 获取权重排序
     *
     * @return weight - 权重排序
     */
    public Byte getWeight() {
        return weight;
    }

    /**
     * 设置权重排序
     *
     * @param weight 权重排序
     */
    public void setWeight(Byte weight) {
        this.weight = weight;
    }
}