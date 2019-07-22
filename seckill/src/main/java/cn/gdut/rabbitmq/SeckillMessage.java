package cn.gdut.rabbitmq;

import cn.gdut.domain.SeckillUser;

/**
 * 在MQ中传递秒杀信息
 * 包含参数秒杀的用户和商品的id
 */
public class SeckillMessage {

    private SeckillUser user;

    private long goodsId;

    public SeckillUser getUser() {
        return user;
    }

    public void setUser(SeckillUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
