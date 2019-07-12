package cn.gdut.service.impl;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.GoodsService;
import cn.gdut.service.SeckillService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    GoodsService goodsService;

    /**
     * 秒杀操作
     * 1 减库存
     * 2 将生成的订单写入到miaosha_order表中
     * @param user 秒杀的用户
     * @param goodsVo 秒杀的商品
     * @return 生成的订单详情
     */
    @Override
    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo) {
        // 减库存
        boolean success = goodsService.reduceStock(goodsVo);
        //不成功
        if (!success){

        }
        //2 生成订单
        return null;
    }
}
