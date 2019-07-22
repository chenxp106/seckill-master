package cn.gdut.rabbitmq;

import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.redis.RedisService;
import cn.gdut.service.GoodsService;
import cn.gdut.service.OrderService;
import cn.gdut.service.SeckillService;
import cn.gdut.vo.GoodsVo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MQ消息接收者
 * 消息绑定在队列进行监听，可以接收到队列中的消息
 */
@Service
public class MQReceiver {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveMiaoshaInfo(String message){
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);

        //获取秒杀用户的信息和商品id
        SeckillUser user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();

        // 获取商品的库存
        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);
        Integer count = goods.getStockCount();
        if (count <= 0){
            return;
        }

        // 判断是否秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserAndGoodsId(user.getId(), goodsId);
        if (order != null){
            return;
        }

        // 秒杀操作，下订单，写入秒杀订单中
        seckillService.seckill(user,goods);
    }
}
