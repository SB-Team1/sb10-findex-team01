package com.sprint.project.findex.config.openapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class OpenApiConfig {

  private final String ENDPOINT = "/getStockMarketIndex";

  @Value("${OPENAPI_URI}")
  private String url;

  @Value("${OPENAPI_KEY}")
  private String apiKey;

  @Bean
  public WebClient openapi() {
    String defaultURI = UriComponentsBuilder.fromUriString(url())
        .queryParam("serviceKey", apiKey)
        .queryParam("resultType", "json")
        .build()
        .toUriString();
    DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(defaultURI);
    factory.setEncodingMode(EncodingMode.VALUES_ONLY);

    return WebClient.builder()
        .uriBuilderFactory(factory)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private String url() {
    return url.concat(ENDPOINT);
  }
}
