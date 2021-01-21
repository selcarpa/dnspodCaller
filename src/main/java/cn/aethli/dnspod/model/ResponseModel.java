package cn.aethli.dnspod.model;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ResponseModel {

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
