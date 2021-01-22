package cn.aethli.dnspod.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "tencent", url = "http://cns.api.qcloud.com")
public interface TencentFeign {
  @GetMapping
  Object request(@RequestParam Object params);
}
