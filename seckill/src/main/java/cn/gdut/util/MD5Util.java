package cn.gdut.util;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类
 */
public class MD5Util {

    /**
     * 获取输入字符串md5
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPassword){
        //加盐规则
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPassword + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     *
     * @param formPassword 表单中获取的密码
     * @param saltDb 数据库中的盐值
     * @return
     */
    public static String formPassToDbPass(String formPassword,String saltDb){
        String str = ""+saltDb.charAt(0) + saltDb.charAt(2) + formPassword + saltDb.charAt(5) + saltDb.charAt(4);
        return md5(str);
    }


}
