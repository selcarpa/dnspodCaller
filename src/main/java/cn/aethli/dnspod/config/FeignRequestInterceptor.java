package cn.aethli.dnspod.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
  private final ObjectMapper objectMapper;

  @Autowired
  public FeignRequestInterceptor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void apply(RequestTemplate template) {
    if ("GET".equals(template.method()) && template.body() != null) {
      try {
        JsonNode jsonNode = objectMapper.readTree(template.body());
        template.body("");

        Map<String, Collection<String>> queries = new HashMap<>();

        buildQuery(jsonNode, "", queries);

        template.queries(queries);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void buildQuery(JsonNode jsonNode, String path, Map<String, Collection<String>> queries) {
    if (!jsonNode.isContainerNode()) {
      if (jsonNode.isNull()) {
        return;
      }
      Collection<String> values = queries.get(path);
      if (CollectionUtils.isEmpty(values)) {
        values = new ArrayList<>();
        queries.put(path, values);
      }
      values.add(jsonNode.asText());
      return;
    }
    if (jsonNode.isArray()) {
      Iterator<JsonNode> elements = jsonNode.elements();
      while (elements.hasNext()) {
        buildQuery(elements.next(), path, queries);
      }
    } else {
      Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> entry = fields.next();
        if (StringUtils.hasText(path)) {
          buildQuery(entry.getValue(), path + "." + entry.getKey(), queries);
        } else {
          buildQuery(entry.getValue(), entry.getKey(), queries);
        }
      }
    }
  }
}
