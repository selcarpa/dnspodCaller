package cn.aethli.dnspod.service.impl;

import cn.aethli.dnspod.dto.RecordDto;
import cn.aethli.dnspod.feign.TencentFeign;
import cn.aethli.dnspod.model.NoticeRequestBody;
import cn.aethli.dnspod.model.feignParameter.TencentCommonParameters;
import cn.aethli.dnspod.service.MailService;
import cn.aethli.dnspod.service.RecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/** @author 93162 */
@Service
@Slf4j
public class RecordServiceImpl implements RecordService {

  @Resource private TencentFeign tencentFeign;
  @Resource private MailService mailService;
  @Resource private ObjectMapper defaultMapper;

  @Override
  @Async
  public void addRecord(NoticeRequestBody<RecordDto> noticeRequestBody) {
    noticeRequestBody.getData().setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
//    try {
//      String sign = TencentCommonParameters
//              .signThis(
//                      noticeRequestBody.getData(),
//                      "cns.api.qcloud.com",
//                      "GET");
//    } catch (UnsupportedEncodingException e) {
//      e.printStackTrace();
//    }
    try {
      Object request = tencentFeign.request(noticeRequestBody.getData());
      log.info(request.toString());
    } catch (FeignException e) {
      log.error(e.getMessage(), e);
    }
    try {
      mailService.sendTextMail(
          noticeRequestBody.getTo(),
          noticeRequestBody.getSubject(),
          defaultMapper.writeValueAsString(noticeRequestBody));
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }
}
