package com.kbsec.mydata.authentication.entrypoint;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.google.gson.Gson;
import com.kbsec.mydata.authentication.exception.KBAuthenticationException;
import com.kbsec.mydata.authentication.response.ApiResponse;
import com.kbsec.mydata.authentication.response.DefaultApiResponseBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;
	private MessageSource messageSource;
	
	@Value("${spring.profiles.active}")
	private String profile;
	
	public CustomAuthenticationEntryPoint(MessageSource messageSource) {
		this.messageSource = messageSource;
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
	
	private void responseMsessage(HttpServletResponse response, KBAuthenticationException kbAuthException) throws IOException {
		ServletServerHttpResponse res = new ServletServerHttpResponse(response);
        res.setStatusCode(HttpStatus.UNAUTHORIZED);
        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.getBody().write(new Gson().toJson(getApiResponse(kbAuthException)).toString().getBytes());
		res.close();

	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		
		log.error("!!!!!!!!!!!!!!!!!!!!!!!");
		
		String profile = "dev";
		
		if(authException instanceof KBAuthenticationException) {
			KBAuthenticationException kbAuthException = (KBAuthenticationException)authException;
			
			
			log.error("KBAuthenticationException :" + authException.getMessage());
			
			ApiResponse apiResponse = DefaultApiResponseBuilder.defaultApiResponse(profile, messageSource, 
					HttpStatus.UNAUTHORIZED, kbAuthException.getCode(), kbAuthException.getReason(), kbAuthException.getMessage());
			
			DefaultApiResponseBuilder.responseWrite(response, apiResponse);
			
//			responseMsessage(response, kbAuthException);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}		
	}
	
	
	
	
	
	
	
}