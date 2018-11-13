package com.xs.beans;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName Base64ToUrl
 * @Description
 * @Author root
 * @Date 18-11-13 上午10:28
 * @Version 1.0
 **/
public class Base64ToUrl implements Serializable {

    @NotNull
    private String base64Var;

    public String getBase64Var() {
        return base64Var;
    }

    public void setBase64Var(String base64Var) {
        this.base64Var = base64Var;
    }
}
