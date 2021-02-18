package com.youandi.backend.auth.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix="api.token")
@Data
public class ApiTokenProperties {
	
	private String expired;
	private String refreshExpired;
	private String signingKey;
	
	
}
