package cn.gdut.controller;

import cn.gdut.controller.result.CodeMsg;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.GoodsService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class SeckillController implements InitializingBean {


    @Autowired
    GoodsService goodsService;

    @RequestMapping("/do_miaosha")
    public String doMiaosha(Model model, SeckillUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);

        //如果用户为空，直接返回登录页面
        if (user == null){
            return "login";
        }
        // 若用户不为空，说明用户已经登录，可以继续执行下一个步骤

        //1 先判断库存是否为空，
        GoodsVo good = goodsService.getGoodsByGoodsId(goodsId);
        int stockCount = good.getStockCount();
        if (stockCount <= 0){
            //秒杀失败
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "miaosha_fail";
        }

        //完成秒杀动作

        return "order_detail";
    }

    /**
     * 系统初始化时执行
     * 系统初始化的时候从数据库中将商品信息查询出来。包括秒杀信息和商品的基本信息
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goods = goodsService.listGoodsVo();
        if (goods == null){
            return ;
        }

    }
}
