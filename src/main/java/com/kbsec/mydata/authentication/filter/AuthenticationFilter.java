package com.kbsec.mydata.authentication.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbsec.mydata.authentication.SignInAuthenticationToken;
import com.kbsec.mydata.authentication.AuthenticationService;
import com.kbsec.mydata.authentication.exception.KBAuthenticationException;

/**
 * Sign-In Filter
 * @author skyrun
 *
 */
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	
    private AuthenticationService tokenAuthenticationService;

    public AuthenticationFilter(String url, AuthenticationManager authenticationManager, AuthenticationService service) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authenticationManager);
        tokenAuthenticationService = service;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {    	
    	return getAuthenticationManager()
    			.authenticate(extract(httpServletRequest));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        
    	logger.info("Successful Auth=========");
    	
    	SignInAuthenticationToken auth = (SignInAuthenticationToken) authentication;
        tokenAuthenticationService.addAuthentication(response, auth); 
    }
    
    
    private UsernamePasswordAuthenticationToken extract(HttpServletRequest httpServletRequest) {
    	
    	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
    	
    	String sid = httpServletRequest.getParameter("sid");
    	if(StringUtils.isBlank(sid)) {
    		throw new KBAuthenticationException("300", new BadCredentialsException("not found sid."));
    	}
		usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(sid, null);    	
    	return usernamePasswordAuthenticationToken;
    }

    
   
    
}
