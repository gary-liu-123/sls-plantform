package com.example.esign.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

public class SignUtils {

    /**
     * HMAC-SHA256 签名，输出小写十六进制字符串
     * 与官方示例完全一致: new HmacUtils(HMAC_SHA_256, key).hmac(content)
     */
    public static String sign(String content, String secret) {
        byte[] bytes = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmac(content);
        return new String(Hex.encodeHex(bytes));
    }
}
