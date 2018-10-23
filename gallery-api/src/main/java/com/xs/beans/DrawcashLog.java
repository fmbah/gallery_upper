package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@ApiModel(value = "提现记录")
@Table(name = "tb_drawcash_log")
public class DrawcashLog extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "draw_cash")
    private BigDecimal drawCash;

    @Column(name = "taxation_cash")
    private BigDecimal taxationCash;

    private String type;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 真实姓名
     */
    private String realname;

    private String status;

    @Transient
    private String statusStr;

    @Column(name = "fail_msg")
    private String failMsg;

    private String remark;

    @Transient
    private String nickname;
    @Transient
    private String wxHeadImgurl;

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
     * @return user_id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return draw_cash
     */
    public BigDecimal getDrawCash() {
        return drawCash;
    }

    /**
     * @param drawCash
     */
    public void setDrawCash(BigDecimal drawCash) {
        this.drawCash = drawCash;
    }

    /**
     * @return taxation_cash
     */
    public BigDecimal getTaxationCash() {
        return taxationCash;
    }

    /**
     * @param taxationCash
     */
    public void setTaxationCash(BigDecimal taxationCash) {
        this.taxationCash = taxationCash;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
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
     * 获取真实姓名
     *
     * @return realname - 真实姓名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 设置真实姓名
     *
     * @param realname 真实姓名
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return fail_msg
     */
    public String getFailMsg() {
        return failMsg;
    }

    /**
     * @param failMsg
     */
    public void setFailMsg(String failMsg) {
        this.failMsg = failMsg;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWxHeadImgurl() {
        return wxHeadImgurl;
    }

    public void setWxHeadImgurl(String wxHeadImgurl) {
        this.wxHeadImgurl = wxHeadImgurl;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}