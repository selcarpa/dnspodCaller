package cn.aethli.dnspod.model.feign.result;

import lombok.Getter;
import lombok.Setter;

/** @author 93162 */
@Getter
@Setter
public class RecordResult extends TencentCommonResult {
  private Data data;

  @Getter
  @Setter
  public static class Data {
    private Record record;
  }

  @Getter
  @Setter
  public static class Record {
    private String id;
    private String name;
    private String status;
    private String weight;
  }
}
