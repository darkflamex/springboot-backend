package com.kbsec.mydata.tr.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TrTest {
	
	private static final Logger logger = LoggerFactory.getLogger(TrTest.class);
	
	public Map<String,Object> inputToHandler(Object e) {
		
		Map<String, Object> handler = new HashMap<String,Object>();
		
		Field[] declaredFields = e.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			Object value = null;

			// private Field일 경우 접근을 허용한다.
			field.setAccessible(true);

			try {
				// Field Value를 참조한다.
				value = field.get(e);
				handler.put(field.getName(), value);
			} catch (IllegalAccessException ex) {
				// log.info("Reflection Error. {}", e);
			}
		}

		return handler;
	}
	
	public <E> E outputToBean(String str, E e) {
		E out = (E) new Gson().fromJson(str, e.getClass());
		return out;
	}
	
	@SuppressWarnings("serial")
	public <E> List<E> outputList(String str, E e) {
		// E out = (E) new Gson().fromJson(str, new TypeToken<List<E>>().getType());
		// E a = new Gson().fromJson(str, new TypeToken<String>));
		
		List<E> outList = new ArrayList<E>();
		
		JsonElement element = new JsonParser().parse(str);
		
		if(element.isJsonArray()) {
			outList = new Gson().fromJson(str, new TypeToken<List<E>>() {}.getType()); 			
		} else {
			@SuppressWarnings("unchecked")
			E out = (E) new Gson().fromJson(str, e.getClass());
			outList.add(out);
		}
		
		return outList;
	}
		
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		PCAM4562_Req req = PCAM4562_Req.builder().hndlCcd("1").rfndDt("2").build();

		TrTest trTest = new TrTest();
		Map<String,Object> handler = trTest.inputToHandler(req);
		
		logger.error(handler.toString());
		
		Gson gson = new Gson(); 
		
		String json = gson.toJson(handler);
		
		logger.error(json.toString());
		
		PCAM4562_Req res = trTest.outputToBean(json, req);
		
		logger.error(res.toString());
		
		List<PCAM4562_Req> list = trTest.outputList(json, req);
		
		logger.error(list.toString());
		
		/*
		Field[] declaredFields = req.getClass().getDeclaredFields();

		for (Field field : declaredFields) {
			Object value = null;

			// private Field일 경우 접근을 허용한다.
			field.setAccessible(true);

			try {
				// Field Value를 참조한다.
				value = field.get(req);
			} catch (IllegalAccessException e) {
				// log.info("Reflection Error. {}", e);
			}
		}
		*/

	}

}
