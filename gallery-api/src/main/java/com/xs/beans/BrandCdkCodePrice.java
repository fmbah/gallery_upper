package com.xs.beans;

import com.xs.core.sbean.BaseBean;

import java.math.BigDecimal;

/**
 * @ClassName BrandCdkCodePrice
 * @Description
 * @Author root
 * @Date 19-1-2 上午10:34
 * @Version 1.0
 **/
public class BrandCdkCodePrice extends BaseBean {

    private Integer brandId;
    private String brandName;
    private String payName;
    private BigDecimal price;


    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
