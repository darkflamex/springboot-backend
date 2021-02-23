package com.kbsec.mydata.tr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class Tkb_00015004_Res {
	


	private TRX_HEADER TRX_HEADER = new TRX_HEADER(); 

	private String isCnt = "";
	private String length = "";
	private String msg = "";
	private String clsf = "";
	private List<Record1> Record1 = new ArrayList<Record1>();

	@Data
	@ToString
	public class Record1 {
		private String dt = "";
		private String opnPrc = "";
		
		public Record1() {}
	}
}