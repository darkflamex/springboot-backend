package com.kbsec.mydata.authentication.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationConfig {

	public static final String AUTHORIZATION_HEADER_NAME  = "Authorization";

	public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

	public static final String AUTHORIZATION_REFRESH_TOKEN_NAME = "REFRESH_TOKEN";

	public static final String SID_HEADER_NAME = "SID";

	public static final String ACCESS_TOKEN = "accessToken";
	public static final String REFRESH_TOKEN = "refreshToken";


	public static final String ACCESS_TOKEN_PREFIX = "accessToken_";
	public static final String REFRESH_TOKEN_PREFIX = "refreshToken_";
	public static final String SID_PREFIX = "SID_";



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
