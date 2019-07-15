package cn.gdut.redis;

/**
 * 用于商品的key
 */
public class GoodsKeyPrefix extends BaseKeyPrefix{

    public GoodsKeyPrefix(int expireSeconds, String prdix) {
        super(expireSeconds, prdix);
    }
    // 缓存在redis中商品列表页面的key的前缀
    public static GoodsKeyPrefix goodsListKeyPrefix =new  GoodsKeyPrefix(60,"goodsList");

    //缓存在redis中商品详情页面key的前缀
    public static GoodsKeyPrefix goodsDetailKeyPrefix = new GoodsKeyPrefix(60,"goodsDetail");

    //缓存在redis中的商品库存前缀（缓存时间为永久）
    public static GoodsKeyPrefix seckillGoodsStockPrefix = new GoodsKeyPrefix(0,"goodsStock");
}
