
package com.kbsec.mydata.authentication.redis;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Service("redisService")
@Slf4j
public class RedisService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RedisTemplate< String, Object> template;

	public synchronized List<String> getKeys(final String pattern) {
		template.setHashValueSerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		Set<String> redisKeys = template.keys(pattern);
		// Store the keys in a List
		List<String> keysList = new ArrayList<>();
		Iterator<String> it = redisKeys.iterator();
		while (it.hasNext()) {
			String data = it.next();
			keysList.add(data);
		}
		return keysList;
	}

	public synchronized Object getValue(final String key) {

		template.setHashValueSerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template.opsForValue().get(key);
	}

	@SuppressWarnings("unchecked")
	public synchronized Object getValue(final String key, @SuppressWarnings("rawtypes") Class clazz) {
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

		Object obj = template.opsForValue().get(key);
		return mapper.convertValue(obj, clazz);
	}

	public void setValue(final String key, final Object value) {
		setValue(key, value, TimeUnit.HOURS, 5, false);
	}

	public void setValue(final String key, final Object value, boolean marshal) {
		if (marshal) {
			template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
			template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		} else {
			template.setHashValueSerializer(new StringRedisSerializer());
			template.setValueSerializer(new StringRedisSerializer());
		}
		template.opsForValue().set(key, value);
		// set a expire for a message
		template.expire(key, 5, TimeUnit.MINUTES);
	}

	public void setValue(final String key, final Object value, TimeUnit unit, long timeout) {
		setValue(key, value, unit, timeout, false);
	}

	public void setValue(final String key, final Object value, TimeUnit unit, long timeout, boolean marshal) {
		if (marshal) {
			template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
			template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		} else {
			template.setHashValueSerializer(new StringRedisSerializer());
			template.setValueSerializer(new StringRedisSerializer());
		}
		template.opsForValue().set(key, value);
		// set a expire for a message
		template.expire(key, timeout, unit);
	}

	public boolean delete(final String key) {
		template.opsForList();

		return template.delete(key);
	}

	public boolean hasKey(final String key) {
		return template.hasKey(key);
	}


	public <E> void put(final String key, final E value, TimeUnit unit, Long timeout ) {
	
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(value.getClass()));
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(value.getClass()));
		template.opsForValue().set(key, value);

		if(timeout == null) {
			timeout = (long)5;
		}

		template.expire(key, timeout, unit);
	}


	public void put(RedisEntity redisEntity) {
		this.put(redisEntity.getKey(), redisEntity.getValue(), redisEntity.getUnit(), redisEntity.getTimeout());
	}


	public <E> E get(final String key, Class<E> clazz) {
		
	
		
		template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(clazz));
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(clazz));
		
		if(!template.hasKey(key)) {
			return null;
		} else {
			String jsonResult = (String) template.opsForValue().get(key);
			if(StringUtils.isBlank(jsonResult)) {
				return null;
			} else {
				try {
					E obj = mapper.readValue(jsonResult, clazz);
					return obj;
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					return null;
				}
			}
		}
	}


	public void add(List<RedisEntity> list, LocalDateTime fromDateTime) {

		
		for(RedisEntity element : list) {
			
			log.info("element >> " + element.toString());
			
			template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
			template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
			
			
			template.opsForValue().set(element.getKey(), element.getValue());	
			long amount = element.getUnit().toSeconds(element.getTimeout());
			LocalDateTime expireDateTime = fromDateTime.plusSeconds(amount);
			Date dExpireDateTime = Date.from( expireDateTime.atZone( ZoneId.systemDefault()).toInstant());
			template.expireAt(element.getKey(), dExpireDateTime);
		}
	}

}
