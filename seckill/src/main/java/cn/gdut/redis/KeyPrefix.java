package cn.gdut.redis;

/**
 * redis的前缀，
 * 之所以在redis中设置前缀，是因为防止键重复。同时根据不用的键设置不同的键
 *
 *
 */
public interface KeyPrefix {

    /**
     * key的过期时间
     * @return
     */
    int expireSeconds();

    /**
     * key的前缀
     * @return
     */
    String getPrefix();
}
