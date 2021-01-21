package cn.aethli.dnspod.model;

import cn.aethli.dnspod.config.KeyManager;
import cn.aethli.dnspod.utils.RSAUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

@Slf4j
public class ModifyAbleHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private String body;

  public ModifyAbleHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
    StringBuilder stringBuilder = new StringBuilder();
    try (InputStream inputStream = request.getInputStream()) {
      if (inputStream != null) {
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(inputStream))) {
          char[] charBuffer = new char[128];
          int bytesRead;
          while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
            stringBuilder.append(charBuffer, 0, bytesRead);
          }
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    try {
      EncryptedBody encryptedBody =
          objectMapper.readValue(stringBuilder.toString(), EncryptedBody.class);
      if (StringUtils.isNoneEmpty(encryptedBody.getContent())) {
        String encryptedBodyContent = encryptedBody.getContent();
        PrivateKey privateKey = KeyManager.getPublicKey(encryptedBody.getKey());
        if (privateKey != null) {
          byte[] decrypt = RSAUtils.decrypt(Base64.decodeBase64(encryptedBodyContent), privateKey);
          String body = new String(decrypt);
          this.setBody(body);
          return;
        }
      }
    } catch (JsonProcessingException
        | NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidKeyException
        | BadPaddingException
        | IllegalBlockSizeException
         e) {
      log.error(e.getMessage(), e);
    }

    this.setBody(stringBuilder.toString());
  }

  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {}

      @Override
      public int read() {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }

  public String getBody() {
    return this.body;
  }

  public void setBody(String body) {
    this.body = body;
  }
}
