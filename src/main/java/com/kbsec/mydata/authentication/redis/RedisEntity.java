package com.kbsec.mydata.authentication.redis;

import java.util.concurrent.TimeUnit;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RedisEntity {
	private String key;
	private Object value;
	private TimeUnit unit; 
	private Long timeout; 
}
