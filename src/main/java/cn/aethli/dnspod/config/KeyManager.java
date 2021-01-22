package cn.aethli.dnspod.config;

import cn.aethli.dnspod.model.Decryptor;
import cn.aethli.dnspod.utils.FileUtils;
import cn.aethli.dnspod.utils.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/** @author 93162 */
@Component
@Slf4j
@Scope("singleton")
public class KeyManager {
  private static final Map<String, Decryptor> PRIVATE_KEY_MAP = new HashMap<>();

  @Value("${caller.privateKeyPath}")
  private String privateKeyPath;

  @Value("${caller.privateKeyFileNamePrefix}")
  private String privateKeyFileNamePrefix;

  @Value("${caller.fileNameSuffix}")
  private String fileNameSuffix;

  public static Decryptor getDecryptor(String key) {
    return PRIVATE_KEY_MAP.get(key);
  }

  @PostConstruct
  private void postConstruct() {
    File keyDir = new File(privateKeyPath);
    if (!keyDir.exists() || !keyDir.isDirectory()) {
      return;
    }
    String[] keyFileNames = keyDir.list();
    if (keyFileNames == null || keyFileNames.length == 0) {
      return;
    }
    for (String keyFileName : keyFileNames) {
      File key = new File(privateKeyPath, keyFileName);
      try {
        String keyBase64String = FileUtils.readFile(key);

        final PrivateKey privateKey = RSAUtils.getPrivateKey(keyBase64String);
        Cipher cipher = Cipher.getInstance(RSAUtils.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        final Decryptor decryptor = new Decryptor(privateKey, cipher);
        final String keyKey =
            keyFileName.replace(fileNameSuffix, "").replace(privateKeyFileNamePrefix, "");
        PRIVATE_KEY_MAP.put(keyKey, decryptor);
        log.info("load private key {}", keyKey);
      } catch (NoSuchAlgorithmException
          | IOException
          | InvalidKeySpecException
          | NoSuchPaddingException
          | InvalidKeyException e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
