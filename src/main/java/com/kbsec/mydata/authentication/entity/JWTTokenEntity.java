package com.kbsec.mydata.authentication.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kbsec.mydata.authentication.SignInAuthenticationToken;
import com.kbsec.mydata.authentication.token.ApiTokenConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTTokenEntity {

	
	private String accessToken;
	
	@JsonInclude(value = Include.NON_NULL)
	private String refreshToken;
}
