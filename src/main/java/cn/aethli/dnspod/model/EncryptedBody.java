package cn.aethli.dnspod.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author 93162
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EncryptedBody {
  private String key;
  private String content;
}
