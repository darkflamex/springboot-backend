package com.kbsec.mydata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableAutoConfiguration
public class KbMyDataApplication  extends SpringBootServletInitializer {

//	@Autowired
//	private RedisService redisService;
//		
//	@Autowired
//	private ApiTokenConfig tokenConfig;
//
//	@Bean
//	public TokenAuthenticationService tokenAuthService() {
//		return new TokenAuthenticationService(redisService, tokenConfig);
//	}
//	
//	@Bean
//	public ObjectMapper mapper(){
//		return new ObjectMapper();
//	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(KbMyDataApplication.class);
    }

	public static void main(String[] args) {
		System.setProperty("spring.profiles.default", "test");
		SpringApplication.run(KbMyDataApplication.class, args);
	}

}
