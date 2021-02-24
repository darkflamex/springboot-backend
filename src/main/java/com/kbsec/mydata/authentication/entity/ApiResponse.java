package com.kbsec.mydata.authentication.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse {
	private int status;
	private String code;
	private String message;
	private String messageDev;
	private Object data;

}
