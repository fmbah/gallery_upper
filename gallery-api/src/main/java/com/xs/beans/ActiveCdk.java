package com.xs.beans;

import com.xs.core.sbean.BaseBean;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "vw_active_cdk")
public class ActiveCdk extends BaseBean {
    @Column(name = "brand_id")
    private Integer brandId;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 激活码
     */
    @Id
    private String code;

    @Column(name = "used_user_id")
    private Integer usedUserId;

    @Column(name = "used_time")
    private Date usedTime;

    private String name;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    private String nickname;

    @Column(name = "wx_sex")
    private Byte wxSex;

    @Column(name = "wx_headimgurl")
    private String wxHeadimgurl;

    @Column(name = "member_expired")
    private Date memberExpired;

    @Column(name = "wx_openid")
    private String wxOpenid;

    /**
     * 小程序openid
     */
    @Column(name = "wx_mini_openid")
    private String wxMiniOpenid;

    @Column(name = "wx_unionid")
    private String wxUnionid;

    /**
     * 推荐者ID
     */
    @Column(name = "recommend_id")
    private Integer recommendId;

    /**
     * 是否代理
     */
    @Column(name = "is_agent")
    private Boolean isAgent;

    /**
     * 收益余额
     */
    @Column(name = "cash_balance")
    private BigDecimal cashBalance;

    /**
     * 0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    @Column(name = "member_type")
    private Byte memberType;

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
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return wx_sex
     */
    public Byte getWxSex() {
        return wxSex;
    }

    /**
     * @param wxSex
     */
    public void setWxSex(Byte wxSex) {
        this.wxSex = wxSex;
    }

    /**
     * @return wx_headimgurl
     */
    public String getWxHeadimgurl() {
        return wxHeadimgurl;
    }

    /**
     * @param wxHeadimgurl
     */
    public void setWxHeadimgurl(String wxHeadimgurl) {
        this.wxHeadimgurl = wxHeadimgurl;
    }

    /**
     * @return member_expired
     */
    public Date getMemberExpired() {
        return memberExpired;
    }

    /**
     * @param memberExpired
     */
    public void setMemberExpired(Date memberExpired) {
        this.memberExpired = memberExpired;
    }

    /**
     * @return wx_openid
     */
    public String getWxOpenid() {
        return wxOpenid;
    }

    /**
     * @param wxOpenid
     */
    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    /**
     * 获取小程序openid
     *
     * @return wx_mini_openid - 小程序openid
     */
    public String getWxMiniOpenid() {
        return wxMiniOpenid;
    }

    /**
     * 设置小程序openid
     *
     * @param wxMiniOpenid 小程序openid
     */
    public void setWxMiniOpenid(String wxMiniOpenid) {
        this.wxMiniOpenid = wxMiniOpenid;
    }

    /**
     * @return wx_unionid
     */
    public String getWxUnionid() {
        return wxUnionid;
    }

    /**
     * @param wxUnionid
     */
    public void setWxUnionid(String wxUnionid) {
        this.wxUnionid = wxUnionid;
    }

    /**
     * 获取推荐者ID
     *
     * @return recommend_id - 推荐者ID
     */
    public Integer getRecommendId() {
        return recommendId;
    }

    /**
     * 设置推荐者ID
     *
     * @param recommendId 推荐者ID
     */
    public void setRecommendId(Integer recommendId) {
        this.recommendId = recommendId;
    }

    /**
     * 获取是否代理
     *
     * @return is_agent - 是否代理
     */
    public Boolean getIsAgent() {
        return isAgent;
    }

    /**
     * 设置是否代理
     *
     * @param isAgent 是否代理
     */
    public void setIsAgent(Boolean isAgent) {
        this.isAgent = isAgent;
    }

    /**
     * 获取收益余额
     *
     * @return cash_balance - 收益余额
     */
    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    /**
     * 设置收益余额
     *
     * @param cashBalance 收益余额
     */
    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    /**
     * 获取0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     *
     * @return member_type - 0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    public Byte getMemberType() {
        return memberType;
    }

    /**
     * 设置0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     *
     * @param memberType 0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    public void setMemberType(Byte memberType) {
        this.memberType = memberType;
    }
}