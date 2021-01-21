package cn.aethli.dnspod.config;

import cn.aethli.dnspod.model.ModifyAbleHttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@WebFilter(urlPatterns = "/security/*",filterName = "securityFilter")
public class SecurityFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    ModifyAbleHttpServletRequestWrapper requestWrapper = null;
    try {
      requestWrapper = new ModifyAbleHttpServletRequestWrapper((HttpServletRequest) request);
    } catch (Exception e) {
      log.warn("requestWrapper Error:", e);
    }

    chain.doFilter((Objects.isNull(requestWrapper) ? request : requestWrapper), response);
  }
}
