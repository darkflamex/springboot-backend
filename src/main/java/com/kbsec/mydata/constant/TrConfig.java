package com.kbsec.mydata.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Builder;
import lombok.Data;

//@Configuration
//@ConfigurationProperties(prefix="tr.path")
@Data
@Builder
public class TrConfig {
	
	public static final String TR_XML_FILE_PREFIX = "Tkb_";
	public static final String OUTPUT_BEAN_SUFFIX = "_Res";
	public static final String INPUT_BEAN_SUFFIX = "_Req";
	
	public static final String OUTPUT_NODE_NAME = "Output";
	public static final String INPUT_NODE_NAME = "Input";
	
	private String xmlFilePath;
	private String requestBeanPackage;
	private String responseBeanPackage;
}


