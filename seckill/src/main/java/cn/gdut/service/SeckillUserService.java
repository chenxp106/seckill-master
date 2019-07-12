package cn.gdut.service;


import cn.gdut.domain.SeckillUser;

import javax.servlet.http.HttpServletResponse;

public interface SeckillUserService {

    public SeckillUser login(HttpServletResponse response,String moble, String password);
}
