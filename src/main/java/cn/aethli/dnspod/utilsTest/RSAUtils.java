package cn.aethli.dnspod.utilsTest;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/** @author aethli */
public class RSAUtils {

  private static final String ALGORITHM = "RSA";

  private static final int KEY_SIZE = 2048;

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
   * @param key
   * @param keyFile
   * @throws IOException
   */
  public static void saveKeyForEncodedBase64(Key key, File keyFile) throws IOException {
    byte[] encBytes = key.getEncoded();
    String encBase64 = Base64.encodeBase64String(encBytes);
    IOUtils.writeFile(encBase64, keyFile);
  }

  /**
   * @param pubKeyBase64
   * @return
   * @throws IOException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PublicKey getPublicKey(String pubKeyBase64)
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] encPubKey = Base64.decodeBase64(pubKeyBase64);

    X509EncodedKeySpec encPubKeySpec = new X509EncodedKeySpec(encPubKey);

    return KeyFactory.getInstance(ALGORITHM).generatePublic(encPubKeySpec);
  }

  /**
   *
   * @param priKeyBase64
   * @return
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  public static PrivateKey getPrivateKey(String priKeyBase64)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] encPriKey = Base64.decodeBase64(priKeyBase64);
    PKCS8EncodedKeySpec encPriKeySpec = new PKCS8EncodedKeySpec(encPriKey);
    return KeyFactory.getInstance(ALGORITHM).generatePrivate(encPriKeySpec);
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
}
