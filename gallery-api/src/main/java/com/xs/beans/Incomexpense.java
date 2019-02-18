package com.xs.beans;

import com.xs.core.sbean.BaseBean;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_incomexpense")
public class Incomexpense extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String type;

    private BigDecimal income;

    private BigDecimal expense;

    private BigDecimal balance;

    private Date tradedate;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "share_profit_id")
    private Integer shareProfitId;

    @Transient
    @ApiModelProperty(name = "分享获益名称")
    private String shareProfitName;
    @Column(name = "sub_type")
    private Byte subType;
    @Column(name = "payment_id")
    private Integer paymentId;


    private String remark;

    @Transient
    private String nickName;
    @Transient
    private String nickName1;
    @Transient
    private String userPic;
    @Transient
    private String userPic1;
    @Transient
    private String payType;
    @Transient
    private BigDecimal payAmount;

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
     * @return income
     */
    public BigDecimal getIncome() {
        return income;
    }

    /**
     * @param income
     */
    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    /**
     * @return expense
     */
    public BigDecimal getExpense() {
        return expense;
    }

    /**
     * @param expense
     */
    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }

    /**
     * @return balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @param balance
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * @return tradedate
     */
    public Date getTradedate() {
        return tradedate;
    }

    /**
     * @param tradedate
     */
    public void setTradedate(Date tradedate) {
        this.tradedate = tradedate;
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
     * @return share_profit_id
     */
    public Integer getShareProfitId() {
        return shareProfitId;
    }

    /**
     * @param shareProfitId
     */
    public void setShareProfitId(Integer shareProfitId) {
        this.shareProfitId = shareProfitId;
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

    public String getShareProfitName() {
        return shareProfitName;
    }

    public void setShareProfitName(String shareProfitName) {
        this.shareProfitName = shareProfitName;
    }

    public Byte getSubType() {
        return subType;
    }

    public void setSubType(Byte subType) {
        this.subType = subType;
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getNickName1() {
        return nickName1;
    }

    public void setNickName1(String nickName1) {
        this.nickName1 = nickName1;
    }

    public String getUserPic1() {
        return userPic1;
    }

    public void setUserPic1(String userPic1) {
        this.userPic1 = userPic1;
    }
}