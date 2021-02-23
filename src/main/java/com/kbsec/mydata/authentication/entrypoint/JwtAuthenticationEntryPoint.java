package com.kbsec.mydata.authentication.entrypoint;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.google.gson.Gson;
import com.kbsec.mydata.authentication.exception.KBAuthenticationException;
import com.kbsec.mydata.common.domain.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;
	private MessageSource messageSource;
	
	@Value("${spring.profiles.active}")
	private String profile;
	
	public JwtAuthenticationEntryPoint(MessageSource messageSource) {
		this.messageSource = messageSource;
		//this.resolver = resolver;
	}


	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {


		
		if(authException instanceof KBAuthenticationException) {
		
			log.error("** Error:" + authException.getMessage());
			
			KBAuthenticationException kbAuthException = (KBAuthenticationException)authException;
			
			// LocaleContextHolder.getLocale()
			String message = messageSource.getMessage("error.message." + kbAuthException.getCode(), null, LocaleContextHolder.getLocale());
			
			ApiResponse apiResponse = ApiResponse.builder()
					.status(HttpServletResponse.SC_UNAUTHORIZED)
					.code(kbAuthException.getCode())
					.message(message)
					.data("")
					.build();
		
			log.info("profile : " + profile);
			
			ServletServerHttpResponse res = new ServletServerHttpResponse(response);
	        res.setStatusCode(HttpStatus.UNAUTHORIZED);
	        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
	        res.getBody().write(new Gson().toJson(getApiResponse(kbAuthException)).toString().getBytes());
			
			//response.sendError(HttpServletResponse.SC_UNAUTHORIZED, new Gson().toJson(apiResponse).toString());
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}		
	}
	
	private ApiResponse getApiResponse(KBAuthenticationException kbAuthException ) {
		String message = messageSource.getMessage("error.message." + kbAuthException.getCode(), null, LocaleContextHolder.getLocale());
	
		ApiResponse apiResponse = null;
		if(!"prd".equals(profile)) {
		
			apiResponse = ApiResponse.builder()
					.status(HttpServletResponse.SC_UNAUTHORIZED)
					.code(kbAuthException.getCode())
					.messageDev("")
					.messageDev(kbAuthException.getReason())
					.message(message)
					.data("")
					.build();
		
		} else {
			apiResponse = ApiResponse.builder()
					.status(HttpServletResponse.SC_UNAUTHORIZED)
					.code(kbAuthException.getCode())
					.messageDev("")
					.message(message)
					.data("")
					.build();
			
		}
		
		
		return apiResponse;
	}
	
	
	
	
	
}