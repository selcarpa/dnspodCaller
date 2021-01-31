package cn.aethli.dnspod.controller;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import cn.aethli.dnspod.dto.RecordDto;
import cn.aethli.dnspod.model.NoticeRequestBody;
import cn.aethli.dnspod.model.ResponseModel;
import cn.aethli.dnspod.service.RecordService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/** @author aethli */
@RestController
@RequestMapping("security/record")
public class RecordController {

  @Resource private RecordService recordService;

  @PostMapping("notice/request")
  public ResponseModel noticeRequest(@RequestBody NoticeRequestBody<RecordDto> noticeRequestBody) {
    recordService.requestAsync(noticeRequestBody);
    return new ResponseModel(ResponseStatus.SUCCESS);
  }

  @PostMapping("request")
  public ResponseModel request(@RequestBody RecordDto recordDto) {
    Object result = recordService.request(recordDto);
    return new ResponseModel(ResponseStatus.SUCCESS,result);
  }
}
