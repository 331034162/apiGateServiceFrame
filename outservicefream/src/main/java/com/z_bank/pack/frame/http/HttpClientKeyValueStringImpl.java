package com.z_bank.pack.frame.http;

import java.util.Map;

import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;
import com.z_bank.pack.frame.utils.HttpClientUtil;

@ZbankBean
public class HttpClientKeyValueStringImpl implements IHttpClient {

	@Override
	public String httpPost(String url, String serviceid,String qudaoName, Map<String, String> param) throws Exception {
		String sign=SysProperties.get(qudaoName + ".aeskey.name");
		String crypt=SysProperties.get(qudaoName + ".entrypack.name");
		return HttpClientUtil.post(url, sign.concat("=").concat(param.get(sign)).concat("&").concat(crypt).concat("=").concat(param.get(crypt)), HttpClientUtil.HTTP_PARAM_TYPE_KEYVALUE);
	}

}
