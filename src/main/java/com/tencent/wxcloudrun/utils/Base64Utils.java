package com.tencent.wxcloudrun.utils;

import java.util.Base64;

public class Base64Utils {
  /**
   * String为Bse64
   * @param str
   * @return
   */
  public static String strConvertBase(String str) {

    Base64.Encoder encoder = Base64.getEncoder();
    if(null != str){
      return encoder.encodeToString(str.getBytes());
    }
    return null;
  }

  /**
   * base 64 encode
   *
   * @param bytes
   *            待编码的byte[]
   * @return 编码后的base 64 code
   */
  public static String base64Encode(byte[] bytes) {

    Base64.Encoder encoder = Base64.getEncoder();
    return encoder.encodeToString(bytes);
  }

}
