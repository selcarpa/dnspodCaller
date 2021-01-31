package cn.aethli.dnspod.model.feign.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** @author 93162 */
@Getter
@Setter
public class RecordResult extends TencentCommonResult {
  private Data data;

  @Getter
  @Setter
  public static class Data {
    private Record record;
    private Domain domain;
    private Info info;
    private List<Record> records;
  }

  @Getter
  @Setter
  public static class Info {
    @JsonProperty("sub_domains")
    private String subDomains;

    @JsonProperty("record_total")
    private String recordTotal;

    @JsonProperty("records_num")
    private String recordsNum;
  }

  @Getter
  @Setter
  public static class Domain {
    private String id;
    private String name;
    private String punycode;
    private String grade;
    private String owner;
    @JsonProperty("ext_status")
    private String extStatus;
    private int ttl;
    @JsonProperty("min_ttl")
    private int minTtl;
    @JsonProperty("dnspod_ns")
    private List<String> dnspodNs;
    private String status;
    private int q_project_id;
  }

  @Getter
  @Setter
  public static class Record {
    private String id;
    private String name;
    private String status;
    private String weight;
    private int ttl;
    private String value;
    private int enabled;
    @JsonProperty("updated_on")
    private String updatedOn;
    @JsonProperty("q_project_id")
    private int qProjectId;
    private String line;
    private String line_id;
    private String type;
    private String remark;
    private int mx;
    private String hold;
  }
}
