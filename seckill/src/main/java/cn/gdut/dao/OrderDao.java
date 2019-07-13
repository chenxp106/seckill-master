package cn.gdut.dao;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;

@Mapper
public interface OrderDao {

    @Insert("INSERT INTO order_info (user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date)"+
            " VALUES (#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    //查询出插入订单信息的表的id，并返回
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "SELECT last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("INSERT INTO  seckill_order (user_id,order_id,goods_id) VALUES (#{userId},#{orderId},#{goodsId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

}
