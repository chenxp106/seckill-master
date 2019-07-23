package cn.gdut.vo;

import javax.validation.constraints.NotNull;

/**
 * 用于接收客户第请求中的表单参数
 * 使用JSR303完成参数校验
 */
public class LoginVo {

    @NotNull

    private String mobile;

    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
