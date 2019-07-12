package cn.gdut.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    public <T> boolean set(String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //将对象转化为json字符串
            String strValue = beanToString(value);

            if (strValue == null || strValue.length() == 0){
                return false;
            }
            jedis.set(key,strValue);
        }
        finally {
            returnToPool(jedis);
        }
        return true;

    }

    /**
     * 将对象转化为json字符串
     * @param value 对象
     * @param <T> 对象的类型
     * @return 对象对应的json字符串
     */
    public static <T> String beanToString(T value){
        if (value == null){
            return null;
        }
        Class<?> clazz = value.getClass();
        //先对基本类型进行处理
        if (clazz == int.class || clazz == Integer.class)
            return "" + value;
        else if (clazz == long.class || clazz == Long.class)
            return ""+value;
        else if (clazz == String.class){
            return (String) value;
        }
        else
            return JSON.toJSONString(value);
    }

    private void returnToPool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }
}
