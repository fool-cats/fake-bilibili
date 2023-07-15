package com.foolcats.bilibili.domain.exception;

/*
* Custom exception, enforce RuntimeException to show status code
* */
public class ConditionException extends RuntimeException{

//    used for serialization version number
    private static final long serialVersionUID = 1L;

//    Json Response code, runtime exception without code message
    private String code;

    public ConditionException(String code, String name){
//        runtime exception's constructor
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
