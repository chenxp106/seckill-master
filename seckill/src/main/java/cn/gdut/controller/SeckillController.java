package cn.gdut.controller;

import cn.gdut.controller.result.CodeMsg;
import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillOrder;
import cn.gdut.domain.SeckillUser;
import cn.gdut.redis.GoodsKeyPrefix;
import cn.gdut.redis.RedisService;
import cn.gdut.service.GoodsService;
import cn.gdut.service.OrderService;
import cn.gdut.service.SeckillService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class SeckillController implements InitializingBean {

    @Autowired
    RedisService redisService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;


    //用于内存标记，标记库存是否为空，从而减少对redis的访问。
    private Map<Long,Boolean> localOverMap = new HashMap<>();

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

        //2 判断用户是否完成秒杀，如果没有完成，继续。否则，重复秒杀
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserAndGoodsId(user.getId(),goodsId);
        if (seckillOrder !=null){
            model.addAttribute("errmsg",CodeMsg.REPEATE_SECKILL.getMsg());
            return "miaosha_fail";
        }

        //3 完成秒杀动作
        OrderInfo orderInfo = seckillService.seckill(user,good);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",good);
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

        //将商品的库存信息存在redis中
        for (GoodsVo good : goods){
            redisService.set(GoodsKeyPrefix.seckillGoodsStockPrefix,""+good.getId(),good.getStockCount());
            localOverMap.put(good.getId(),false);//系统在启动时，标记库存不为空
        }

    }
}
