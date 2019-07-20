package cn.gdut.service;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.vo.GoodsVo;

import java.awt.image.BufferedImage;

public interface SeckillService {

    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo);

    public BufferedImage createVerifyCode(SeckillUser user,long goodsId);

    /**
     * 用于校验验证码
     * @param user 用户
     * @param goodsId 商品id
     * @param verifyCode 验证码
     * @return
     */
    public boolean checkVerifyCode(SeckillUser user,long goodsId,int verifyCode);


    /**
     * 创建秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    public String createSeckillPath(SeckillUser user,long goodsId);

    public boolean checkPath(SeckillUser user,long goodsId,String path);
}
