package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_user")
public class User extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "wx_openid")
    private String wxOpenid;

    @Column(name = "wx_unionid")
    private String wxUnionid;

    /**
     * 小程序openid
     */
    @Column(name = "wx_mini_openid")
    private String wxMiniOpenid;

    @Column(name = "wx_sex")
    private Byte wxSex;

    @Column(name = "wx_headimgurl")
    private String wxHeadimgurl;

    @Column(name = "member_expired")
    private Date memberExpired;

    /**
     * 推荐者ID
     */
    @Column(name = "recommend_id")
    private Integer recommendId;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 0: 非会员
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    @Column(name = "member_type")
    private Byte memberType;

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

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @Transient
    @ApiModelProperty(value = "加入品牌数")
    private Integer joinBrandCount;
    @Transient
    @ApiModelProperty(value = "收益人(推荐人)id")
    private Integer profitUserId;
    @Transient
    @ApiModelProperty(value = "收益人(推荐人)名称")
    private String profitUserName;
    @Transient
    @ApiModelProperty(value = "推荐人数(邀请用户数)")
    private Integer recommendCount;
    @Transient
    @ApiModelProperty(value = "分享获益总额")
    private BigDecimal shareProfitAmount;
    @Transient
    @ApiModelProperty(value = "是否会员")
    private String isMemberStr;
    @Transient
    @ApiModelProperty(value = "会员类型")
    private String memberTypeStr;

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


    public Boolean getAgent() {
        return isAgent;
    }

    public void setAgent(Boolean agent) {
        isAgent = agent;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getJoinBrandCount() {
        return joinBrandCount;
    }

    public void setJoinBrandCount(Integer joinBrandCount) {
        this.joinBrandCount = joinBrandCount;
    }

    public Integer getProfitUserId() {
        return profitUserId;
    }

    public void setProfitUserId(Integer profitUserId) {
        this.profitUserId = profitUserId;
    }

    public String getProfitUserName() {
        return profitUserName;
    }

    public void setProfitUserName(String profitUserName) {
        this.profitUserName = profitUserName;
    }

    public Integer getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(Integer recommendCount) {
        this.recommendCount = recommendCount;
    }

    public BigDecimal getShareProfitAmount() {
        return shareProfitAmount;
    }

    public void setShareProfitAmount(BigDecimal shareProfitAmount) {
        this.shareProfitAmount = shareProfitAmount;
    }

    public String getIsMemberStr() {
        return isMemberStr;
    }

    public void setIsMemberStr(String isMemberStr) {
        this.isMemberStr = isMemberStr;
    }

    public String getMemberTypeStr() {
        return memberTypeStr;
    }

    public void setMemberTypeStr(String memberTypeStr) {
        this.memberTypeStr = memberTypeStr;
    }
}