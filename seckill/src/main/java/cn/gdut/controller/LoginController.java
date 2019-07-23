package cn.gdut.controller;

import cn.gdut.controller.result.Result;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.SeckillUserService;
import cn.gdut.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private SeckillUserService  seckillUserService;

    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){

        String mobel = loginVo.getMobile();
        String password = loginVo.getPassword();
        SeckillUser seckillUser = seckillUserService.login(response,mobel,password);
        if (seckillUser == null){
            return Result.success(false);
        }
        return Result.success(true);
    }
}
