package cn.gdut.controller;


import cn.gdut.controller.result.CodeMsg;
import cn.gdut.controller.result.Result;
import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.GoodsService;
import cn.gdut.service.OrderService;
import cn.gdut.vo.GoodsVo;
import cn.gdut.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> orderInfo(SeckillUser user, @RequestParam("orderId") long orderId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 获取订单信息
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }

        // 如果订单存在，则获取订单的相关信息
        Long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(order);
        return Result.success(vo);
    }
}
