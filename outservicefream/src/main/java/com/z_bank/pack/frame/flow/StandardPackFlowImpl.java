package com.z_bank.pack.frame.flow;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.z_bank.pack.frame.container.ZbankBeanContainer;
import com.z_bank.pack.frame.http.IHttpClient;
import com.z_bank.pack.frame.map2Json.IMap2Json;
import com.z_bank.pack.frame.map2Xml.IMap2Xml;
import com.z_bank.pack.frame.packscure.IPackSecure;
import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;

@ZbankBean
public class StandardPackFlowImpl implements IPackFlow{
	public static Logger log = Logger.getLogger(StandardPackFlowImpl.class);
	@Override
	public String parse(Map<String,Object> map,String qudaoName,String serviceid,String logTag) throws Exception {
		try {
			// TODO 获取请求报文转换对应的转换成json的模板名称
			String toReqJsonTempName = qudaoName + "_" + serviceid + "_req";
			log.info(logTag + "----ESB转成Json的模板是：" + toReqJsonTempName);
			// TODO 获取响应报文转换对应的转换成json的模板名称
			String toRespxmlTempName = qudaoName + "_" + serviceid + "_resp";
			log.info(logTag + "----JSON转成ESB XML的模板是：" + toRespxmlTempName);

			IMap2Json map2Json = (IMap2Json)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get(qudaoName + ".map2reqjson.class"));
			String reqJson = map2Json.parse2JsonString(map, toReqJsonTempName);
			log.info(logTag + "----map转换成json之后的结果是：" + reqJson);

			IPackSecure packScure = (IPackSecure)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get(qudaoName + ".packscure.class"));
			Map<String, Object> scureConf = new HashMap<String, Object>();
			scureConf.put("PUBLIC_KEY", SysProperties.get(qudaoName + ".packscure.pubkey"));

			Map<String, String> encryReqMap = packScure.encryptAndSign(reqJson, scureConf, qudaoName);
			log.info(logTag + "----加密之后的结果是：" + encryReqMap);

			Map<String, String> param = null;
			if (!StringUtils.isEmpty(SysProperties.get(qudaoName + ".postdata.outwrapper.name"))) {
				param = new HashMap<String, String>();
				param.put(SysProperties.get(qudaoName + ".postdata.outwrapper.name"), JSON.toJSONString(encryReqMap));
			} else {
				param = encryReqMap;
			}

			IHttpClient httpClient = (IHttpClient)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get(qudaoName + ".http.class"));
			String url = processUrl(qudaoName,serviceid);
			log.info(logTag + "----发送请求到" + url + "请求参数是:" + param);
			String respJsonStr = httpClient.httpPost(url,serviceid,qudaoName, param);// 发送请求到第三方的调用
			log.info(logTag + "----得到的响应是:" + respJsonStr);

			String decryRespStr = packScure.decrytAndSignCheck(respJsonStr, scureConf, qudaoName);
			log.info(logTag + "----解密后的结果是:" + decryRespStr);

			IMap2Xml map2Xml = (IMap2Xml)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get(qudaoName + ".respJson2xml.class"));
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("model", JSONObject.parseObject(decryRespStr));
			log.info(logTag + "----转换成ESB XML的模型数据是:" + model);

			String xmlStr = map2Xml.map2XmlString(model, toRespxmlTempName);
			log.info(logTag + "----响应ESB XML报文是：" + xmlStr);
			return xmlStr;
		} catch (Exception e) {
			log.error(logTag + "----" + e.getMessage(), e);
			throw e;
		}
	}
	
	private static String processUrl(String qudaoName,String serviceid){
		String url = SysProperties.get(qudaoName + ".req.url");
		if(url.contains("{")){
			url =  url.replaceAll("\\{[^}]*\\}", serviceid);
		}
		return url;
	}

}
