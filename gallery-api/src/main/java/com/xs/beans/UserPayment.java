package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@ApiModel(value = "支付记录")
@Table(name = "tb_user_payment")
public class UserPayment extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_no")
    private String orderNo;

    private BigDecimal amount;

    private String status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "gmt_payment")
    private Date gmtPayment;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 支付类型：
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    @Column(name = "recharge_type")
    private Byte rechargeType;

    @Column(name = "cdk_code")
    private String cdkCode;
    @Column(name = "remark")
    private String remark;

    @Transient
    @ApiModelProperty(value = "支付类型：5: 金卡会员 6: 铂金会员 10: 钻石会员")
    private String rechargeTypeStr;


    @Transient
    @ApiModelProperty(value = "用户昵称")
    private String userName;
    @Transient
    @ApiModelProperty(value = "用户头像")
    private String wxHeadImgurl;
    @Transient
    @ApiModelProperty(value = "一级分益人id")
    private Integer sp1Id;
    @Transient
    @ApiModelProperty(value = "一级分益人名称")
    private String sp1Name;
    @Transient
    @ApiModelProperty(value = "一级分益人获益金额")
    private BigDecimal sp1Profit;
    @Transient
    @ApiModelProperty(value = "二级分益人id")
    private Integer sp2Id;
    @Transient
    @ApiModelProperty(value = "二级分益人名称")
    private String sp2Name;
    @Transient
    @ApiModelProperty(value = "二级分益人获益金额")
    private BigDecimal sp2Profit;

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
     * @return order_no
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
     * @return transaction_id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return gmt_payment
     */
    public Date getGmtPayment() {
        return gmtPayment;
    }

    /**
     * @param gmtPayment
     */
    public void setGmtPayment(Date gmtPayment) {
        this.gmtPayment = gmtPayment;
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
     * 获取支付类型：
5: 金卡会员
6: 铂金会员
10: 钻石会员
     *
     * @return recharge_type - 支付类型：
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    public Byte getRechargeType() {
        return rechargeType;
    }

    /**
     * 设置支付类型：
5: 金卡会员
6: 铂金会员
10: 钻石会员
     *
     * @param rechargeType 支付类型：
5: 金卡会员
6: 铂金会员
10: 钻石会员
     */
    public void setRechargeType(Byte rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWxHeadImgurl() {
        return wxHeadImgurl;
    }

    public void setWxHeadImgurl(String wxHeadImgurl) {
        this.wxHeadImgurl = wxHeadImgurl;
    }

    public Integer getSp1Id() {
        return sp1Id;
    }

    public void setSp1Id(Integer sp1Id) {
        this.sp1Id = sp1Id;
    }

    public String getSp1Name() {
        return sp1Name;
    }

    public void setSp1Name(String sp1Name) {
        this.sp1Name = sp1Name;
    }

    public BigDecimal getSp1Profit() {
        return sp1Profit;
    }

    public void setSp1Profit(BigDecimal sp1Profit) {
        this.sp1Profit = sp1Profit;
    }

    public Integer getSp2Id() {
        return sp2Id;
    }

    public void setSp2Id(Integer sp2Id) {
        this.sp2Id = sp2Id;
    }

    public String getSp2Name() {
        return sp2Name;
    }

    public void setSp2Name(String sp2Name) {
        this.sp2Name = sp2Name;
    }

    public BigDecimal getSp2Profit() {
        return sp2Profit;
    }

    public void setSp2Profit(BigDecimal sp2Profit) {
        this.sp2Profit = sp2Profit;
    }

    public String getRechargeTypeStr() {
        return rechargeTypeStr;
    }

    public void setRechargeTypeStr(String rechargeTypeStr) {
        this.rechargeTypeStr = rechargeTypeStr;
    }

    public String getCdkCode() {
        return cdkCode;
    }

    public void setCdkCode(String cdkCode) {
        this.cdkCode = cdkCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}