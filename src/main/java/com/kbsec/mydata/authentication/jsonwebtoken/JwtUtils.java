package com.kbsec.mydata.authentication.jsonwebtoken;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import com.kbsec.mydata.authentication.exception.KBAuthenticationException;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.RequiredTypeException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtUtils {

	
    
//  try {
//      claims = Jwts.parser()
//              .setSigningKey(secret)
//              .parseClaimsJws(token).getBody();
//  } catch (JwtException jwtException) {            	
//  	logger.error("**JWT Error :"+ jwtException.getMessage());
//  	throw new KBAuthenticationException("400",jwtException.getMessage());
//  }
	
	public static Claims parse(String jws, String secret) {
		Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jws).getBody();
        } catch (MalformedJwtException malformedJwtException) { 	
        	// 구조적인 문제가 있는 JWT  
        	throw new KBAuthenticationException("400", malformedJwtException);
        }  catch (PrematureJwtException prematureJwtException) {
        	// 접근이 허용되기 전인 JWT가 수신된 경우
        	throw new KBAuthenticationException("400", prematureJwtException);
            
        } catch(ExpiredJwtException expiredJwtException) {
        	// 유효 기간이 지난 JWT를 수신한 경우
        	throw new KBAuthenticationException("400", expiredJwtException);
            
        } catch (ClaimJwtException claimJwtException) {
            // JWT 권한claim 검사가 실패했을 때
        	throw new KBAuthenticationException("400", claimJwtException);
            
        } catch (SignatureException signatureException) {
        	// 시그너처 연산이 실패하였거나, JWT의 시그너처 검증이 실패한 경우
        	throw new KBAuthenticationException("400", signatureException);
            
        } catch (UnsupportedJwtException unsupportedJwtException) {
        	// 수신한 JWT의 형식이 애플리케이션에서 원하는 형식과 맞지 않는 경우.
        	// 예를 들어, 암호화된 JWT를 사용하는 애프리케이션에 암호화되지 않은 JWT가 전달되는 경우에 이 예외가 발생합니다.
        	throw new KBAuthenticationException("400", unsupportedJwtException);
            
        } catch (JwtException jwtException) {
        	// catch 외의 Jwt 관련 Exception
        	throw new KBAuthenticationException("400", jwtException);
            
        } catch (Exception exception) {
        	// jwt 외의 Exception 
        	throw new KBAuthenticationException("400", exception);
            
        }
        return claims;
	}
        
   public static String build(String subject, 
		   Map<String,Object> claims, 
		   LocalDateTime standardDateTime,
		   long timeout,
		   String secret) {
	  
	   LocalDateTime expireDateTime = standardDateTime.plusSeconds(timeout);
	   Date dExpireDateTime = Date.from( expireDateTime.atZone( ZoneId.systemDefault()).toInstant());
		 
	   String jwt = Jwts.builder()
				.setSubject(subject)
				.setClaims(claims)
				.setExpiration(dExpireDateTime)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	   return jwt;
	}
}
