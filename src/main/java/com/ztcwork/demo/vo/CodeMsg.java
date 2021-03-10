package com.ztcwork.demo.vo;

public class CodeMsg {

    private int code;
    private String msg;
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
