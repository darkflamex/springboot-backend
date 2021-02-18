package com.youandi.backend.auth.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Builder;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix="api.url")
@Data
public class AuthorizeRequestUrl {
	private String signIn;
	private String signOut;
	
}
