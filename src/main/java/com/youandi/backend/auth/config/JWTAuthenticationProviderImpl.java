package com.youandi.backend.auth.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.youandi.backend.auth.redis.RedisService;

public class JWTAuthenticationProviderImpl implements org.springframework.security.authentication.AuthenticationProvider {

	private RedisService service;

    public JWTAuthenticationProviderImpl(RedisService service) {
        this.service = service;
    }
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return false;
	}

}
