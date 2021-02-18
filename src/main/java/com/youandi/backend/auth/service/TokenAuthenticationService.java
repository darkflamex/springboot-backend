package com.youandi.backend.auth.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.token.Sha512DigestUtils;

import com.youandi.backend.auth.common.ApiTokenConfig;
import com.youandi.backend.auth.domain.AuthenticationTokenImpl;
import com.youandi.backend.auth.domain.KBUser;
import com.youandi.backend.auth.exception.CustomJWTException;
import com.youandi.backend.auth.exception.KBAuthenticationException;
import com.youandi.backend.auth.redis.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

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

    public void addAuthentication(HttpServletResponse response, AuthenticationTokenImpl auth) {
        // We generate a token now.
    	logger.error("START");
    	
    	Map<String, Object> claims = new HashMap<>();
        claims.put("username", auth.getPrincipal());
        claims.put("hash", auth.getHash());
        String JWT = Jwts.builder()
                .setSubject(auth.getPrincipal().toString())
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + apiTokenConfig.getExpired()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        response.addHeader(ApiTokenConfig.AUTHORIZATION_HEADER_NAME, ApiTokenConfig.AUTHORIZATION_HEADER_PREFIX + " " + JWT);
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
            	
            	logger.error("**Error :"+ jwtException.getMessage());
            	// throw new CustomJWTException(jwtException.getMessage(), jwtException);
            	throw new KBAuthenticationException("400",jwtException.getMessage());
            	
//            	throw new BadCredentialsException("jwt");
            }
            
            //Valid token and now checking to see if the token is actally expired or alive by quering in redis.
            if (claims != null && claims.containsKey("username")) {
                String username = claims.get("username").toString();
                String hash = claims.get("hash").toString();
                KBUser user = (KBUser) service.getValue(String.format("%s:%s", username,hash), KBUser.class);
                if (user != null) {
                    AuthenticationTokenImpl auth = new AuthenticationTokenImpl(user.getUsername(), AuthorityUtils.createAuthorityList("USER"));
                    auth.setDetails(user);
                    auth.authenticate();
                    return auth;
                } else {
                    return new UsernamePasswordAuthenticationToken(null, null);
                }

            }
        }
        return null;
    }
}
