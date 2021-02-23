package com.kbsec.mydata.tr.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.ElementOrder.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kbsec.mydata.tr.domain.Tkb_00015004_Res;

public class TrService {
	
	private static final Logger logger = LoggerFactory.getLogger(TrService.class);
	
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
	
	
	public <E> E outputToBean(String str, Class<E> clazz) {
		E out = (E) new Gson().fromJson(str, clazz);
		return out;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		TrService trTest = new TrService();
		
		String filePath = "/Users/skyrun/git/backend/src/main/resources/tr/sample/Tkb_00015004.json";
		String json = FileUtils.readFileToString(new File(filePath), "utf-8");

//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		Tkb_00015004_Res res = mapper.readValue(new File(filePath), Tkb_00015004_Res.class);
//		System.out.println(res.toString());

		Tkb_00015004_Res res = trTest.outputToBean(json, Tkb_00015004_Res.class);
		System.out.println(res.toString());
		
		
		Gson gson = new Gson();
		Tkb_00015004_Res rest1 = gson.fromJson(json, Tkb_00015004_Res.class);
		System.out.println(rest1.toString());
		
		/*
		PCAM4562_Req req = PCAM4562_Req.builder().hndlCcd("1").rfndDt("2").build();

		TrService trTest = new TrService();
		Map<String,Object> handler = trTest.inputToHandler(req);
		
		logger.error(handler.toString());
		
		Gson gson = new Gson(); 
		
		String json = gson.toJson(handler);
		
		logger.error(json.toString());
		
		PCAM4562_Req res = trTest.outputToBean(json, req);
		
		logger.error(res.toString());
		
		List<PCAM4562_Req> list = trTest.outputList(json, req);
		
		logger.error(list.toString());
		*/
		
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
