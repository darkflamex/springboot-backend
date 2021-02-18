package com.youandi.backend.auth.domain;

import lombok.Data;

@Data
public class LgAccountUser {
	private boolean isLogIn;
	
	private String sid;
	private String userid;
	
	private String status;
}
