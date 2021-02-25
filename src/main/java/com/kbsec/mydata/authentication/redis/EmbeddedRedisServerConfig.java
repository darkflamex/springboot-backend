package com.kbsec.mydata.authentication.redis;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.embedded.RedisServer;

/**
 * Embedded Redis Server Configuration
 * spring.profiles.active = boot or local or test 인 경우 동작한다.
 * @author skyrun
 *
 */
@Profile({"boot", "local", "test"})
@Configuration 
public class EmbeddedRedisServerConfig { 
	
	private RedisServer redisServer; 
	
	public EmbeddedRedisServerConfig(
			@Value("${spring.redis.port}")
			int port) { 
		redisServer = new RedisServer(port); 
	} 
	
	@PostConstruct 
	public void startRedis() throws IOException { 
		redisServer.start(); 
	} 
	
	@PreDestroy 
	public void stopRedis() { 
		redisServer.stop(); 
	} 
}

