package cn.aethli.dnspod.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/** @author aethli */
public class RSAUtils {

  public static final String ALGORITHM = "RSA";

  public static final int KEY_SIZE = 2048;

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
   * @param cipher
   * @return
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static byte[] crypt(byte[] plainData, Cipher cipher, int inputOffset, int inputLen)
      throws BadPaddingException, IllegalBlockSizeException {
    return cipher.doFinal(plainData, inputOffset, inputLen);
  }

  /**
   * @param plainData
   * @param cipher
   * @param mode
   * @return
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static byte[] segmentCrypt(byte[] plainData, Cipher cipher, Mode mode)
      throws BadPaddingException, IllegalBlockSizeException, IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    int inputLength = plainData.length;
    int MAX_ENCRYPT_BLOCK;
    if (mode.equals(Mode.ENCRYPT)) {
      MAX_ENCRYPT_BLOCK = KEY_SIZE / 8 - 11;
    } else {
      MAX_ENCRYPT_BLOCK = KEY_SIZE / 8;
    }
    int offset = 0;
    byte[] cache;
    while (inputLength - offset > 0) {
      if (inputLength - offset > MAX_ENCRYPT_BLOCK) {
        cache = crypt(plainData, cipher, offset, MAX_ENCRYPT_BLOCK);
        offset += MAX_ENCRYPT_BLOCK;
      } else {
        cache = crypt(plainData, cipher, offset, inputLength - offset);
        offset = inputLength;
      }
      byteArrayOutputStream.write(cache);
    }
    return byteArrayOutputStream.toByteArray();
  }

  public enum Mode {
    ENCRYPT,
    DECRYPT
  }
}
