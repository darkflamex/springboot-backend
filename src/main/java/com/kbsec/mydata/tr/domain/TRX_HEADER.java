package com.kbsec.mydata.tr.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TRX_HEADER {

	public String trx_compress = "";
	public String trx_node_id = "";
	private String trx_cont = "";
	private String trx_elapsed_time = "";
	private String trx_rq_id = "";
	private String trx_contkey = "";
	private String trx_time = "";
	private String trx_userflag2 = "";
	private String trx_msgcode = "";
	private String trx_total_len = "";
	private String trx_tr_date_len = "";
	private String trx_error = "";
	private String trx_cipher = "";
	private String trx_userflag1 = "";
	private String trx_msg = "";
	private String trx_globalid = "";
	private String trx_connect_server = "";
	private String trx_serviceid = "";
}
