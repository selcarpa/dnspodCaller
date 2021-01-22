package cn.aethli.dnspod.model.feignParameter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordParameters extends TencentCommonParameters {
  private String domain;
  private String subDomain;
  private String recordType;
  private String recordLine;
  private String value;
  private Integer ttl;
  private Integer mx;
}
