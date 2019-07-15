package cn.gdut.service;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.vo.GoodsVo;

public interface OrderService {

    public OrderInfo createOrder(SeckillUser user, GoodsVo goods);

    public SeckillOrder getSeckillOrderByUserAndGoodsId(Long userId,long goodsId);

}
