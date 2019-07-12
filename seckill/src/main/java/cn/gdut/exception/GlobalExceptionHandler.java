package cn.gdut.exception;


import cn.gdut.controller.result.CodeMsg;
import cn.gdut.controller.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        e.printStackTrace();

        //如果所拦截的异常是自定义的全局异常，这按自定义的处理方式，否则按默认的处理方式
        if (e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return Result.error(exception.getCodeMsg());
        }
        else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
