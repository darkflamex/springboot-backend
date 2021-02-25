package com.kbsec.mydata.authentication;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.Sha512DigestUtils;

import com.kbsec.mydata.authentication.config.AuthenticationConfig;
import com.kbsec.mydata.authentication.config.DefaultApiResponse;
import com.kbsec.mydata.authentication.entity.JWTTokenEntity;
import com.kbsec.mydata.authentication.jsonwebtoken.JwtUtils;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.url.AuthorizeRequestUrl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationService {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	
    private RedisService service;

    private String secret;

    private AuthenticationConfig authenticationConfig;
    
    @Autowired
    private AuthorizeRequestUrl authorizeRequestUrl;
    
    
    public AuthenticationService(RedisService service, AuthenticationConfig authenticationConfig) {
    
    	this.authenticationConfig = authenticationConfig;	
    	this.service = service;
        secret = Sha512DigestUtils.shaHex(authenticationConfig.getSigningKey());
    }

     
    public void addAuthentication(HttpServletResponse response, SignInAuthenticationToken auth) throws IOException {
        
    	KBUser user  = (KBUser)auth.getDetails();
    	
    	String userCreated = user.getCreated();
    	LocalDateTime created = LocalDateTime.parse(userCreated, DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    	 		    	
    	logger.info("accessToken : " + user.getAccessToken());
    	logger.info("refreshToken : " + user.getRefreshToken());
        
    	Map<String, Object> accessTokenClaims = new HashMap<>();
    	accessTokenClaims.put(AuthenticationConfig.ACCESS_TOKEN, user.getAccessToken());
    	String accessTokenJwt = JwtUtils.build(AuthenticationConfig.ACCESS_TOKEN, accessTokenClaims, created, authenticationConfig.getExpired(), secret);
    	logger.info("accessTokenJwt : " + accessTokenJwt);
    	
    	Map<String, Object> refreshTokenClaims = new HashMap<>();
    	refreshTokenClaims.put(AuthenticationConfig.REFRESH_TOKEN, user.getRefreshToken());
    	String refreshTokenJwt = JwtUtils.build(AuthenticationConfig.REFRESH_TOKEN, refreshTokenClaims, created, authenticationConfig.getRefreshExpired(), secret);
    	logger.info("refreshTokenJwt : " + refreshTokenJwt);
    
    	response.sendRedirect(authorizeRequestUrl.getAuthorize() + "?accessToken=" + accessTokenJwt + "&refreshToken=" + refreshTokenJwt);
    	
    }
    
    public void addSignInAuthentication(HttpServletResponse response, SignInAuthenticationToken auth) throws IOException {
        // We generate a token now.
    	
    	KBUser user  = (KBUser)auth.getDetails();
    	
    	String userCreated = user.getCreated();
    	LocalDateTime created = LocalDateTime.parse(userCreated, DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    	 		    	
    	logger.info("accessToken : " + user.getAccessToken());
    	logger.info("refreshToken : " + user.getRefreshToken());
        
    	Map<String, Object> accessTokenClaims = new HashMap<>();
    	accessTokenClaims.put(AuthenticationConfig.ACCESS_TOKEN, user.getAccessToken());
    	String accessTokenJwt = JwtUtils.build(AuthenticationConfig.ACCESS_TOKEN, accessTokenClaims, created, authenticationConfig.getExpired(), secret);
    	logger.info("accessTokenJwt : " + accessTokenJwt);
    	
    	Map<String, Object> refreshTokenClaims = new HashMap<>();
    	refreshTokenClaims.put(AuthenticationConfig.REFRESH_TOKEN, user.getRefreshToken());
    	String refreshTokenJwt = JwtUtils.build(AuthenticationConfig.REFRESH_TOKEN, refreshTokenClaims, created, authenticationConfig.getRefreshExpired(), secret);
    	logger.info("refreshTokenJwt : " + refreshTokenJwt);
    	
    	JWTTokenEntity jwtToken = JWTTokenEntity.builder()
    			.accessToken(accessTokenJwt)
    			.refreshToken(refreshTokenJwt)
    			.build();
    	
    	DefaultApiResponse.response(response, jwtToken);
    	
		
    }

    public Authentication getAuthentication(HttpServletRequest request) throws AuthenticationException {
        String accessTokenJwt = request.getHeader(AuthenticationConfig.AUTHORIZATION_HEADER_NAME);
        if (accessTokenJwt == null) {
            return null;
        }
        //remove "Bearer" text
        accessTokenJwt = accessTokenJwt.replace(AuthenticationConfig.AUTHORIZATION_HEADER_PREFIX, "").trim();

        //Validating the token
        if (accessTokenJwt != null && !accessTokenJwt.isEmpty()) {
            // parsing the token.`
            Claims claims = JwtUtils.parse(accessTokenJwt, secret);
            
//            try {
//                claims = Jwts.parser()
//                        .setSigningKey(secret)
//                        .parseClaimsJws(token).getBody();
//            } catch (JwtException jwtException) {            	
//            	logger.error("**JWT Error :"+ jwtException.getMessage());
//            	throw new KBAuthenticationException("400",jwtException.getMessage());
//            }
//            
            if (claims != null && claims.containsKey("accessToken")) {
                String accessToken = claims.get("accessToken").toString();
                KBUser user = (KBUser) service.getValue(accessToken, KBUser.class);
                if (user != null) {
                    SignInAuthenticationToken auth = new SignInAuthenticationToken(user.getSid(), AuthorityUtils.createAuthorityList("USER"));
                    auth.setDetails(user);
                    auth.setAuthenticated(true);
                    return auth;
                } else {
                    return new UsernamePasswordAuthenticationToken(null, null);
                }
            }   
        }
        return null;
    }
}
