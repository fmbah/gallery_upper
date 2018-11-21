package com.xs.beans;

import com.xs.core.sbean.BaseBean;

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

    private String remark;

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
}