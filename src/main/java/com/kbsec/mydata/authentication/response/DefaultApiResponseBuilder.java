package com.kbsec.mydata.authentication.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;

import com.google.gson.Gson;

public class DefaultApiResponseBuilder {

	public static void responseWrite(HttpServletResponse response, ApiResponse apiResponse) throws IOException {
		ServletServerHttpResponse res = new ServletServerHttpResponse(response);
        res.setStatusCode(HttpStatus.valueOf(apiResponse.getStatus()));
        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.getBody().write(new Gson().toJson(apiResponse).toString().getBytes());
    	res.close();
	}
	

	public static ApiResponse defaultApiResponse(String profile, MessageSource messageSource, 
			HttpStatus httpStatus, String code, String messageDev, Object data ) {
		
		String message = messageSource.getMessage("error.message." + code, null, LocaleContextHolder.getLocale());
		
		ApiResponse apiResponse = null;
		if(!"prd".equals(profile)) {
		
			apiResponse = ApiResponse.builder()
					.status(httpStatus.value())
					.code(code)
					.messageDev("")
					.messageDev(messageDev)
					.message(message)
					.data(data)
					.build();
		
		} else {
			apiResponse = ApiResponse.builder()
					.status(httpStatus.value())
					.code(code)
					.messageDev("")
					.message(message)
					.data(data)
					.build();
			
		}	
		return apiResponse;
	}

}
