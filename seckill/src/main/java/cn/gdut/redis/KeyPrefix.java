package cn.gdut.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}
