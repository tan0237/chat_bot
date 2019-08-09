package com.espay.util;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码工具类
 */
public class Md5Util {

    /**
     * 密码加密
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        return base64en.encode(md5.digest(str.getBytes("utf-8")));
    }

    /**
     * 密码验证
     * @param inputPass
     * @param realPass
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean checkPass(String inputPass,String realPass) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        if(EncoderByMd5(inputPass).equals(realPass))
            return true;
        else
            return false;
    }
}
