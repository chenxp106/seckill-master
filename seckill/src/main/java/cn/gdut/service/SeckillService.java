package cn.gdut.service;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.vo.GoodsVo;

import java.awt.image.BufferedImage;

public interface SeckillService {

    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo);

    public BufferedImage createVerifyCode(SeckillUser user,long goodsId);
}
