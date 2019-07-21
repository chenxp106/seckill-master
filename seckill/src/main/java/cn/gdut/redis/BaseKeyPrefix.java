package cn.gdut.redis;

public abstract class BaseKeyPrefix implements KeyPrefix {

    int expireSeconds;//过期时间
    String prdix;//前缀

    public void setPrdix(String prdix) {
        this.prdix = prdix;
    }

    public BaseKeyPrefix(int expireSeconds, String prdix) {
        this.expireSeconds = expireSeconds;
        this.prdix = prdix;
    }

    public BaseKeyPrefix(String prefix){
        this(0,prefix);
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName;
    }
}
