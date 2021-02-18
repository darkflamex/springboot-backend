package com.youandi.backend.auth.filter;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.h2.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youandi.backend.auth.common.ApiTokenConfig;
import com.youandi.backend.auth.domain.AuthenticationTokenImpl;
import com.youandi.backend.auth.service.TokenAuthenticationService;

/**
 * Sign-In Filter
 * @author skyrun
 *
 */
public class SignInFilter extends AbstractAuthenticationProcessingFilter {

	static final Logger logger = LoggerFactory.getLogger(SignInFilter.class);
	
	
    private TokenAuthenticationService tokenAuthenticationService;

    public SignInFilter(String url, AuthenticationManager authenticationManager, TokenAuthenticationService service) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authenticationManager);
        tokenAuthenticationService = service;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {    	
    	return getAuthenticationManager().authenticate(extract(httpServletRequest));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        
    	logger.info("Successful Auth=========");
    	
    	AuthenticationTokenImpl auth = (AuthenticationTokenImpl) authentication;
        tokenAuthenticationService.addAuthentication(response, auth); 
    }
    
    
    private UsernamePasswordAuthenticationToken extract(HttpServletRequest httpServletRequest) {
    	
    	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
    	Credentials credentials = null;
    	
    	String reqBody = "";
    	try {
			reqBody = org.apache.commons.io.IOUtils.toString(httpServletRequest.getInputStream(),"UTF-8");
			logger.error(reqBody);
    	} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	
    	try {
			// credentials = new ObjectMapper().readValue(httpServletRequest.getInputStream(), Credentials.class);
			credentials = new ObjectMapper().readValue(reqBody, Credentials.class);
			
			if(StringUtils.isBlank(credentials.getCredentials())) {
				throw new BadCredentialsException("not found credentials");
			}
			
			usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(credentials.getPrincipal(), credentials.getCredentials());
    	} catch (IOException e) {
			logger.error(e.getMessage(),e);
    		// TODO Auto-generated catch block
    		throw new AuthenticationServiceException(e.getMessage(),e);
		}
    	
    	return usernamePasswordAuthenticationToken;
    }
    
//    private String extract(String header) {
//        if (StringUtils.isBlank(header)) {
//            throw new AuthenticationServiceException("Authorization header cannot be blank!");
//        }
//
//        if (header.length() <= ApiTokenConfig.SID_HEADER_NAME.length()) {
//            throw new AuthenticationServiceException("Invalid authorization header size.");
//        }
//        return header.substring(ApiTokenConfig.SID_HEADER_NAME.length(), header.length());
//    }
    
   
    
}
