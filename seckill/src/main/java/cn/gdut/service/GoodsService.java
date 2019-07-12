package cn.gdut.service;

import cn.gdut.vo.GoodsVo;

import java.util.List;

public interface GoodsService {

    public List<GoodsVo> listGoodsVo();

    public GoodsVo getGoodsByGoodsId(long goodsId);

    public boolean reduceStock(GoodsVo goodsVo);

}
