package com.kbsec.mydata.authentication.token;


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
		
	public Long getRefreshExpired() {
		return Long.parseLong(apiTokenProperties.getRefreshExpired());
	}

	public String getSigningKey() {
		return apiTokenProperties.getSigningKey();
	}
	
	public Long getExpired() {
		return Long.parseLong(apiTokenProperties.getExpired());
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
