
package com.kbsec.mydata.authentication;

import java.time.LocalDateTime;
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

import com.kbsec.mydata.authentication.entity.JWTTokenEntity;
import com.kbsec.mydata.authentication.redis.RedisEntity;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationProviderImpl implements AuthenticationProvider {

	private final String ACCESS_TOKEN_PREFIX = "ACCESS_";
	private final String REFRESH_TOKEN_PREFIX = "REFRESH_";
    private final String SID_PREFIX = "SID_";
	
	private ApiTokenConfig tokenConfig;
	private RedisService redisService;

    
    public AuthenticationProviderImpl(ApiTokenConfig tokenConfig, RedisService service) {
        this.tokenConfig = tokenConfig;
    	this.redisService = service;
        
    }
    
    /*
     * Sign-In 시 
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	log.info(AuthenticationProviderImpl.class.toString());
    	
    	String sid = (String)authentication.getPrincipal();
    	
    	KBUser user = null;
    	String accessToken 	= (String)redisService.getValue(SID_PREFIX + ACCESS_TOKEN_PREFIX + sid, String.class);
    	String refreshToken = (String)redisService.getValue(SID_PREFIX + REFRESH_TOKEN_PREFIX + sid, String.class);

    	log.info("1 accessToken : " + accessToken);
    	log.info("1 refreshToken : " + accessToken);
   	
    	// TO-DO
    	// 
    	if(StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(refreshToken)) {
    		// 발급된 AccessToken, RefreshToken 이 있다면 
    		// 이전 AccessToken, RefreshToken 신규 발급 token 으로 교체 
    		// AccessToken, RefreshToken 재발급 
    		
    		redisService.delete(ACCESS_TOKEN_PREFIX + accessToken);
    		redisService.delete(REFRESH_TOKEN_PREFIX + refreshToken);
    		accessToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		refreshToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		
    	 	log.info("2 accessToken : " + accessToken);
        	log.info("2 refreshToken : " + accessToken);
       	} else {
       		accessToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    		refreshToken 	= UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
    	
       	}
    	
    	
    	
    	user = KBUser.builder()
    	.sid(sid)
    	.accessToken(accessToken)
    	.refreshToken(refreshToken)
    	.created(new Date())
    	.build();

    	
    	
    	RedisEntity accessTokenEntity = RedisEntity.builder()
    			.key(ACCESS_TOKEN_PREFIX + accessToken)
    			.value(user)
    			.unit(TimeUnit.SECONDS)
    			.timeout(tokenConfig.getExpired())
    			.build();
    	RedisEntity refreshTokenEntity = RedisEntity.builder()
    			.key(REFRESH_TOKEN_PREFIX + refreshToken)
    			.value(user)
    			.unit(TimeUnit.SECONDS)
    			.timeout(tokenConfig.getRefreshExpired())
    			.build();
    	
    	RedisEntity sidAccessToken = RedisEntity.builder()
    			.key(SID_PREFIX + ACCESS_TOKEN_PREFIX + sid)
    			.value(accessToken)
    			.unit(TimeUnit.SECONDS)
    			.timeout(tokenConfig.getRefreshExpired())
    			.build();
    	
    	RedisEntity sidRefreshToken = RedisEntity.builder()
    			.key(SID_PREFIX + REFRESH_TOKEN_PREFIX + sid)
    			.value(refreshToken)
    			.unit(TimeUnit.SECONDS)
    			.timeout(tokenConfig.getRefreshExpired())
    			.build();
    	
    	LocalDateTime now = LocalDateTime.now();
    	
    	redisService.add( Arrays.<RedisEntity>asList(
    			accessTokenEntity, 
    			refreshTokenEntity
    			
    	), now);
    	
    	
    	redisService.add( Arrays.<RedisEntity>asList(
    			sidAccessToken,
    			sidRefreshToken
    	), now);
    	
    	
		// redisService.put(ACCESS_TOKEN_PREFIX + accessToken, user, TimeUnit.SECONDS, tokenConfig.getExpired());
		// redisService.put(REFRESH_TOKEN_PREFIX + refreshToken, user, TimeUnit.SECONDS, tokenConfig.getRefreshExpired());
		
    	// redisService.put(SID_PREFIX + REFRESH_TOKEN_PREFIX + sid, refreshToken, TimeUnit.SECONDS, tokenConfig.getRefreshExpired());
		// redisService.put(SID_PREFIX + ACCESS_TOKEN_PREFIX + sid, accessToken, TimeUnit.SECONDS, tokenConfig.getExpired());

    	log.info("accessToken : " + user.getAccessToken());
    	log.info("refreshToken : " + user.getRefreshToken());
    
    	
    	
    	SignInAuthenticationToken signAuthToken = new SignInAuthenticationToken(sid, AuthorityUtils.createAuthorityList("USER"));
    	signAuthToken.setAuthenticated(true);
    	signAuthToken.setDetails(user);
    	
    	
    	
    	return signAuthToken;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }
    


    	
//      if (username == null || username.length() < 5) {
//      throw new BadCredentialsException("Username not found.");
//  }
//  if (password.length() < 5) {
//      throw new BadCredentialsException("Wrong password.");
//  }

}
