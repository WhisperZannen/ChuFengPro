package com.fun.chufengpro.pojo;

public class ReturnData {
    private int Code;
    private Object data;
    private String message;

    @Override
    public String toString() {
        return "ReturnData{" +
                "Code=" + Code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
