package com.kbsec.mydata.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

//	@Bean
//	public MessageSource messageSource() {
//	    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//	    messageSource.setBasename("classpath:messagess");
//	    messageSource.setCacheSeconds(10); //reload messages every 10 seconds
//	    return messageSource;
//	}
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		// 메세지 프로퍼티파일의 위치와 이름을 지정한다.
		source.setBasename("classpath:/messages/message");
		// 기본 인코딩을 지정한다.
		source.setDefaultEncoding("UTF-8");
		// 프로퍼티 파일의 변경을 감지할 시간 간격을 지정한다.
		source.setCacheSeconds(60);
		// 없는 메세지일 경우 예외를 발생시키는 대신 코드를 기본 메세지로 한다.
		source.setUseCodeAsDefaultMessage(true);
		return source;
	}
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		// TODO Auto-generated method stub
//		//WebMvcConfigurer.super.addResourceHandlers(registry);
//
//		registry.addResourceHandler("/resources/**");
//	}
	
	
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry
//        	// 다운로드 매핑할 uri 지정
//			.addResourceHandler("/resources/**")
//			// 실제 파일이 존재하는 디렉토리 지정(일반적으로 application.properties 를 이용한다)
//			.addResourceLocations(FILE_ROOT.toUri().toString());
//	}
}
