
package com.youandi.backend.auth.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.youandi.backend.auth.common.AuthorizeRequestUrl;
import com.youandi.backend.auth.filter.JWTAuthFilter;
import com.youandi.backend.auth.filter.JWTAuthenticationFilter;
import com.youandi.backend.auth.filter.JWTFilter;
import com.youandi.backend.auth.filter.SignInFilter;
import com.youandi.backend.auth.redis.RedisService;
import com.youandi.backend.auth.service.TokenAuthenticationService;


@Configuration
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private TokenAuthenticationService tokenService;

	@Autowired
	private RedisService redisService;

	@Autowired
	private AuthorizeRequestUrl authorizeRequestUrl;
	
	@Autowired
	private MessageSource messageSource;
	
//	@Autowired
//	private HandlerExceptionResolver resolver;
	

	@Override
	protected AuthenticationManager authenticationManager() throws Exception {  	
		return new ProviderManager(Arrays.asList((AuthenticationProviderImpl) new AuthenticationProviderImpl(redisService)));
	}

	/*
    private AuthenticationManager jwtAuthenticationManager() throws Exception {  	
        return new ProviderManager(Arrays.asList((JWTAuthenticationProviderImpl) new JWTAuthenticationProviderImpl(redisService)));
    } 
	 */



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
		// bean.setOrder(0);
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
		//             .anyRequest().authenticated()
		.antMatchers("/vi/api/**").authenticated()
		.and().httpBasic().disable()
		.csrf().disable()
		.cors().disable()
//		.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint())
//		.and()

//		.exceptionHandling(APIResponseEntityExceptionHandler())
		// sign-in filter
		.addFilterBefore(new SignInFilter(authorizeRequestUrl.getSignIn(), authenticationManager(), tokenService), UsernamePasswordAuthenticationFilter.class)

		// auth filter
		//.addFilterBefore(new JWTAuthenticationFilter(tokenService, new JwtAuthenticationEntryPoint()), UsernamePasswordAuthenticationFilter.class)

		// .addFilterBefore(new JWTAuthFilter(authenticationManager(),tokenService), UsernamePasswordAuthenticationFilter.class)

		//.addFilterBefore(new JWTFilter(tokenService), UsernamePasswordAuthenticationFilter.class)

		.addFilterBefore(new JWTFilter(tokenService, jwtAuthenticationEntryPoint() ), UsernamePasswordAuthenticationFilter.class)
//		.addFilterBefore(new JWTFilter(tokenService), UsernamePasswordAuthenticationFilter.class)

		;

	}

	
	@Bean("jwtAuthenticationEntryPoint")
	public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint(messageSource);
		return JwtAuthenticationEntryPoint;
	}
	
	

}
