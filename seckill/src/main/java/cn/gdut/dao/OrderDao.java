package cn.gdut.dao;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao {

    @Insert("INSERT INTO order_info (user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date)"+
            " VALUES (#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    long insert(OrderInfo orderInfo);

    @Insert("INSERT INTO  seckill_order (user_id,order_id,goods_id) VALUES (#{userId},#{orderId},#{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);
}
