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

    /**
     *
     * @param prefix 前缀
     * @param key 键
     * @param value 值
     * @param <T> 返回类型
     * @return T
     */
    public <T> boolean set(KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();

            //将对象转化为json字符串
            String strValue = beanToString(value);

            if (strValue == null || strValue.length() == 0){
                return false;
            }

            //生成真正的存储在redis中的key
            String realKey = prefix.getPrefix()+key;

            //获取过期的时间
            int seconds = prefix.expireSeconds();

            if (seconds <= 0){
                //设置key的过期时间为redis的默认值（由redis的缓存策略控制）
                jedis.set(realKey,strValue);
            }
            else {
                jedis.setex(realKey,seconds,strValue);
            }
            return true;
        }
        finally {
            returnToPool(jedis);
        }
    }

    public <T> T get(KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            //获取真的key
            String realKey = prefix.getPrefix() + key;
            //获取值
            String strValue = jedis.get(realKey);
            //将json字符串转为对象
            T objValue = stringToBean(strValue, clazz);
            return objValue;
        }
        finally {
            //归还redis到连接池
            returnToPool(jedis);
        }
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

    public static <T> T stringToBean(String strValue,Class<T> clazz){
        if ((strValue == null) || (strValue.length() == 0)||( clazz == null)){
            return null;
        }

        // int or interger
        if ((clazz == int.class) || (clazz == Integer.class)){
            return (T)Integer.valueOf(strValue);
        }
        else if ((clazz == long.class) || (clazz == Long.class)){
            return (T)Long.valueOf(strValue);
        }
        else if (clazz == String.class){
            return (T)strValue;
        }
        else
            return JSON.toJavaObject(JSON.parseObject(strValue),clazz);

    }

    private void returnToPool(Jedis jedis){
        if (jedis != null){
            jedis.close();
        }
    }
}
