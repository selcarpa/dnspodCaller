package cn.aethli.dnspod.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/** @author aethli */
public class RSAUtils {

  private static final String ALGORITHM = "RSA";

  private static final int KEY_SIZE = 2048;

  private static KeyFactory keyFactory = null;

  private static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
    if (keyFactory == null) {
      synchronized (ALGORITHM) {
        keyFactory = KeyFactory.getInstance(ALGORITHM);
      }
    }
    return keyFactory;
  }

  /**
   * @return a random keyPair by algorithm and key size
   * @throws NoSuchAlgorithmException if algorithm not exist
   */
  public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM);
    gen.initialize(KEY_SIZE);
    return gen.generateKeyPair();
  }

  /**
   * @param pubKeyBase64
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getPublicKey(String pubKeyBase64)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] encPubKey = Base64.decodeBase64(pubKeyBase64);

    X509EncodedKeySpec encPubKeySpec = new X509EncodedKeySpec(encPubKey);

    return getKeyFactory().generatePublic(encPubKeySpec);
  }

  /**
   * @param priKeyBase64
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(String priKeyBase64)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] encPriKey = Base64.decodeBase64(priKeyBase64);
    PKCS8EncodedKeySpec encPriKeySpec = new PKCS8EncodedKeySpec(encPriKey);
    return getKeyFactory().generatePrivate(encPriKeySpec);
  }

  /**
   * @param plainData
   * @param pubKey
   * @return
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static byte[] encrypt(byte[] plainData, PublicKey pubKey)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    return cipher.doFinal(plainData);
  }

  /**
   * @param cipherData
   * @param priKey
   * @return
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static byte[] decrypt(byte[] cipherData, PrivateKey priKey)
      throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, priKey);
    return cipher.doFinal(cipherData);
  }

  /*  public static String rsaEncrypt(String input, PublicKey publicKey) {
    String result = "";
    try {
      // 加密
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      byte[] inputArray = input.getBytes();
      int inputLength = inputArray.length;
      int MAX_ENCRYPT_BLOCK = 117;
      int offSet = 0;
      byte[] resultBytes = {};
      byte[] cache;
      while (inputLength - offSet > 0) {
        if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
          cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
          offSet += MAX_ENCRYPT_BLOCK;
        } else {
          cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
          offSet = inputLength;
        }
        resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
        System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
      }
      result = Base64.encodeToString(resultBytes);
    } catch (Exception e) {
      System.out.println("rsaEncrypt error:" + e.getMessage());
    }
    System.out.println("加密的结果：" + result);
    return result;
  }*/
}
