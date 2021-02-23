package com.kbsec.mydata.authentication.filter;

import lombok.Data;

@Data
public class Credentials {
	private String principal;
	private String credentials;
}
