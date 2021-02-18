package com.youandi.backend.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import com.youandi.backend.auth.service.TokenAuthenticationService;


public class JWTAuthenticationFilter extends GenericFilterBean {

	
	
	private boolean ignoreFailure = false;
	private TokenAuthenticationService service;
	private AuthenticationEntryPoint authenticationEntryPoint;

	public JWTAuthenticationFilter(TokenAuthenticationService service, AuthenticationEntryPoint authenticationEntryPoint) {
		this.service = service;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		Authentication authentication = service.getAuthentication((HttpServletRequest) request);
		SecurityContextHolder.getContext().setAuthentication(authentication);		
		
//		try {
//			Authentication authentication = service.getAuthentication((HttpServletRequest) request);
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//		} catch(AuthenticationException ex) {
//			SecurityContextHolder.clearContext();
//			if (this.ignoreFailure) {
//				filterChain.doFilter(request, response);
//			}
//			else {
//				this.authenticationEntryPoint.commence((HttpServletRequest)request, (HttpServletResponse)response, ex);
//			}
//			return;
//		}
		filterChain.doFilter(request, response);
	}
}
