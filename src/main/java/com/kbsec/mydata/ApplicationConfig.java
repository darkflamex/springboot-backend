package com.kbsec.mydata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbsec.mydata.authentication.AuthenticationService;
import com.kbsec.mydata.authentication.config.AuthenticationConfig;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.url.AuthorizeRequestUrl;

@Configuration
public class ApplicationConfig {
	
	@Autowired
	private RedisService redisService;
		
	@Autowired
	private AuthenticationConfig tokenConfig;
	
	@Bean
	public AuthenticationService tokenAuthService() {
		return new AuthenticationService(redisService, tokenConfig);
	}
	
	@Bean
	public ObjectMapper mapper(){
		return new ObjectMapper();
	}
}
