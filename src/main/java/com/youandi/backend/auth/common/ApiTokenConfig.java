package com.youandi.backend.auth.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiTokenConfig {

	public static final String AUTHORIZATION_HEADER_NAME  = "Authorization";

	public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

	public static final String AUTHORIZATION_REFRESH_TOKEN_NAME = "REFRESH_TOKEN";
	
	public static final String SID_HEADER_NAME = "SID";
	
	
	@Autowired
	ApiTokenProperties apiTokenProperties;
		
	public int getRefreshExpired() {
		return Integer.parseInt(apiTokenProperties.getRefreshExpired());
	}

	public String getSigningKey() {
		return apiTokenProperties.getSigningKey();
	}
	
	public int getExpired() {
		return Integer.parseInt(apiTokenProperties.getExpired());
	}
	

//	public int getRefreshExpired() {
//		return 1000;
//	}
//
//	public String getSigningKey() {
//		return "test";
//	}
//	
//	public int getExpired() {
//		return 1000;
//	}

	
}
