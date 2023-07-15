package com.foolcats.bilibili.domain;

public class JsonResponse<T> {
    private String statusCode;

    private String message;

//    we may have int, string,object ,list return types, so we use generic
    private T data;


    public JsonResponse(String statusCode, String message){
        this.statusCode = statusCode;
        this.message = message;
    }

    public JsonResponse(T data){
        this.data = data;
        message = "success";
        statusCode = "0";
    }

//    default success status code and message without returning content to frontend
//    this method used in some scenarios like the request is
//    successful,but we don't want to send content to the frontend,we just send message and status code
    public static  JsonResponse<String> success(){
//        call the constructor with default status and message
//        success and 0
        return new JsonResponse<>(null);
    }

//    when successfully sending content to the frontend
    public static  JsonResponse<String> success(String data){
//        this method used in some scenarios like when user is logged in, we send token as string
        return new JsonResponse<>(data);
    }

//    default failure status and message

    public static JsonResponse<String> failure(){
        return new JsonResponse<>("1","failure");
    }

//    custom failure message and status code
    public static JsonResponse<String> failure(String statusCode,String message){
//        used to return specific status code and message
        return new JsonResponse<>(statusCode,message);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
