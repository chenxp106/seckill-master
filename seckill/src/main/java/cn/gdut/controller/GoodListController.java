package cn.gdut.controller;


import cn.gdut.controller.result.Result;
import cn.gdut.domain.SeckillUser;
import cn.gdut.redis.GoodsKeyPrefix;
import cn.gdut.redis.RedisService;
import cn.gdut.service.GoodsService;
import cn.gdut.vo.GoodsDetailVo;
import cn.gdut.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodListController {

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toList(HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
                         SeckillUser user){

        model.addAttribute("user",user);

        String html = null;

        //1，从redis缓存中获取html
        html = redisService.get(GoodsKeyPrefix.goodsListKeyPrefix,"",String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        //2、如果不在redis中，则需要手动渲染，从数据库中获取数据


        //将查询到的商品渲染到页面中
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsVoList);

        //渲染html
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());

        //第一个参数为渲染的html文件名，第二个参数为web相应的上下文
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);

        //如果html页面不为空，则将页面缓存在redis中
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.goodsListKeyPrefix,"",html);
        }
        return html;
    }

    /**
     *处理商品详情页（未做页面静态处理）
     * @param response reson
     * @param request  req
     * @param model 页面的域对象
     * @param seckillUser 用户信息
     * @param goodsId 商品id
     * @return 商品详情页
     */
    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail(
            HttpServletResponse response,
            HttpServletRequest request,
            Model model,
            SeckillUser seckillUser,
            //获取参数
            @PathVariable("goodsId") long goodsId
    ){
        //从redis中获取详情数据，
        String html = null;
        html = redisService.get(GoodsKeyPrefix.goodsDetailKeyPrefix,""+goodsId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }

        //如果没有缓存，则需要手动渲染页面
        model.addAttribute("user",seckillUser);
        //通过id查询到商品的信息
        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        //获取秒杀的状态时间和系统现在的时间
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态 0： 秒杀未开始  1：秒杀进行中 2：秒杀结束
        int miaoshaStatus = 0;
        // 秒杀剩余时间
        int remainSecond = 0;

        //未开始
        if (now < startDate){
            miaoshaStatus = 0;
            remainSecond = (int)((startDate - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endDate){
            miaoshaStatus = 2;
            remainSecond = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSecond =  0;
        }
        //将秒杀的状态信息传给前端页面
        model.addAttribute("seckillStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSecond);

        //渲染html
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",webContext);

        //如果html页面不为空，则将html存到redis中
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKeyPrefix.seckillGoodsStockPrefix,""+goodsId,html);
        }
        return html;
    }

    @RequestMapping(value = "to_detail_static/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetailStatic(SeckillUser user,@PathVariable("goodsId") long goodsId){

        //通过商品id查询数据库
        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);

        //获取商品的秒杀开始与结束时间
        long startDate = goods.getStartDate().getTime();
        long endDate = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态 0： 秒杀未开始  1：秒杀进行中 2：秒杀结束
        int miaoshaStatus = 0;
        // 秒杀剩余时间
        int remainSecond = 0;

        //未开始
        if (now < startDate){
            miaoshaStatus = 0;
            remainSecond = (int)((startDate - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endDate){
            miaoshaStatus = 2;
            remainSecond = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSecond =  0;
        }

        // 服务端封装商品数据直接传递给客户端，而不用绚烂页面
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setRemainSeconds(remainSecond);
        goodsDetailVo.setSeckillStatus(miaoshaStatus);

        return Result.success(goodsDetailVo);
    }
}
