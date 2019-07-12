package cn.gdut.exception;

import cn.gdut.controller.result.CodeMsg;

public class GlobalException extends RuntimeException{

    private CodeMsg codeMsg;

    /**
     * 使用构造器接收CodeMsg
     * @param codeMsg
     */
    public GlobalException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg(){
        return codeMsg;
    }
}
