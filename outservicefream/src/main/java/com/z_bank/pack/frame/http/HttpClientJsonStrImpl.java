package com.z_bank.pack.frame.http;

import java.util.Map;

import com.z_bank.pack.frame.resource.ZbankBean;
import com.z_bank.pack.frame.utils.HttpClientUtil;

@ZbankBean
public class HttpClientJsonStrImpl implements IHttpClient {

	@Override
	public String httpPost(String url, String serviceid,String qudaoName,Map<String, String> param) throws Exception {
		return HttpClientUtil.post(url, param, HttpClientUtil.HTTP_PARAM_TYPE_JSON_STR);
	}

}
