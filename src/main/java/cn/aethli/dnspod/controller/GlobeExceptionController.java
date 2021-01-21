package cn.aethli.dnspod.controller;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import cn.aethli.dnspod.model.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** @author aethli */
@Slf4j
@RestControllerAdvice
public class GlobeExceptionController {

  @ExceptionHandler(value = Exception.class)
  public Object exceptionCatch(Exception exception) {
    log.error(exception.toString());
    exception.printStackTrace();
    return new ResponseModel(ResponseStatus.ERROR, exception.getMessage());
  }
}
