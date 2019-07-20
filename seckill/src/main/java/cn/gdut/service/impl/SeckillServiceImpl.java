package cn.gdut.service.impl;

import cn.gdut.domain.OrderInfo;
import cn.gdut.domain.SeckillUser;
import cn.gdut.redis.RedisService;
import cn.gdut.redis.SeckillKeyPrefix;
import cn.gdut.service.GoodsService;
import cn.gdut.service.OrderService;
import cn.gdut.service.SeckillService;
import cn.gdut.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;
    private char[] ops = new char[]{'+','-','*'};

    /**
     * 秒杀操作
     * 1 减库存
     * 2 将生成的订单写入到miaosha_order表中
     * @param user 秒杀的用户
     * @param goodsVo 秒杀的商品
     * @return 生成的订单详情
     */
    @Override
    public OrderInfo seckill(SeckillUser user, GoodsVo goodsVo) {
        // 减库存
        boolean success = goodsService.reduceStock(goodsVo);
        //不成功
        if (!success){

        }
        //2 生成订单
        return orderService.createOrder(user,goodsVo);
    }

    @Override
    public BufferedImage createVerifyCode(SeckillUser user, long goodsId) {

        if (user == null || goodsId <= 0){
            return null;
        }

        // 验证码的宽高
        int width = 80;
        int height = 32;

        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();

        // 计算表达式值，并把把验证码值存到redis中
        int expResult = calc(verifyCode);
        redisService.set(SeckillKeyPrefix.seckillKeyPrefix, user.getId() + "," + goodsId, expResult);
        //输出图片
        return image;

    }

    private String generateVerifyCode(Random random){
        int num1 = random.nextInt(10);
        int num2 = random.nextInt(10);
        int num3 = random.nextInt(10);
        char op1 = ops[random.nextInt(3)];
        char op2 = ops[random.nextInt(3)];
        String exp = ""+num1 + op1 + num2 + op2 +num3;
        return exp;
    }

    private int calc(String exp){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        try {
            return (Integer) engine.eval(exp);
        } catch (ScriptException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
