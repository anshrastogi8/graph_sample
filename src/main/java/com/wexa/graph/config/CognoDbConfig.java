package com.wexa.graph.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognoDbConfig {

	@Value("${COGNODB_URI}")
	private String uri;

	@Value("${COGNODB_USER}")
	private String user;

	@Value("${COGNODB_PASSWORD}")
	private String password;

	@Bean
	public Driver neo4jDriver() {
		return  GraphDatabase.driver(uri,AuthTokens.basic("cognodb", password));
	}
}
