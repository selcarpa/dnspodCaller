package cn.aethli.dnspod.feign;

import cn.aethli.dnspod.config.TencentFeignRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "tencent",
    url = "cns.api.qcloud.com",
    configuration = TencentFeignRequestInterceptor.class)
public interface TencentFeign {
  @GetMapping
  Object request(@RequestBody Object params);
}
