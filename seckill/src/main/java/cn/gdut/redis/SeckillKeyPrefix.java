package cn.gdut.redis;

public class SeckillKeyPrefix extends BaseKeyPrefix {
    public SeckillKeyPrefix(int expireSeconds, String prdix) {
        super(expireSeconds, prdix);
    }

    public SeckillKeyPrefix(String prefix){
        super(prefix);
    }

    public static SeckillKeyPrefix seckillKeyPrefix = new SeckillKeyPrefix(300,"seckillVerifyCode");

    public static SeckillKeyPrefix isGoodsOver = new SeckillKeyPrefix("isGoodsOver");
}
