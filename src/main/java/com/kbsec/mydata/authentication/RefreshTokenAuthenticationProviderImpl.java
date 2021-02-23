
package com.kbsec.mydata.authentication;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.Sha512DigestUtils;

import com.kbsec.mydata.authentication.exception.KBAuthenticationException;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshTokenAuthenticationProviderImpl implements AuthenticationProvider {

	private final String ACCESS_TOKEN_PREFIX = "ACCESS_";
	private final String REFRESH_TOKEN_PREFIX = "REFRESH_";
    
	
	private ApiTokenConfig tokenConfig;
	private RedisService redisService;

    
	private String secret;
	
    public RefreshTokenAuthenticationProviderImpl(ApiTokenConfig tokenConfig, RedisService service) {
        this.tokenConfig = tokenConfig;
    	this.redisService = service;
    	this.secret = Sha512DigestUtils.shaHex(tokenConfig.getSigningKey());
        
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    	log.info(RefreshTokenAuthenticationProviderImpl.class.toGenericString());
    	
    	String token = (String)authentication.getPrincipal();
    	
    	log.error("token = " + token);
    	token = token.replace(ApiTokenConfig.AUTHORIZATION_HEADER_PREFIX, "").trim();


        if (token != null && !token.isEmpty()) {
            // parsing the token.`
            Claims claims = null;
            try {
                claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token).getBody();
            } catch (JwtException jwtException) {
            	
            	log.error("**!!!!1Error :"+ jwtException.getMessage());
            	throw new KBAuthenticationException("400",jwtException.getMessage());
            
            }
            
            if (claims != null && claims.containsKey("refreshToken")) {
                String refreshToken = claims.get("refreshToken").toString();
                log.error("refreshToken == " + refreshToken);
                KBUser user = (KBUser) redisService.getValue(REFRESH_TOKEN_PREFIX + refreshToken, KBUser.class);
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
        return type.equals(JwtAuthenticationTokenImpl.class);
    }
    
    

}
