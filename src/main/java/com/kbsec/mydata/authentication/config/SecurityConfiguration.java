
package com.kbsec.mydata.authentication.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.kbsec.mydata.authentication.RefreshTokenAuthenticationProviderImpl;
import com.kbsec.mydata.authentication.SignInAuthenticationProviderImpl;
import com.kbsec.mydata.authentication.AuthenticationService;
import com.kbsec.mydata.authentication.entrypoint.CustomAuthenticationEntryPoint;
import com.kbsec.mydata.authentication.filter.AuthenticationFilter;
import com.kbsec.mydata.authentication.filter.JWTFilter;
import com.kbsec.mydata.authentication.filter.RefreshTokenFilter;
import com.kbsec.mydata.authentication.filter.SignInFilter;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.url.AuthorizeRequestUrl;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationService tokenService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private AuthorizeRequestUrl authorizeRequestUrl;
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AuthenticationConfig tokenConfig;
	

	@Override
	protected AuthenticationManager authenticationManager() throws Exception {  	
		AuthenticationProvider[] arr = {
			(AuthenticationProvider) new SignInAuthenticationProviderImpl(tokenConfig, redisService),
			(AuthenticationProvider) new RefreshTokenAuthenticationProviderImpl(tokenConfig, redisService)
		};	
		return new ProviderManager(
				Arrays.asList(arr));
	}
	
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
		return bean;
	}


	/**
	 * 메세지 소스를 생성한다.
	 */

//	@Bean
//	public ReloadableResourceBundleMessageSource messageSource() {
//		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
//		// 메세지 프로퍼티파일의 위치와 이름을 지정한다.
//		source.setBasename("classpath:/messages/message");
//		// 기본 인코딩을 지정한다.
//		source.setDefaultEncoding("UTF-8");
//		// 프로퍼티 파일의 변경을 감지할 시간 간격을 지정한다.
//		source.setCacheSeconds(60);
//		// 없는 메세지일 경우 예외를 발생시키는 대신 코드를 기본 메세지로 한다.
//		source.setUseCodeAsDefaultMessage(true);
//		return source;
//	}
	
	



	/**
	 * 변경된 언어 정보를 기억할 로케일 리졸퍼를 생성한다.
	 * 여기서는 세션에 저장하는 방식을 사용한다.
	 * @return
	 */

	@Bean
	public SessionLocaleResolver localeResolver() {
		return new SessionLocaleResolver();

	}

	/*
	 * Spring security config 
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.headers().cacheControl();

		http.csrf().disable()
		.authorizeRequests()
		.antMatchers("/v1/api/**").authenticated()
		.and().httpBasic().disable()
		.csrf().disable()
		.cors().disable()
		
		// sign-in filter
		// .addFilterBefore(new SignInFilter(authorizeRequestUrl.getSignIn(), authenticationManager(), tokenService), UsernamePasswordAuthenticationFilter.class)
		
		//
		.addFilterBefore(new AuthenticationFilter(authorizeRequestUrl.getSignIn(), authenticationManager(), tokenService), UsernamePasswordAuthenticationFilter.class)
		
		
		// refresh token filter
		.addFilterBefore(new RefreshTokenFilter("/token", authenticationManager(), redisService, tokenConfig, messageSource), UsernamePasswordAuthenticationFilter.class)
		
//		.exceptionHandling()
//		.authenticationEntryPoint(jwtAuthenticationEntryPoint())
		
		// authentication filter
//		.and()
		.addFilterBefore(new JWTFilter(tokenService, jwtAuthenticationEntryPoint() ), UsernamePasswordAuthenticationFilter.class)
		;

	}

	@Bean("jwtAuthenticationEntryPoint")
	public CustomAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		CustomAuthenticationEntryPoint JwtAuthenticationEntryPoint = new CustomAuthenticationEntryPoint(messageSource);
		return JwtAuthenticationEntryPoint;
	}
	
	

}
