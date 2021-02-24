package com.kbsec.mydata.authentication.config;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;

import com.google.gson.Gson;

public class DefaultApiResponse {
	
	
	public static void response(HttpServletResponse response, Object jsonBean ) throws IOException {
    	ServletServerHttpResponse res = new ServletServerHttpResponse(response);
        res.setStatusCode(HttpStatus.ACCEPTED);
        res.getServletResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.getBody().write(new Gson().toJson(jsonBean).toString().getBytes());
    	res.close();

	}
}

