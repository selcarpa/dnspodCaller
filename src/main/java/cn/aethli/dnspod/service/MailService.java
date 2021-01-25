package cn.aethli.dnspod.service;

/** @author 93162 */
public interface MailService {
  /**
   * @param to receiver
   * @param subject subject
   * @param text text content
   */
  void sendTextMail(String to, String subject, String text);
}
