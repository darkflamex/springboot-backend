package com.kbsec.mydata.swagger;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@ConditionalOnWebApplication
//@EnableSwagger2
//@Configuration 
public class Swagger2Config { 
	public static final String AUTHORIZATION_HEADER = "Authorization";

	
	@Bean public Docket api() { 
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.securityContexts(Arrays.asList(securityContext()))
		        .securitySchemes(Arrays.asList(apiKey()))
				.select() 
				.apis(RequestHandlerSelectors.basePackage("com.youandi.backend.controller")) 
				.paths(PathSelectors.ant("/v1/api/**")) 
				.build(); 
		
	} 
	
	private ApiInfo apiInfo() { 
		return new ApiInfoBuilder() 
				.title("제목 작성") 
				.version("버전 작성") 
				.description("설명 작성") 
				.license("라이센스 작성") 
				.licenseUrl("라이센스 URL 작성") 
				.build(); 
	}
	
	
	private ApiKey apiKey() {
	    return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	  }

	  private SecurityContext securityContext() {
	    return SecurityContext.builder()
	        .securityReferences(defaultAuth())
	        .build();
	  }

	  List<SecurityReference> defaultAuth() {
	    AuthorizationScope authorizationScope
	        = new AuthorizationScope("global", "accessEverything");
	    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	    authorizationScopes[0] = authorizationScope;
	    return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	  }
}

