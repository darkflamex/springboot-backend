package com.kbsec.mydata.tr.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class TrHeader {
	protected String Name;
	protected String Desc;
	protected String Route;
	protected String base21;
	protected String blocktype;
	protected String attytype;
	protected String headtype;
	protected String Journal;
	protected String screenid;
	protected String Mid;
	protected String Processor_info;
	protected String Tr_info;
	protected String Page_num;
	protected String HostExpireTime;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
