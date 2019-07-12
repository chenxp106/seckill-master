package cn.gdut.service.impl;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.OrderService;
import cn.gdut.vo.GoodsVo;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    /**
     * 创建订单
     * @param user 秒杀的用户
     * @param goods 秒杀的商品
     * @return 订单的详情
     */
    @Override
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {

        OrderInfo orderInfo = new OrderInfo();
        SeckillOrder seckillOrder = new SeckillOrder();

        return null;
    }
}
