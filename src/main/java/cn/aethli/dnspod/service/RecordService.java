package cn.aethli.dnspod.service;

import cn.aethli.dnspod.dto.RecordDto;
import cn.aethli.dnspod.model.NoticeRequestBody;

/** @author 93162 */
public interface RecordService {
  void requestAsync(NoticeRequestBody<RecordDto> noticeRequestBody);

  Object request(RecordDto recordDto);
}
