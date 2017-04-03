package com.example.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
/*this tells the boot that this applies only to the cloud db*/
@Profile("cloud")
public class CloudDataSourceConfiguration {

	@Bean
	public Cloud cloud() {
		return new CloudFactory().getCloud();
	}

	/*
	 * here this would connect to the pcfdev-mysql service in the pcf, to update
	 * the name of the service use cloud ui
	 */
	@Bean
	// @ConfigurationProperties(DataSourceProperties.PREFIX)
	public DataSource dataSource() {
		return cloud().getServiceConnector("pcfdev-mysql", DataSource.class,
				null);
	}

}