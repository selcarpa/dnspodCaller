package cn.aethli.dnspod.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/** @author 93162 */
@RestControllerAdvice
@RequestMapping("ip")
public class IpController {
  @GetMapping(value = "myIp")
  public String myIp(HttpServletRequest request) {
    String ip;
    ip = request.getHeader("X-Forwarded-For");
    if (StringUtils.isEmpty(ip)) {
      ip = request.getRemoteHost();
    }
    return ip;
  }
}
