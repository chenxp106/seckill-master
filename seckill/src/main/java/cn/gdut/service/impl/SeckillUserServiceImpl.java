package cn.gdut.service.impl;

import cn.gdut.constant.Constant;
import cn.gdut.controller.result.CodeMsg;
import cn.gdut.dao.SeckillUserDao;
import cn.gdut.domain.SeckillUser;
import cn.gdut.exception.GlobalException;
import cn.gdut.redis.RedisService;
import cn.gdut.service.SeckillUserService;
import cn.gdut.util.MD5Util;
import cn.gdut.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserServiceImpl implements SeckillUserService {

    @Autowired
    private SeckillUserDao seckillUserDao;

    //将cookie对应的用户存到第三方缓存中，用redis
    @Autowired
    private RedisService redisService;

    @Override
    public SeckillUser login(HttpServletResponse response,String moble, String password) {
        SeckillUser seckillUser = seckillUserDao.getById(Long.parseLong(moble));
        if (seckillUser == null){
            //抛出异常会被全局异常接收，全局异常会将信息传导全局异常处理器
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //判断手机号码与密码与数据库中的是否一致
        String dbPassword = seckillUser.getPassword();
        String dbSalt = seckillUser.getSalt();
        String calcPassword = MD5Util.formPassToDbPass(password,dbSalt);
        //密码不匹配
        if (!calcPassword.equals(dbPassword)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //执行到这里说明登录成功了，生成cookie
        String token = UUIDUtil.uuid();
        //每次访问都会产生一个session存储于redis和反馈给客户端，一个session对于一个user对象
        redisService.set(token,seckillUser);
        Cookie cookie = new Cookie(Constant.COOKIE_NAME_TOKEN,token);
        //将cookie存放到response中
        cookie.setPath("/");
        response.addCookie(cookie);
        return seckillUser;
    }

    @Override
    public SeckillUser getSeckillUserByToken(HttpServletResponse response, String token) {
        //先判空
        if (StringUtils.isEmpty(token)){
            return null;
        }

        SeckillUser seckillUser = redisService.get(token,SeckillUser.class);

        return seckillUser;
    }
}
