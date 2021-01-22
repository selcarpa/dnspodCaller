package cn.aethli.dnspod.controller;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import cn.aethli.dnspod.model.ResponseModel;
import cn.aethli.dnspod.utils.RSAUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** @author 93162 */
@ControllerAdvice
@RequestMapping("rsa")
public class RsaController {

  @Value("${caller.publicKeyFileNamePrefix}")
  private String publicKeyFileNamePrefix;

  @Value("${caller.privateKeyFileNamePrefix}")
  private String privateKeyFileNamePrefix;

  @Value("${caller.fileNameSuffix}")
  private String fileNameSuffix;

  @Resource private ObjectMapper defaultMapper;

  @GetMapping("generateKeyPair")
  public void generateKeyPair(HttpServletResponse response)
      throws NoSuchAlgorithmException, IOException {
    KeyPair keyPair = RSAUtils.generateKeyPair();
    String publicKeyBase64 = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
    String privateKeyBase64 = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
    String name = UUID.randomUUID().toString();
    ServletOutputStream outputStream = response.getOutputStream();
    response.setContentType("application/force-download");
    response.addHeader(
        "Content-Disposition",
        "attachment; fileName="
            + URLEncoder.encode(
                "key_pair_" + name + ".zip", String.valueOf(StandardCharsets.UTF_8)));

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
      zipOutputStream.putNextEntry(new ZipEntry(publicKeyFileNamePrefix + name + fileNameSuffix));
      zipOutputStream.write(publicKeyBase64.getBytes());
      zipOutputStream.closeEntry();
      zipOutputStream.putNextEntry(new ZipEntry(privateKeyFileNamePrefix + name + fileNameSuffix));
      zipOutputStream.write(privateKeyBase64.getBytes());
      zipOutputStream.closeEntry();
    }
  }

  @ResponseBody
  @PostMapping("encrypt")
  public ResponseModel encrypt(@RequestBody Object params)
      throws NoSuchAlgorithmException, IOException, InvalidKeySpecException,
          IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
          NoSuchPaddingException {
    String publicKeyString =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3IFj7tEC677eKWvsHq5TFOnQr2sJpPmwwZgt6VANlllidgffcPzsQNrMceMHvy3qXJJPOEtPzIxzRdsHWr/Yxs1mub7sW3+ZB3xzLyNZs51W51lSqp/zE09N42K5xka1wt931O8Llt5pxwoMP0SuqfXKT14tVdMe3NKKTY5cFXRJCtD+sYH5Ev7+ucxgGNlw5FqQkxfLEx+z71bxSe+bWlotersCvMUVquKcWYYbiJXDxBRZu1mPf0jzBjwtmbWrE3xjBUY3FxwFL3cdugKD9oF3SSsYrUO6brja/xBnxWLmwcEEG9e4afqEMKocaitA86IbrXU90+QE7YfxQkRWcQIDAQAB";
    PublicKey publicKey = RSAUtils.getPublicKey(publicKeyString);
    Cipher cipher = Cipher.getInstance(RSAUtils.ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] encrypt = RSAUtils.segmentCrypt(defaultMapper.writeValueAsBytes(params), cipher, RSAUtils.Mode.ENCRYPT);
    String encryptedString = Base64.encodeBase64String(encrypt);
    return new ResponseModel(ResponseStatus.SUCCESS, encryptedString);
  }
}
