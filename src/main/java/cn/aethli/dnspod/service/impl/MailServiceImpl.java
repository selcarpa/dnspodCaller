package cn.aethli.dnspod.service.impl;

import cn.aethli.dnspod.service.MailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/** @author 93162 */
@Service
public class MailServiceImpl implements MailService {
  @Resource private JavaMailSender javaMailSender;

  @Override
  public void sendTextMail(String to, String subject, String text) {
    if (false) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(text);
      message.setFrom("chaos@aethli.cn");
      javaMailSender.send(message);
    }
  }
}
