package cn.aethli.dnspod.service.impl;

import cn.aethli.dnspod.service.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/** @author 93162 */
@Service
public class MailServiceImpl implements MailService {
  @Resource private JavaMailSender javaMailSender;
  @Value("${caller.mailModule}")
  private String mailModule;
  @Value("${spring.mail.username}")
  private String from;

  @Override
  public void sendTextMail(String to, String subject, String text) {
    if (mailModule.equals("true")) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      message.setFrom(from);
      javaMailSender.send(message);
    }
  }
}
