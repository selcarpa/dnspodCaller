package cn.aethli.dnspod.model.feignParameter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomUtils;

@Getter
@Setter
public abstract class TencentCommonParameters {
  private final Integer nonce = RandomUtils.nextInt();
  private String action;
  private String region;
  private Integer timestamp;
  private String secretId;
  private String signature;
  private String signatureMethod;
  private String token;
}
