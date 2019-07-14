package cn.gdut.redis;

import cn.gdut.constant.Constant;

public class SeckillUserKeyPrefix extends BaseKeyPrefix {


    public SeckillUserKeyPrefix(int expireSeconds, String prdix) {
        super(expireSeconds, prdix);
    }

    public static SeckillUserKeyPrefix token = new SeckillUserKeyPrefix(Constant.USER_TOKEN_EXPIRE,"token");

    //用于存储用户对象到redis的key前缀
    public static SeckillUserKeyPrefix getSeckillUserById = new SeckillUserKeyPrefix(0,"id");
}
