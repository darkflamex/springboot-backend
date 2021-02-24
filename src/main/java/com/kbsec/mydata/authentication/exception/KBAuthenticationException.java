package com.kbsec.mydata.authentication.exception;

import org.springframework.lang.Nullable;
import org.springframework.security.core.AuthenticationException;

import lombok.Getter;

@SuppressWarnings("serial")
public class KBAuthenticationException extends AuthenticationException{

	@Getter
	private final String code;
	
	@Nullable
	@Getter
	private final String reason;
	
	@Nullable
	@Getter
	private final Object data;
	
	public KBAuthenticationException(String code) {
		// TODO Auto-generated constructor stub
		this(code, null, null);
	}
	
	
	
	public KBAuthenticationException(String code, String reason) {
		// TODO Auto-generated constructor stub
		this(code, reason, null);
	}
	
	public KBAuthenticationException(String code, String reason, Object data) {
		super(code);
		// TODO Auto-generated constructor stub
		this.code = code;
		this.reason = reason;
		this.data = data;
	}
	
	public KBAuthenticationException(String code, Throwable throwable) {
		// TODO Auto-generated constructor stub
		super(code, throwable);
		this.code = code;
		this.reason = throwable.getMessage();
		this.data = null;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	

}
