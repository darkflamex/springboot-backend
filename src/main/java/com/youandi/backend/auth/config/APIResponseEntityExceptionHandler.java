package com.youandi.backend.auth.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.youandi.backend.auth.exception.KBAuthenticationException;

//@ControllerAdvice
public class APIResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
	
	//@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(KBAuthenticationException.class)
	public ResponseEntity<String> ab(KBAuthenticationException kbAuthE) {
		return new ResponseEntity<String>("hi", new HttpHeaders(),HttpStatus.OK);
	}
}
