package cn.aethli.dnspod.config;

import cn.aethli.dnspod.common.enums.ResponseStatus;
import cn.aethli.dnspod.exception.DecryptException;
import cn.aethli.dnspod.model.ModifyAbleHttpServletRequestWrapper;
import cn.aethli.dnspod.model.ResponseModel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@WebFilter(urlPatterns = "/security/*", filterName = "securityFilter")
public class SecurityFilter implements Filter {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static byte[] BYTES;

  static {
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addSerializer(
        LocalDateTime.class,
        new JsonSerializer<LocalDateTime>() {
          @Override
          public void serialize(
              LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            gen.writeString(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
          }
        });
    OBJECT_MAPPER.registerModule(javaTimeModule);
    try {
      BYTES = OBJECT_MAPPER.writeValueAsBytes(new ResponseModel(ResponseStatus.ERROR, "Forbidden"));
    } catch (JsonProcessingException e) {
      BYTES = new byte[0];
      e.printStackTrace();
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    ModifyAbleHttpServletRequestWrapper requestWrapper;
    try {
      requestWrapper = new ModifyAbleHttpServletRequestWrapper((HttpServletRequest) request);
    } catch (DecryptException | BadPaddingException | IllegalBlockSizeException e) {

      String ip;
      ip = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
      if (StringUtils.isEmpty(ip)) {
        ip = request.getRemoteHost();
      }

      log.error(
          "{}:,ip:{},url:{}", e.getMessage(), ip, ((HttpServletRequest) request).getRequestURL());
      log.debug(e.getMessage(), e);
      if (response instanceof ResponseFacade) {
        ((ResponseFacade) response).setStatus(403);
      }
      response.setContentType("application/json");
      final ServletOutputStream outputStream = response.getOutputStream();
      outputStream.write(BYTES);
      return;
    }
    chain.doFilter(requestWrapper, response);
  }
}
