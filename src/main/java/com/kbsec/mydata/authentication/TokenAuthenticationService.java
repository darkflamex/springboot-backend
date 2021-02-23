package com.kbsec.mydata.authentication;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.Sha512DigestUtils;

import com.google.gson.Gson;
import com.kbsec.mydata.authentication.SignInAuthenticationToken;
import com.kbsec.mydata.authentication.KBUser;
import com.kbsec.mydata.authentication.entity.JWTTokenEntity;
import com.kbsec.mydata.authentication.exception.KBAuthenticationException;
import com.kbsec.mydata.authentication.redis.RedisService;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);
	
    private RedisService service;

    private String secret;

    private ApiTokenConfig apiTokenConfig;
    
    public TokenAuthenticationService(RedisService service, ApiTokenConfig apiTokenConfig) {
    
    	this.apiTokenConfig = apiTokenConfig;	
    	this.service = service;
        secret = Sha512DigestUtils.shaHex(apiTokenConfig.getSigningKey());
    }

    public String accessTokenByJWT( ApiTokenConfig apiTokenConfig,
    		String accessToken, String secret) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("accessToken", accessToken);
		//claims.put("hash", authToken.getHash());

		String accessTokenJWT = Jwts.builder()
				.setSubject("accessToken")
				.setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis() + apiTokenConfig.getExpired()))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();

		return accessTokenJWT;

	}

	public String refreshTokenByJWT( ApiTokenConfig apiTokenConfig,
			String refreshToken, String secret) {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("refreshToken", refreshToken);
		
		
		String refreshTokenByJWT = Jwts.builder()
				.setSubject("refreshToken")
				.setClaims(claims)
				.setExpiration(new Date(System.currentTimeMillis() + apiTokenConfig.getRefreshExpired()))
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();

		return refreshTokenByJWT;

	}
    
    public void addAuthentication(HttpServletResponse response, SignInAuthenticationToken auth) throws IOException {
        // We generate a token now.
    	logger.error("START");
    	//        response.addHeader(ApiTokenConfig.AUTHORIZATION_HEADER_NAME, ApiTokenConfig.AUTHORIZATION_HEADER_PREFIX + " " + JWT);
    	
    	
    	KBUser user = (KBUser)auth.getDetails();
    	logger.info("accessToken : " + user.getAccessToken());
    	logger.info("refreshToken : " + user.getRefreshToken());
        
    	
    	
    	String accessTokenJwt = accessTokenByJWT(apiTokenConfig, user.getAccessToken(), secret);
    	String refreshTokenJwt = refreshTokenByJWT(apiTokenConfig, user.getRefreshToken(), secret);
    	
    	logger.info("refreshTokenJwt : " + refreshTokenJwt);
    	
    	
    	JWTTokenEntity jwtToken = JWTTokenEntity.builder()
    			.accessToken(accessTokenJwt)
    			.refreshToken(refreshTokenJwt)
    			.build();
    	
    	ServletServerHttpResponse res = new ServletServerHttpResponse(response);
        res.setStatusCode(HttpStatus.ACCEPTED);
        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.getBody().write(new Gson().toJson(jwtToken).toString().getBytes());
    	
		
    }

    public Authentication getAuthentication(HttpServletRequest request) throws AuthenticationException {
        String token = request.getHeader(ApiTokenConfig.AUTHORIZATION_HEADER_NAME);
        if (token == null) {
            return null;
        }
        //remove "Bearer" text
        token = token.replace(ApiTokenConfig.AUTHORIZATION_HEADER_PREFIX, "").trim();

        //Validating the token
        if (token != null && !token.isEmpty()) {
            // parsing the token.`
            Claims claims = null;
            try {
                claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token).getBody();
            } catch (JwtException jwtException) {            	
            	logger.error("**JWT Error :"+ jwtException.getMessage());
            	throw new KBAuthenticationException("400",jwtException.getMessage());
            }
            
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
