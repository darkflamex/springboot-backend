package com.kbsec.mydata.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class TestDomain {
	
	@ApiModelProperty(value="아이디", dataType = "String", required = true)
	private String id;
	
	@ApiModelProperty(value="이름", dataType = "String", required = false)
	private String name;
}
