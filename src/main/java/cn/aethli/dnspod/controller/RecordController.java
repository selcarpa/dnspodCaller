package cn.aethli.dnspod.controller;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import cn.aethli.dnspod.dto.RecordDto;
import cn.aethli.dnspod.feign.TencentFeign;
import cn.aethli.dnspod.model.ResponseModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/** @author aethli */
@RestController
@RequestMapping("security/record")
public class RecordController {

  @Resource private TencentFeign tencentFeign;

  @PostMapping("addRecord")
  public ResponseModel addRecord(@RequestBody RecordDto recordDto) {
    recordDto.setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
    tencentFeign.request(recordDto);
    return new ResponseModel(ResponseStatus.SUCCESS);
  }
}
