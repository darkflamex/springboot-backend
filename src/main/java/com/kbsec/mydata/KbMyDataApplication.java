package com.kbsec.mydata;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbsec.mydata.authentication.TokenAuthenticationService;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAutoConfiguration
public class KbMyDataApplication  extends SpringBootServletInitializer {

	@Autowired
	private RedisService redisService;
	
//	@Bean
//	public ApiTokenConfig getApiTokenConfig() {
//		return new ApiTokenConfig();
//	}
	
	@Autowired
	private ApiTokenConfig tokenConfig;

	@Bean
	public TokenAuthenticationService tokenAuthService() {
		return new TokenAuthenticationService(redisService, tokenConfig);
	}
	
	@Bean
	public ObjectMapper mapper(){
		return new ObjectMapper();
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(KbMyDataApplication.class);
    }

	
	
	@ Order(1)
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// TODO Auto-generated method stub
		//super.onStartup(servletContext);
		logger.info("!!!!!!!");
	}

	public static void main(String[] args) {
		System.setProperty("spring.profiles.default", "test");
		SpringApplication.run(KbMyDataApplication.class, args);
	}

}
