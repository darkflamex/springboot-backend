package com.kbsec.mydata.authentication.response;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse {

	private int status;
	private String code;
	private String message;
	
	@JsonInclude(value = Include.NON_NULL)
	private String messageDev;
	private Object data;

}
