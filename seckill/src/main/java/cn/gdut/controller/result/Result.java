package cn.gdut.controller.result;


public class Result<T> {
    private int code;
    private String msg;
    private T data;

    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg serverError){
        if (serverError == null){
            return;
        }
        this.code = serverError.getCode();
        this.msg = serverError.getMsg();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 成功时的返回的结果
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    public static <T> Result<T> error(CodeMsg serverError){
        return new Result<T>(serverError);
    }
}
