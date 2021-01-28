package cn.aethli.dnspod.model.feign.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TencentCommonResult {
  private int code;
  private String message;
  private String codeDesc;
}
