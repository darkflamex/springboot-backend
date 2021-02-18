package com.youandi.backend.auth.filter;

import lombok.Data;

@Data
public class Credentials {
	private String principal;
	private String credentials;
}
