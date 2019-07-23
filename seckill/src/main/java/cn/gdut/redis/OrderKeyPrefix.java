package cn.gdut.redis;

public class OrderKeyPrefix extends BaseKeyPrefix {
    public OrderKeyPrefix(int expireSeconds, String prdix) {
        super(expireSeconds, prdix);
    }

    public OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    // 秒杀订单的前缀
    public static OrderKeyPrefix getSeckillOrderByUidGid = new OrderKeyPrefix("getSeckillOrderByUidGid");
}
