package cn.aethli.dnspod.service.impl;

import cn.aethli.dnspod.dto.RecordDto;
import cn.aethli.dnspod.feign.TencentFeign;
import cn.aethli.dnspod.model.NoticeRequestBody;
import cn.aethli.dnspod.model.feign.result.RecordResult;
import cn.aethli.dnspod.service.MailService;
import cn.aethli.dnspod.service.RecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/** @author 93162 */
@Service
@Slf4j
public class RecordServiceImpl implements RecordService {

  @Resource private TencentFeign tencentFeign;
  @Resource private MailService mailService;
  @Resource private ObjectMapper defaultMapper;

  @Override
  @Async
  public void requestAsync(NoticeRequestBody<RecordDto> noticeRequestBody) {
    noticeRequestBody.getData().setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
    try {
      RecordResult request = tencentFeign.request(noticeRequestBody.getData());
      log.debug(defaultMapper.writeValueAsString(request));
      mailService.sendTextMail(
          noticeRequestBody.getTo(),
          noticeRequestBody.getSubject(),
          defaultMapper.writeValueAsString(request));

    } catch (FeignException | JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public Object request(RecordDto recordDto) {
    recordDto.setTimestamp(Math.toIntExact(System.currentTimeMillis() / 1000));
    return tencentFeign.request(recordDto);
  }
}
