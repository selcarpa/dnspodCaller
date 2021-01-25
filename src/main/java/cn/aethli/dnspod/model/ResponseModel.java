package cn.aethli.dnspod.model;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime date = LocalDateTime.now();

  private ResponseStatus status;
  private String msg;
  private Object data;

  public ResponseModel(ResponseStatus status) {
    this.status = status;
  }

  public ResponseModel(ResponseStatus status, String msg) {
    this.status = status;
    this.msg = msg;
  }

  public ResponseModel(ResponseStatus status, Object data) {
    this.status = status;
    this.data = data;
  }
}
