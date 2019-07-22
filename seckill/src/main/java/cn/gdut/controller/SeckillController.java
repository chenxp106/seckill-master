package cn.gdut.controller;

import cn.gdut.controller.result.CodeMsg;
import cn.gdut.controller.result.Result;
import cn.gdut.domain.OrderInfo;
import cn.gdut.rabbitmq.MQSender;
import cn.gdut.rabbitmq.SeckillMessage;
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
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.awt.image.BufferedImage;
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

    @Autowired
    MQSender sender;


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
     * 异步请求 $("#")
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVertifyCode(HttpServletResponse response, SeckillUser user,
                                                @RequestParam("goodsId") long goodsId){
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //创建验证码
        try {
            BufferedImage image = seckillService.createVerifyCode(user,goodsId);
            ServletOutputStream out = response.getOutputStream();
            //将图片写入到respon中
            ImageIO.write(image,"JPEG",out);
            out.close();
            out.flush();
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }

    }

    /**
     * 获取秒杀的接口地址
     * 每一次点解秒杀，都会生成一个随机的秒杀地址返回给客户端
     * @param model model
     * @param user 用户
     * @param goodsId 商品id
     * @param verifyCode 验证码
     * @return 被隐藏的接口地址
     */
    @RequestMapping(value = "path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(Model model,
                                         SeckillUser user,
                                         @PathParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode" ,defaultValue = "0") int verifyCode){
        model.addAttribute("user",user);

        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //校验验证码
        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if (!check){
            //验证码不正确
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //验证通过，获取秒杀路径
        String path = seckillService.createSeckillPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/{path}/do_miaosha_static",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaoshaStatic(Model model,SeckillUser user,
                                           @RequestParam("goodsId") long goodsId,
                                           @PathVariable("path") String path){
        model.addAttribute("user",user);

        //如果用户为空，则返回登录页面
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        //校验path是否正确
        boolean checkPath = seckillService.checkPath(user, goodsId, path);
        if (!checkPath){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //通过内存标记，减少对redis的访问，秒杀结束才继续访问redis
        Boolean over = localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //预减库存
        Long stock = redisService.decr(GoodsKeyPrefix.seckillGoodsStockPrefix,"" + goodsId);
        if (stock < 0){

            //秒杀结束，标记该商品已经结束
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断重复秒杀
        SeckillOrder order = orderService.getSeckillOrderByUserAndGoodsId(user.getId(), goodsId);
        if (order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }

        // 商品有库存且用户为秒杀商品，则将请求放入到MQ，消息队列中。集中进行处理
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);

        //放入到消息队列中
        sender.sendMiaoshaMessage(message);
        return Result.success(0);
    }

    @RequestMapping(value = "/result" ,method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,
                                      SeckillUser user,
                                      @RequestParam("goodsId") long goodsId){
        model.addAttribute("user",user);

        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long result = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }


    /**
     * 系统初始化时执行
     * 系统初始化的时候从数据库中将商品信息查询出来。包括秒杀信息和商品的基本信息
     * @throws Exception
     */
    @RequestMapping(value = "")
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
