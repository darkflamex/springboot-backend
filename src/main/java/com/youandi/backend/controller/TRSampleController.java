package com.youandi.backend.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youandi.backend.auth.domain.TestDomain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(value = "테스트 Controller")
@RequestMapping("/v1/api")
public class TRSampleController {

	@ApiOperation(value="test", notes = "example")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 500, message = "Internal Server Error")		
	})
	@Secured("USER")
	@GetMapping("/user")
	public String goUser(
			@ApiIgnore  Authentication auth,
			@RequestBody TestDomain test
	) {
		
		System.out.println("!!!" + auth.getAuthorities());
		
		return "test";
	}
}
