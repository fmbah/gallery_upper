package com.xs.core;


import java.io.Serializable;

public class ResponseBean  implements Serializable {

    private static final long serialVersionUID = 100003L;

    // http 状态码
    private int code;

    // 返回信息
    private String msg;

    // 返回的数据
    private Object data;

    public ResponseBean(){}

    public ResponseBean(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseBean setCode(ResultCode resultCode) {
        this.code = resultCode.code();
        return this;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResponseBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseBean setData(Object data) {
        this.data = data;
        return this;
    }

    public String toJson() {
        StringBuffer buf = new StringBuffer("");
        return buf.append("{")
                .append("\"code\":\"").append(code).append("\"")
                .append(",\"msg\":\"").append(msg).append("\"")
                .append(",\"data\":").append(data).append("}")
                .toString();
    }


    public String toString() {
        StringBuffer buf = new StringBuffer("");
        buf.append("{")
                .append("\"code\":\"").append(code).append("\"")
                .append(",\"msg\":\"").append(msg).append("\"");

        if(data == null || data.equals("")) {
            buf.append(",\"data\":\"\"}");
        } else {
            buf.append(",\"data\":\"").append(data).append("\"}");
        }

        return buf.toString();
    }


//    @Override
//    public String toString() {
//        return JSON.toJSONString(this);
//    }
}
