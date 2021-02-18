/**
 * 
 */
package com.youandi.backend.auth.exception;

import org.springframework.security.core.AuthenticationException;
/**
 * @author skyrun
 *
 */
public class CustomJWTException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1375197353168958470L;

	
	
	public CustomJWTException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}



	public CustomJWTException(String msg, Throwable cause) {
		super(msg, cause);
		// TODO Auto-generated constructor stub
	}

}
