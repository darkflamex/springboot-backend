package com.youandi.backend.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.youandi.backend.auth.service.TokenAuthenticationService;

public class JWTFilter extends OncePerRequestFilter {
	private boolean ignoreFailure = false;


	private TokenAuthenticationService service;
	private AuthenticationEntryPoint authenticationEntryPoint;


	public JWTFilter(TokenAuthenticationService service, AuthenticationEntryPoint authenticationEntryPoint) {
		this.service = service;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub



		try {
			Authentication authentication = service.getAuthentication((HttpServletRequest) request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch(AuthenticationException ex) {
			SecurityContextHolder.clearContext();
			if (this.ignoreFailure) {
				filterChain.doFilter(request, response);
			}
			else {
				this.authenticationEntryPoint.commence((HttpServletRequest)request, (HttpServletResponse)response, ex);
			}
			return;

		}
		filterChain.doFilter(request, response);
	}

}
