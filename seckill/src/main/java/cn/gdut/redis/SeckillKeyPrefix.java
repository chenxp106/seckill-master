package cn.gdut.redis;

public class SeckillKeyPrefix extends BaseKeyPrefix {
    public SeckillKeyPrefix(int expireSeconds, String prdix) {
        super(expireSeconds, prdix);
    }

    public static SeckillKeyPrefix seckillKeyPrefix = new SeckillKeyPrefix(300,"seckillVerifyCode");
}
