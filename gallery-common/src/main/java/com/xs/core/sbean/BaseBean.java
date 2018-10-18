package com.xs.core.sbean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @Auther: Fmbah
 * @Date: 18-10-10 下午4:25
 * @Description: 
 */
@ApiModel
public abstract class BaseBean implements Serializable {

    private static final long serialVersionUID = -7701523341154091991L;

    @Transient
    @ApiModelProperty(value = "开始时间")
    private String sTime;

    @Transient
    @ApiModelProperty(value = "结束时间")
    private String eTime;

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" [");
        this.toStringAppendFields(builder);
        builder.append("]");
        return builder.toString();
    }
    protected void toStringAppendFields(StringBuilder builder) {
    }
}
