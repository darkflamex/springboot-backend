package com.kbsec.mydata.authentication.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.kbsec.mydata.authentication.SignInAuthenticationToken;
import com.kbsec.mydata.authentication.entity.JWTTokenEntity;
import com.kbsec.mydata.authentication.JwtAuthenticationTokenImpl;
import com.kbsec.mydata.authentication.KBUser;
import com.kbsec.mydata.authentication.RefreshAuthenticationTokenImpl;
import com.kbsec.mydata.authentication.redis.RedisEntity;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Sign-In Filter
 * @author skyrun
 *
 */
public class RefreshTokenFilter extends AbstractAuthenticationProcessingFilter {

	
	private final String ACCESS_TOKEN_PREFIX = "ACCESS_";
	private final String REFRESH_TOKEN_PREFIX = "REFRESH_";
    private final String SID_PREFIX = "SID_";
	
	static final Logger logger = LoggerFactory.getLogger(RefreshTokenFilter.class);
	
	private RedisService redisService;
	private String secret;
	private ApiTokenConfig apiTokenConfig;
	
    public RefreshTokenFilter(String url, AuthenticationManager authenticationManager, RedisService redisService, ApiTokenConfig apiTokenConfig) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authenticationManager);
        
        this.redisService = redisService;
        this.apiTokenConfig = apiTokenConfig;
        secret = Sha512DigestUtils.shaHex(apiTokenConfig.getSigningKey());
    }   

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {    	
    	return getAuthenticationManager()
    			
    			.authenticate(extract(httpServletRequest));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        
    	logger.info("Successful Auth=========");
    	
    	RefreshAuthenticationTokenImpl auth = (RefreshAuthenticationTokenImpl) authentication;
    	String refreshToken = (String)auth.getPrincipal();
    	
    	logger.error("refreshToken : " + refreshToken);
    	
    	KBUser user = (KBUser) redisService.getValue(REFRESH_TOKEN_PREFIX + refreshToken, KBUser.class);
    	
    	if(redisService.hasKey(ACCESS_TOKEN_PREFIX + user.getAccessToken())) {
    		
    		// 이전에 발급한 accessToken 이 존재한다면
    		// accessToken 관련 삭제  
    		
    		redisService.delete(ACCESS_TOKEN_PREFIX + user.getAccessToken());
    		redisService.delete(SID_PREFIX + ACCESS_TOKEN_PREFIX + user.getSid());
    	}
    	
    	// accessToken 신규 생
       	String accessToken = UUID.nameUUIDFromBytes(LocalDateTime.now().toString().getBytes()).toString();
       	user.setAccessToken(accessToken);
       	
       	
       	Map<String, Object> claims = new HashMap<>();
		
       	logger.info("apiTokenConfig.getExpired() : " + apiTokenConfig.getExpired().toString());
           	
       	claims.put("accessToken", accessToken);
       	String accessTokenJwt = Jwts.builder()
				.setSubject("accessToken")
				.setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis() + apiTokenConfig.getExpired()))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();

       	// accessToken token store에 저장
       	RedisEntity accessTokenEntity = RedisEntity.builder()
    			.key(ACCESS_TOKEN_PREFIX + accessToken)
    			.value(user)
    			.unit(TimeUnit.SECONDS)
    			.timeout(apiTokenConfig.getExpired())
    			.build();
    	
    	RedisEntity sidAccessToken = RedisEntity.builder()
    			.key(SID_PREFIX + ACCESS_TOKEN_PREFIX + user.getSid())
    			.value(accessToken)
    			.unit(TimeUnit.SECONDS)
    			.timeout(apiTokenConfig.getExpired())
    			.build();
    
    	LocalDateTime now = LocalDateTime.now();
    	
    	
    	redisService.add( Arrays.<RedisEntity>asList(
    			accessTokenEntity
        ), now);
       	
    	redisService.add( Arrays.<RedisEntity>asList( 
    			sidAccessToken
        ), now);
    	
    	// response 출
    	JWTTokenEntity jwtToken = JWTTokenEntity.builder()
    			.accessToken(accessTokenJwt)
    			.build();
    	
    	
    	ServletServerHttpResponse res = new ServletServerHttpResponse(response);
        res.setStatusCode(HttpStatus.ACCEPTED);
        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.getBody().write(new Gson().toJson(jwtToken).toString().getBytes());

    	
    }
    
    
    private JwtAuthenticationTokenImpl extract(HttpServletRequest httpServletRequest) {
    	JwtAuthenticationTokenImpl jwtAuthenticationTokenImpl = null;
    	String token = httpServletRequest.getHeader(ApiTokenConfig.AUTHORIZATION_HEADER_NAME);
    	jwtAuthenticationTokenImpl = new JwtAuthenticationTokenImpl(token);
    	return jwtAuthenticationTokenImpl;
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
