package cn.aethli.dnspod.common.enums;

/** @author aethli */
public enum ResponseStatus {
  SUCCESS("success", 0),
  FAIL("fail", 1),
  ERROR("error", 2);
  private final String desc;
  private final int value;

  ResponseStatus(String desc, int value) {
    this.desc = desc;
    this.value = value;
  }

  public static ResponseStatus get(int value) {
    for (ResponseStatus status : ResponseStatus.values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("argument error: " + value);
  }

  public int getValue() {
    return this.value;
  }

  public String getDesc() {
    return this.desc;
  }
}
