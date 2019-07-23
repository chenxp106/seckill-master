package cn.gdut.service.impl;

import cn.gdut.dao.OrderDao;
import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.redis.OrderKeyPrefix;
import cn.gdut.redis.RedisService;
import cn.gdut.service.OrderService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    /**
     * 创建订单
     * @param user 秒杀的用户
     * @param goods 秒杀的商品
     * @return 订单的详情
     */
    @Override
    @Transactional
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

        //将秒杀的信息存到redis中
        redisService.set(OrderKeyPrefix.getSeckillOrderByUidGid,":" + user.getId() + "_" + goods.getId(),seckillOrder);

        return orderInfo;
    }

    /**
     * 根据用户id和goodsid判断用户是否下过订单
     * @param userId 用户id
     * @param goodsId 商品id
     * @return
     */
    @Override
    public SeckillOrder getSeckillOrderByUserAndGoodsId(Long userId, long goodsId) {

        // 从redis缓存中获取是否下过订单,减少对数据库的访问。
        SeckillOrder order = redisService.get(OrderKeyPrefix.getSeckillOrderByUidGid, ":" + userId + "_" + goodsId, SeckillOrder.class);
        if (order != null){
            return order;
        }

        //将得到的订单放到redis缓存中
        SeckillOrder seckillOrderByUserAndGoodsId = orderDao.getSeckillOrderByUserAndGoodsId(userId, goodsId);
        if (seckillOrderByUserAndGoodsId != null){
            redisService.set(OrderKeyPrefix.getSeckillOrderByUidGid,":"+ userId + "_" + goodsId,seckillOrderByUserAndGoodsId);
        }

        return seckillOrderByUserAndGoodsId;
    }

    @Override
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
