
package com.kbsec.mydata.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.Sha512DigestUtils;

import com.kbsec.mydata.authentication.config.AuthenticationConfig;
import com.kbsec.mydata.authentication.jsonwebtoken.JwtUtils;
import com.kbsec.mydata.authentication.redis.RedisService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshTokenAuthenticationProviderImpl implements AuthenticationProvider {

	private RedisService redisService;
	private String secret;
	
    public RefreshTokenAuthenticationProviderImpl(AuthenticationConfig tokenConfig, RedisService service) {
    	this.redisService = service;
    	this.secret = Sha512DigestUtils.shaHex(tokenConfig.getSigningKey());
        
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    	log.info(RefreshTokenAuthenticationProviderImpl.class.toGenericString());
    	
    	String refreshTokenJwt = (String)authentication.getPrincipal();
    	
    	log.info("refreshTokenJwt = " + refreshTokenJwt);
    	refreshTokenJwt = refreshTokenJwt.replace(AuthenticationConfig.AUTHORIZATION_HEADER_PREFIX, "").trim();


        if (refreshTokenJwt != null && !refreshTokenJwt.isEmpty()) {
            Claims claims = JwtUtils.parse(refreshTokenJwt, secret);
            log.error("claims = " + claims.toString());
        	
            if (claims != null && claims.containsKey("refreshToken")) {
                String refreshToken = claims.get("refreshToken").toString();
                log.info("refreshToken == " + refreshToken);
                KBUser user = (KBUser) redisService.getValue(AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken, KBUser.class);
                if (user != null) {
                	RefreshAuthenticationTokenImpl auth = new RefreshAuthenticationTokenImpl(refreshToken, AuthorityUtils.createAuthorityList("USER"));
                    auth.setDetails(user);
                    auth.setAuthenticated(true);
                    return auth;
                } else {
                    return new RefreshAuthenticationTokenImpl(null);
                }
            }  
        }
		return new RefreshAuthenticationTokenImpl(null);
    }

    
    
    @Override
    public boolean supports(Class<?> type) {
        return type.equals(RefreshTokenAuthenticationTokenImpl.class);
    }
    
    

}
