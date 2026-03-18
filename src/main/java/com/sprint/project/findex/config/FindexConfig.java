package com.sprint.project.findex.config;

import com.sprint.project.findex.dto.SortDirection;
import com.sprint.project.findex.dto.indexdata.IndexDataSortField;
import com.sprint.project.findex.dto.indexinfo.IndexInfoSortField;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.FormatterRegistry;
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

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(String.class, IndexDataSortField.class, IndexDataSortField::from);
    registry.addConverter(String.class, SortDirection.class, SortDirection::from);
    registry.addConverter(String.class, IndexInfoSortField.class, IndexInfoSortField::from);
  }
}
