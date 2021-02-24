
package com.kbsec.mydata.authentication;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

import com.kbsec.mydata.authentication.config.AuthenticationConfig;
import com.kbsec.mydata.authentication.redis.RedisEntity;
import com.kbsec.mydata.authentication.redis.RedisService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignInAuthenticationProviderImpl implements AuthenticationProvider {

	private AuthenticationConfig authenticationConfig;
	private RedisService redisService;

    
    public SignInAuthenticationProviderImpl(AuthenticationConfig authenticationConfig, RedisService service) {
        this.authenticationConfig = authenticationConfig;
    	this.redisService = service;
        
    }
    
    /*
     * Sign-In 시 
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	log.info(SignInAuthenticationProviderImpl.class.toString());
    	
    	String sid = (String)authentication.getPrincipal();
    	
    	KBUser user = null;
    	String accessToken 	= (String)redisService.getValue(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.ACCESS_TOKEN_PREFIX + sid, String.class);
    	String refreshToken = (String)redisService.getValue(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.REFRESH_TOKEN_PREFIX + sid, String.class);

//    	log.info("accessToken : " + accessToken);
//    	log.info("refreshToken : " + accessToken);
   	
    	// TO-DO
    	// 
    	if(StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(refreshToken)) {
    		// 발급된 AccessToken, RefreshToken 이 있다면 
    		// 이전 AccessToken, RefreshToken 신규 발급 token 으로 교체 
    		// AccessToken, RefreshToken 재발급 
    		
    		redisService.delete(AuthenticationConfig.ACCESS_TOKEN_PREFIX + accessToken);
    		redisService.delete(AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken);
    		accessToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		refreshToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		
//    	 	log.info("2 accessToken : " + accessToken);
//        	log.info("2 refreshToken : " + accessToken);
       	} else {
       		accessToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		refreshToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    	
       	}
    	LocalDateTime now = LocalDateTime.now();
    	String sNow = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")); 
    			
    	user = KBUser.builder()
    	.sid(sid)
    	.accessToken(accessToken)
    	.refreshToken(refreshToken)
    	.created(sNow)
    	.accessTokenIssued(sNow)
    	.refreshTokenIssued(sNow)
    	.build();

    	RedisEntity accessTokenEntity = RedisEntity.builder()
    			.key(AuthenticationConfig.ACCESS_TOKEN_PREFIX + accessToken)
    			.value(user)
    			.unit(TimeUnit.SECONDS)
    			.timeout(authenticationConfig.getExpired())
    			.build();
    	
    	log.info("AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken : " + AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken);
    	log.info("AuthenticationConfig.ACCESS_TOKEN_PREFIX + accessToken : " + AuthenticationConfig.ACCESS_TOKEN_PREFIX + accessToken);
    	
    	RedisEntity refreshTokenEntity = RedisEntity.builder()
    			.key(AuthenticationConfig.REFRESH_TOKEN_PREFIX + refreshToken)
    			.value(user)
    			.unit(TimeUnit.SECONDS)
    			.timeout(authenticationConfig.getRefreshExpired())
    			.build();
    	
    	RedisEntity sidAccessToken = RedisEntity.builder()
    			.key(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.ACCESS_TOKEN_PREFIX + sid)
    			.value(accessToken)
    			.unit(TimeUnit.SECONDS)
    			.timeout(authenticationConfig.getRefreshExpired())
    			.build();
    	
    	RedisEntity sidRefreshToken = RedisEntity.builder()
    			.key(AuthenticationConfig.SID_PREFIX + AuthenticationConfig.REFRESH_TOKEN_PREFIX + sid)
    			.value(refreshToken)
    			.unit(TimeUnit.SECONDS)
    			.timeout(authenticationConfig.getRefreshExpired())
    			.build();
    	
    	
    	redisService.add( Arrays.<RedisEntity>asList(
    			accessTokenEntity, 
    			refreshTokenEntity
    			
    	), now);
    	
    	redisService.add( Arrays.<RedisEntity>asList(
    			sidAccessToken,
    			sidRefreshToken
    	), now);
    	
    	log.info("accessToken : " + user.getAccessToken());
    	log.info("refreshToken : " + user.getRefreshToken());
    
    	SignInAuthenticationToken signAuthToken = new SignInAuthenticationToken(sid, AuthorityUtils.createAuthorityList("USER"));
    	signAuthToken.setAuthenticated(true);
    	signAuthToken.setDetails(user);
    	
    	log.info("KB User : " + user.toString());
    	
    	return signAuthToken;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }
}
