package cn.gdut.service.impl;

import cn.gdut.dao.GoodsDao;
import cn.gdut.domain.SeckillGoods;
import cn.gdut.service.GoodsService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsDao goodsDao;


    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    @Override
    public GoodsVo getGoodsByGoodsId(long goodsId) {
        return goodsDao.getGoodsByGoodsId(goodsId);
    }

    @Override
    public boolean reduceStock(GoodsVo goodsVo) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setGoodsId(goodsVo.getId()); //秒杀商品的id和商品的id是一样的
        int ret = goodsDao.
        return false;
    }


}
