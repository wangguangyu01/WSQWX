package com.tencent.wxcloudrun.utils;


import com.tencent.wxcloudrun.exception.BDException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/> 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 *
 * @author zhangshunhua
 * @version 1.0
 * @date 2019-4-29
 */
@Slf4j
public class RSAUtils {

  /**
   * 加密算法RSA
   */
  public static final String KEY_ALGORITHM = "RSA";

  /**
   * 签名算法
   */
  public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

  /**
   * 获取公钥的key
   */
  private static final String PUBLIC_KEY = "RSAPublicKey";

  /**
   * 获取私钥的key
   */
  private static final String PRIVATE_KEY = "RSAPrivateKey";

  /**
   * RSA最大加密明文大小
   */
  private static final int MAX_ENCRYPT_BLOCK = 117;

  /**
   * RSA最大解密密文大小
   */
  private static final int MAX_DECRYPT_BLOCK = 128;

  private static final String PUBLIC_KEY_CODE = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDjzOqwqifoMVYQhErTnXo61SIY+3639l2G5WmcOtSRIJl7cvs9YEfypqyIcDzCUgDOCnxK4HmL9RhyeYKYnKvhkfsOmanY36S6O4fI1b2rkwT/e9qBYjOdOPNBYbF+lPTfh+Kh9PxS0Dh3dnmMEcNACytqtAobLEShbjud+6tSGQIDAQAB";

  /**
   * <p>
   * 生成密钥对(公钥和私钥)
   * </p>
   */
  public static Map<String, Object> genKeyPair() throws Exception {
    KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    keyPairGen.initialize(1024);
    KeyPair keyPair = keyPairGen.generateKeyPair();
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    Map<String, Object> keyMap = new HashMap<String, Object>(2);
    keyMap.put(PUBLIC_KEY, publicKey);
    keyMap.put(PRIVATE_KEY, privateKey);
    return keyMap;
  }

  /**
   * <p>
   * 用私钥对信息生成数字签名
   * </p>
   *
   * @param data 已加密数据
   * @param privateKey 私钥(BASE64编码)
   */
  public static String sign(byte[] data, String privateKey) throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
    Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
    signature.initSign(privateK);
    signature.update(data);
    return Base64Utils.encodeToString(signature.sign());
  }

  /**
   * <p>
   * 校验数字签名
   * </p>
   *
   * @param data 已加密数据
   * @param publicKey 公钥(BASE64编码)
   * @param sign 数字签名
   */
  public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    PublicKey publicK = keyFactory.generatePublic(keySpec);
    Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
    signature.initVerify(publicK);
    signature.update(data);
    return signature.verify(Base64Utils.decodeFromString(sign));
  }

  /**
   * <P>
   * 私钥解密
   * </p>
   *
   * @param encryptedData 已加密数据
   * @param privateKey 私钥(BASE64编码)
   */
  public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
      throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.DECRYPT_MODE, privateK);
    int inputLen = encryptedData.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] cache;
    int i = 0;
    // 对数据分段解密
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
        cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
      } else {
        cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * MAX_DECRYPT_BLOCK;
    }
    byte[] decryptedData = out.toByteArray();
    out.close();
    return decryptedData;
  }

  /**
   * <p>
   * 公钥解密
   * </p>
   *
   * @param encryptedData 已加密数据
   * @param publicKey 公钥(BASE64编码)
   */
  public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key publicK = keyFactory.generatePublic(x509KeySpec);
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.DECRYPT_MODE, publicK);
    int inputLen = encryptedData.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] cache;
    int i = 0;
    // 对数据分段解密
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
        cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
      } else {
        cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * MAX_DECRYPT_BLOCK;
    }
    byte[] decryptedData = out.toByteArray();
    out.close();
    return decryptedData;
  }

  /**
   * <p>
   * 公钥加密
   * </p>
   *
   * @param data 源数据
   * @param publicKey 公钥(BASE64编码)
   */
  public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key publicK = keyFactory.generatePublic(x509KeySpec);
    // 对数据加密
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.ENCRYPT_MODE, publicK);
    int inputLen = data.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] cache;
    int i = 0;
    // 对数据分段加密
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
        cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
      } else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * MAX_ENCRYPT_BLOCK;
    }
    byte[] encryptedData = out.toByteArray();
    out.close();
    return encryptedData;
  }

  /**
   * <p>
   * 私钥加密
   * </p>
   *
   * @param data 源数据
   * @param privateKey 私钥(BASE64编码)
   */
  public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
    byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
    PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
    Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
    Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    cipher.init(Cipher.ENCRYPT_MODE, privateK);
    int inputLen = data.length;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int offSet = 0;
    byte[] cache;
    int i = 0;
    // 对数据分段加密
    while (inputLen - offSet > 0) {
      if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
        cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
      } else {
        cache = cipher.doFinal(data, offSet, inputLen - offSet);
      }
      out.write(cache, 0, cache.length);
      i++;
      offSet = i * MAX_ENCRYPT_BLOCK;
    }
    byte[] encryptedData = out.toByteArray();
    out.close();
    return encryptedData;
  }

  /**
   * @param keyMap 密钥对
   */
  public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
    Key key = (Key) keyMap.get(PRIVATE_KEY);
    return Base64Utils.encodeToString(key.getEncoded());
  }

  /**
   * <p>
   * 获取公钥
   * </p>
   *
   * @param keyMap 密钥对
   */
  public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
    Key key = (Key) keyMap.get(PUBLIC_KEY);
    return Base64Utils.encodeToString(key.getEncoded());
  }

  /**
   * java端公钥加密
   */
  public static String encryptedDataOnJava(String data, String PUBLICKEY) {
    try {
      data = Base64Utils.encodeToString(encryptByPublicKey(data.getBytes("UTF-8"), PUBLICKEY));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  /**
   * java端私钥解密
   */
  public static String decryptDataOnJava(String data, String privateKey) {
    String temp = "";
    try {
      byte[] rs = Base64Utils.decodeFromString(data);
      temp = new String(RSAUtils.decryptByPrivateKey(rs, privateKey), "UTF-8");
    } catch (Exception e) {
      log.error("密码解密异常", e);
      throw new BDException("解密失败", 10009);
    }
    return temp;
  }

  public static void main(String[] args) throws Exception {
//    Map<String, Object> result = RSAUtils.genKeyPair();
//    System.out.println(RSAUtils.getPrivateKey(result));
//    System.out.println(RSAUtils.getPublicKey(result));

    String sign="JaEHCInkz4tEm2R2AcRAT9j2V7A%2FufCLzQc%2BArx%2BGBmmT%2Fz1Gsj1scjfnVFsIvHdMTinHJeS2%2Bq9LUiDNjan0YRi6stwzFSyBp%2BCGvKeT%2FClZUztZJ66Nbj%2BtEs9Z67hGXPmjaHF3PdvHnXMrfhzxsyKQvYuduvs%2FsJ%2B7MFNm9Y%3D";
    //sign = URLDecoder.decode(sign,"UTF-8");
    String privateKeyCode = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJiz1Ec4jZb0exAnOdNH2VoeGkuCZCdVMqlWQpK8VI1XYlk0Gb3DE9tZgmd/oW0EFIrwLU8TjyaIEVzNER1a8MZzUXojFOQvGV/td/gbFCxOR2FOhc5MjsSlzjBgSwPjh4ChO2N8EfgxoGI0m/ILh354b9qXVaANPCXR6+YaxDMzAgMBAAECgYEAg6CfnzTUeAcN/YKSidkpNlE0gVpIUs9R+D3u7OWCTMPCdvo+JiD+ANpLWkPeNkaKHqmhpMRLZP9tV/08f31ghZn7aC0/4DkCUn5AGcZ4SW75dgMg4v3E+/9ABU6W2/HeBOvUuSAXxFO7Ok7IgeYiGc+Anogmr3YAWR8zE30/qOECQQDWAOhWs7yISLbjUXvZ4Xx8rb/8uy0+zHikBM43m5a/oVjnDg3JNMyMbGrSHhbKSKmT89s3s+pC5Bj/EEyj7M0fAkEAtqtIXXalbVe5Cu+nGA9JR9y2MtGB78vJg3aY7N3/9hWm6Yi5WrFbX+OxcZipk47FAaguPr6o6uG+HuEZBMaDbQJAC4y2/V7r8OsDu9b8+TBbOJtj/i7X7Ui5xhhgTM3/383Eb4vpoI9R7s43IanwDvDG/i4uCZ6TKRMIalOl3z77fwJAc754LfCdxXjOISXVJKOa9VPehrjFsmHYH9qn736DTzRM/LDLcbHrigjGSIpI+Nx/7BraoptAgQPk6cALkEEQyQJAaB5MsAAr4ehoQpPHMZnOIw0XtywBrcuGUg5cWwqo3a2vxKrhLek5m2dAwL7zKsARUmeFE5YTjELUYQo31Tcmyg==";

    String param1 = RSAUtils.decryptDataOnJava(sign, privateKeyCode);
    System.out.println("==="+param1);

  }

}
