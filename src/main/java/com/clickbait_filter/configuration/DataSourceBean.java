package com.clickbait_filter.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceBean {
	
	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    @Primary
    public DataSource getDataSource() {
		return DataSourceBuilder
                .create()
                .url(url)
                .driverClassName(driverClassName)
                .build();
    }
}