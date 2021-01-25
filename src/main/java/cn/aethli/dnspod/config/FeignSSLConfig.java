package cn.aethli.dnspod.config;

import feign.Client;
import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.net.ssl.SSLContext;

@Configuration
@Slf4j
public class FeignSSLConfig {

  @Bean
  @Scope("prototype")
  public Feign.Builder feignBuilder() {
    Client client;
    try {
      SSLContext context =
          new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
      client = new Client.Default(context.getSocketFactory(), new NoopHostnameVerifier());
    } catch (Exception e) {
      log.error("Create feign client with SSL config failed", e);
      client = new Client.Default(null, null);
    }
    return Feign.builder().client(client);
  }
}
