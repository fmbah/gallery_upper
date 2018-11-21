package com.xs.beans;

import com.xs.core.sbean.BaseBean;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_share_profit")
public class ShareProfit extends BaseBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "paymnet_id")
    private Integer paymnetId;

    private BigDecimal profit;

    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Column(name = "gmt_modified")
    private Date gmtModified;

    @Column(name = "sub_type")
    private Byte subType;

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
     * @return paymnet_id
     */
    public Integer getPaymnetId() {
        return paymnetId;
    }

    /**
     * @param paymnetId
     */
    public void setPaymnetId(Integer paymnetId) {
        this.paymnetId = paymnetId;
    }

    /**
     * @return profit
     */
    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * @param profit
     */
    public void setProfit(BigDecimal profit) {
        this.profit = profit;
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
     * @return sub_type
     */
    public Byte getSubType() {
        return subType;
    }

    /**
     * @param subType
     */
    public void setSubType(Byte subType) {
        this.subType = subType;
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