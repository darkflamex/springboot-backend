package com.youandi.backend.auth.exception;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

import lombok.Getter;

@SuppressWarnings("serial")
public class DefaultNestedRuntimeException extends NestedRuntimeException {

	@Getter
	private final String code;
	
	@Nullable
	@Getter
	private final String reason;
	
	@Nullable
	@Getter
	private final Object data;
	
	public DefaultNestedRuntimeException(String code) {
		// TODO Auto-generated constructor stub
		this(code, null, null);
	}
	
	public DefaultNestedRuntimeException(String code, String reason, Object data) {
		super(code);
		// TODO Auto-generated constructor stub
		this.code = code;
		this.reason = reason;
		this.data = data;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	

}
