package cn.aethli.dnspod.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TencentFeignRequestInterceptor implements RequestInterceptor {
  @Resource private ObjectMapper defaultMapper;

  @Override
  public void apply(RequestTemplate template) {
    if ("GET".equals(template.method()) && template.body() != null) {
      try {
        JsonNode jsonNode = defaultMapper.readTree(template.body());
        template.body("");

        Map<String, Collection<String>> queries = buildQuery(jsonNode);
//        Collection<String> secretKeyCollection = queries.get("secretKey");
//        queries.remove("secretKey");
//        String secretKey = secretKeyCollection.iterator().next();
//        StringBuilder signContentBuilder = new StringBuilder();
//        signContentBuilder
//            .append(template.method())
//            .append(template.feignTarget().url())
//            .append("?")
//            .append(
//                queries.keySet().stream()
//                    .map(k -> String.format("%s=%s", k, queries.get(k).toString()))
//                    .collect(Collectors.joining("&")));
//        if (secretKey != null) {
//          HmacUtils hmacUtils =
//              new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey.getBytes(StandardCharsets.UTF_8));
//          byte[] signBytes = hmacUtils.hmac(signContentBuilder.toString());
//          String sign = Base64.encodeBase64String(signBytes);
//          queries.put("Signature", Collections.singleton(URLEncoder.encode(sign, "UTF-8")));
//        }
        template.queries(queries);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private Map<String, Collection<String>>  buildQuery(JsonNode jsonNode)
      throws UnsupportedEncodingException {
    Map<String, Collection<String>> queries = new TreeMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      if (entry.getValue() instanceof NullNode) {
        continue;
      }
      queries.put(
          entry.getKey(),
          Collections.singleton(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8")));
    }
    return queries;
  }
}
