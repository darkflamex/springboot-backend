package com.kbsec.mydata.tr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Tkb_SEAM0007 extends TrHeader {
	private String gnlAcNo = "";
	private String gdsNo = "";
	private Record1 recode1 = null;
	
	@Data
	public class Record1 {
		private String gnlAcno = "";
		private String encdScrtNo = "";
	}
}
