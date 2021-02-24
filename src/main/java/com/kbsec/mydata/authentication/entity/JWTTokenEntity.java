package com.kbsec.mydata.authentication.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTTokenEntity {

	private String accessToken;
	
	@JsonInclude(value = Include.NON_NULL)
	private String refreshToken;
}
