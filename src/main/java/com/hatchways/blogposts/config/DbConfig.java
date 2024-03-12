package com.hatchways.blogposts.config;

import javax.sql.DataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@EnableJpaAuditing
public class DbConfig {

  @Value("${blogposts.db.url}")
  private String dbUrl;

  @Bean
  public DataSource dataSource() {
    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.sqlite.JDBC");
    dataSource.setUrl(dbUrl);
    return dataSource;
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
