package cn.aethli.dnspod.model.feignParameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

@Getter
@Setter
public abstract class TencentCommonParameters {
  @JsonProperty("Nonce")
  private final Integer nonce = RandomUtils.nextInt();
  @JsonProperty("SignatureMethod")
  private final String signatureMethod = "HmacSHA1";
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
}
