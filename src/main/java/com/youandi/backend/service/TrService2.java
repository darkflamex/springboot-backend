package com.youandi.backend.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.youandi.backend.auth.service.TokenAuthenticationService;
import com.youandi.backend.tr.domain.TRX_HEADER;
import com.youandi.backend.tr.domain.Tkb_00015004_Req;
import com.youandi.backend.tr.domain.Tkb_00015004_Res;

import net.sf.json.JSONObject;

public class TrService2<I,O> {
	
	private static final Logger logger = LoggerFactory.getLogger(TrService2.class);
	
	private Gson gson;
	
	public O request(I i, Class<O> clazz) {
	
		
		Map<String,Object> map = inputToHandler(i);
		
		JSONObject result = null;
		// TODO 
		// JKASS_v4.jar 
		// request ...
		// return JSONObject 
		
		try {
			String filePath = "/Users/skyrun/git/backend/src/main/resources/tr/sample/Tkb_00015004.json";
			String json = FileUtils.readFileToString(new File(filePath), "utf-8");
		
			result = JSONObject.fromObject(json);	
			gson = new Gson();
			
			O o = (O)gson.fromJson(result.toString(), clazz);
			return o;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	private Map<String,Object> inputToHandler(Object e) {
		
		Map<String, Object> handler = new HashMap<String,Object>();
		
		Field[] declaredFields = e.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			Object value = null;
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

		Tkb_00015004_Req tkb_00015004_Req = Tkb_00015004_Req.builder()
				.length("33")
				.build();
		
		TrService2<Tkb_00015004_Req, Tkb_00015004_Res> trService = new TrService2<Tkb_00015004_Req, Tkb_00015004_Res>();
		Tkb_00015004_Res res = trService.request(tkb_00015004_Req, Tkb_00015004_Res.class);
		
		System.out.println(res.toString());
	}

}
