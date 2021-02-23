package com.kbsec.mydata.tr.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
@Data
public class PCAM4562_Req {
	private String hndlCcd;
	private String rfndDt;
	private Recode1 recode1;
	public class Recode1 {
		
	}
}


