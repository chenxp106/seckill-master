package cn.gdut.rabbitmq;

import cn.gdut.redis.RedisService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息的发送者
 */
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    public void sendMiaoshaMessage(SeckillMessage message){
        String msg = RedisService.beanToString(message);

        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);
    }
}
