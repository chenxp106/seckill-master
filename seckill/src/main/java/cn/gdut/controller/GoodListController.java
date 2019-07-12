package cn.gdut.controller;


import cn.gdut.domain.SeckillUser;
import cn.gdut.service.GoodsService;
import cn.gdut.vo.GoodsVo;
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

        //强查询到的商品渲染到页面中
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsVoList);

        //渲染html
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        String html = null;

        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",webContext);

        return html;
    }

    /**
     *
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
        String html = null;
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",webContext);
        return html;
    }
}
