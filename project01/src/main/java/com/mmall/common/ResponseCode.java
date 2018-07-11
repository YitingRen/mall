package com.mmall.common;

public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"Need LOGIN"),
    ILIGAL_ARGUMENT(2,"ILIGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code =code;
        this.desc =desc;
    }

    //open code and desc
    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }


}
