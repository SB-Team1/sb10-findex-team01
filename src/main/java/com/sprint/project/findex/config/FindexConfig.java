package com.sprint.project.findex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaAuditing
public class FindexConfig implements WebMvcConfigurer {

  private final String PREFIX_ENDPOINT = "/api";

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.addPathPrefix(PREFIX_ENDPOINT,
        HandlerTypePredicate.forBasePackage("com.sprint.project.findex"));
  }
}
