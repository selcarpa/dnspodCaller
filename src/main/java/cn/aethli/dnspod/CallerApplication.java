package cn.aethli.dnspod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@ServletComponentScan
public class CallerApplication {

  public static void main(String[] args) {
    SpringApplication.run(CallerApplication.class, args);
  }
}
