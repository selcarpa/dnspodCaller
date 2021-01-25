package cn.aethli.dnspod.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 93162
 **/
@Getter
@Setter
public class NoticeRequestBody <T>{
  private String to;
  private String subject;
  private T data;
}
