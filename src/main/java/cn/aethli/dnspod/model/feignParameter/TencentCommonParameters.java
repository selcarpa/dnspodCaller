package cn.aethli.dnspod.model.feignParameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class TencentCommonParameters {
  @JsonProperty("Nonce")
  private final Integer nonce = RandomUtils.nextInt();

  @JsonProperty("SignatureMethod")
  private String signatureMethod;

  @JsonProperty("Action")
  private String action;

  @JsonProperty("Region")
  private String region;

  @JsonProperty("Timestamp")
  private Integer timestamp;

  @JsonProperty("SecretId")
  private String secretId;

  @JsonProperty("Signature")
  private String signature;

  @JsonProperty("Token")
  private String token;

  public String signThis(
      String secretKey, TencentCommonParameters parameter, String url, String method)
      throws UnsupportedEncodingException {
    Map<String, String> fields =
        new TreeMap<String, String>() {
          @Override
          public String put(String key, String value) {
            if (StringUtils.isNotEmpty(value)) {
              return super.put(key, value);
            } else {
              return null;
            }
          }
        };
    fields.put("Nonce", String.valueOf(parameter.nonce));
    fields.put("SignatureMethod", parameter.signatureMethod);
    fields.put("Action", parameter.action);
    fields.put("Region", parameter.region);
    fields.put("Timestamp", String.valueOf(parameter.timestamp));
    fields.put("SecretId", parameter.secretId);
    fields.put("Token", parameter.token);
    StringBuilder signContentBuilder = new StringBuilder();
    signContentBuilder
        .append(method)
        .append(url)
        .append("?")
        .append(
            fields.keySet().stream()
                .map(k -> String.format("%s=%s", k, fields.get(k)))
                .collect(Collectors.joining("&")));
    if (secretKey != null) {
      HmacUtils hmacUtils =
          new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes(StandardCharsets.UTF_8));
      byte[] signBytes = hmacUtils.hmac(signContentBuilder.toString());
      String sign = Base64.encodeBase64String(signBytes);
      return URLEncoder.encode(sign, "UTF-8");
    }
    return null;
  }
}
