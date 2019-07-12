package cn.gdut.service;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.vo.GoodsVo;

public interface SeckillService {

    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo);
}
