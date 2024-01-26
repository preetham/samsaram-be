package me.preetham.samsaram.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
public class ElasticConfiguration extends ElasticsearchConfiguration {

  @Value("${elastic.host}")
  private String host;

  @Value("${elastic.fingerprint}")
  private String fingerprint;

  @Value("${elastic.apikey}")
  private String apiKey;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder().connectedTo(host)
        .usingSsl(fingerprint)
        .withHeaders(() -> {
          HttpHeaders headers = new HttpHeaders();
          headers.add(HttpHeaders.AUTHORIZATION, "ApiKey " + apiKey);
          return headers;
        }).build();
  }
}
