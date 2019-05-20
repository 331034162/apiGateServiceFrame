package com.z_bank.pack.frame.http;

import java.util.Map;

public interface IHttpClient {
	
	public String httpPost(String url, String serviceid,String qudaoName,Map<String,String> param) throws Exception;

}
