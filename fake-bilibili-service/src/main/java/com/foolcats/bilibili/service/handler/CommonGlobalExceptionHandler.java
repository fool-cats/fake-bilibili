package com.foolcats.bilibili.service.handler;

import com.foolcats.bilibili.domain.JsonResponse;
import com.foolcats.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/*
highest priority/order exception handler
* */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {

//    just like its name

    /*
    *
    * HttpServletRequest is used to encapsulate the request from frontend
    *
    * */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String errorMsg = e.getMessage();
        if(e instanceof ConditionException){
            String errorCode = ((ConditionException)e).getCode();
//            it can be used in this scenario, when user's input password is incorrect
//            we can throw exception which is ConditionException implemented by ourself.
            return new JsonResponse<>(errorCode, errorMsg);
        }else{
            return new JsonResponse<>("500",errorMsg);
        }
    }
}
