package cn.aethli.dnspod.controller;

import cn.aethli.dnspod.model.ResponseModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author aethli */
@RestController
@RequestMapping("security/record")
public class RecordController {

  @PostMapping("addRecord")
  public ResponseModel addRecord(@RequestBody Object object) {
    return null;
  }
}
