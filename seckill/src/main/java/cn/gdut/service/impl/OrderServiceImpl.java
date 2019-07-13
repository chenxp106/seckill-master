package cn.gdut.service.impl;

import cn.gdut.dao.OrderDao;
import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.OrderService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

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

        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        //将订单信息插入到order_info表中
        long orderId = orderDao.insert(orderInfo);
        System.out.println("orderId"+orderId);

        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());

        //将秒杀订单插入到miaoshs_order中
        orderDao.insertSeckillOrder(seckillOrder);
        return orderInfo;
    }
}
