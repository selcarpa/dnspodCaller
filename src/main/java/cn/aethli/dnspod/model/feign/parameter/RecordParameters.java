package cn.aethli.dnspod.model.feign.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordParameters extends TencentCommonParameters {
  @JsonProperty("domain")
  private String domain;

  @JsonProperty("subDomain")
  private String subDomain;

  @JsonProperty("recordType")
  private String recordType;

  @JsonProperty("recordLine")
  private String recordLine;

  @JsonProperty("value")
  private String value;

  @JsonProperty("ttl")
  private Integer ttl;

  @JsonProperty("mx")
  private Integer mx;
}
