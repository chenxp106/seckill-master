package cn.gdut.dao;

import cn.gdut.domain.SeckillGoods;
import cn.gdut.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    /**
     * 查询所有商品的信息，包括秒杀信息
     * @return lis
     */
    @Select("SELECT g.*,mg.stock_count,mg.`start_date`,mg.`end_date`,mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON g.`id`= mg.`goods_id`;")
    List<GoodsVo> listGoodsVo();

    /**
     * 通过商品id查找商品的所有信息
     * @param goodsId
     * @return
     */
    @Select("SELECT g.*,mg.stock_count,mg.`start_date`,mg.`end_date`,mg.seckill_price FROM seckill_goods mg LEFT JOIN goods g ON g.`id`= mg.`goods_id`WHERE goods_id = #{goodsId}")
    GoodsVo getGoodsByGoodsId(@Param("goodsId") Long goodsId);

    @Update("UPDATE seckill_goods SET stock_count = stock_count-1 WHERE goods_id=#{goodsId} AND stock_count>0;")
    int reduceStack(SeckillGoods seckillGoods);
}
