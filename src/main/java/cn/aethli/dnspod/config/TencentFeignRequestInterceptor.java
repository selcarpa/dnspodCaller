package cn.aethli.dnspod.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TencentFeignRequestInterceptor extends SpringDecoder implements RequestInterceptor {
  @Resource private ObjectMapper defaultMapper;

  public TencentFeignRequestInterceptor(ObjectFactory<HttpMessageConverters> messageConverters) {
    super(messageConverters);
  }

  @Override
  public void apply(RequestTemplate template) {
    if ("GET".equals(template.method()) && template.body() != null) {
      try {
        JsonNode jsonNode = defaultMapper.readTree(template.body());
        template.body(null, StandardCharsets.UTF_8);

        Map<String, Collection<String>> queries = buildQuery(jsonNode);
        Collection<String> secretKeyCollection = queries.get("SecretKey");
        queries.remove("SecretKey");
        String secretKey = secretKeyCollection.iterator().next();
        StringBuilder signContentBuilder = new StringBuilder();
        signContentBuilder
            .append(template.method())
            .append(template.feignTarget().url().replace("https://", ""))
            .append(template.path())
            .append("?")
            .append(
                queries.keySet().stream()
                    .map(
                        k ->
                            String.format(
                                "%s=%s",
                                k.replace("_", "."),
                                ((Set) (queries.get(k))).iterator().next().toString()))
                    .collect(Collectors.joining("&")));

        String signatureMethod = queries.get("SignatureMethod").iterator().next();
        if (secretKey != null) {
          HmacUtils hmacUtils = new HmacUtils(signatureMethod, secretKey);
          byte[] signBytes = hmacUtils.hmac(signContentBuilder.toString());
          String sign = Base64.encodeBase64String(signBytes);
          queries.put("Signature", Collections.singleton(URLEncoder.encode(sign, "UTF-8")));
        }
        template.queries(queries);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private Map<String, Collection<String>> buildQuery(JsonNode jsonNode)
      throws UnsupportedEncodingException {
    Map<String, Collection<String>> queries = new TreeMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      if (entry.getValue() instanceof NullNode) {
        continue;
      }
      queries.put(entry.getKey(), Collections.singleton(String.valueOf(entry.getValue().asText())));
    }
    return queries;
  }

  @Override
  public Object decode(Response response, Type type) throws FeignException, IOException {
    Map<String, Collection<String>> headers = new HashMap<>(response.headers());
    headers.forEach(
        (k, v) -> {
          if (k.equals("content-type")) {
            v = new LinkedList<>();
            v.add("application/json;charset=utf-8");
            headers.put("content-type", v);
          }
        });
    response = response.toBuilder().headers(headers).build();
    return super.decode(response, type);
  }
}
