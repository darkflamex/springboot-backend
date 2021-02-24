package com.kbsec.mydata.controller;

import java.util.Map;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kbsec.mydata.config.LogAnnotation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@Api(value = "Auth Controller")
public class AuthController {

	@ApiOperation(value="test", notes = "example")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 500, message = "Internal Server Error")		
	})
	@GetMapping("/authorize")
	@LogAnnotation
	public String authorize(
		@RequestParam Map<String,Object> requestParam
	) {	
		log.info(requestParam.toString());
		return "test";
	}
}
