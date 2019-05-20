package com.z_bank.pack.frame.utils;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.z_bank.pack.frame.resource.SysProperties;

public class HttpClientUtil {

	private static Log log = LogFactory.getLog(HttpClientUtil.class);

	public static String HTTP_PARAM_TYPE_FORM = "form";
	public static String HTTP_PARAM_TYPE_JSON_STR = "json_str";
	public static String HTTP_PARAM_TYPE_KEYVALUE = "key_value";
	
	 // HTTP内容类型。
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

	public static final String CONTENT_TYPE_TEXT = "text/plain";

    // HTTP内容类型。通过json的方式提交form表单
    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

	/**
	 * 
	 * @param url
	 * @param param
	 * @param paramType 参数发送方式。form、jsonStr、uri(url后加参数的形式)
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, Map<String, String> param, String paramType) throws Exception {
		String respData = "";
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost(url);
			RequestConfig.Builder builder = RequestConfig.custom();
			if (!StringUtils.isEmpty(SysProperties.get("http.connect.request.timeout"))) {
				builder.setConnectionRequestTimeout(Integer.parseInt(SysProperties.get("http.connect.request.timeout")));
			}

			if (!StringUtils.isEmpty(SysProperties.get("http.connect.timeout"))) {
				builder.setConnectTimeout(Integer.parseInt(SysProperties.get("http.connect.timeout")));
			}
			if (!StringUtils.isEmpty(SysProperties.get("http.socket.timeout"))) {
				builder.setSocketTimeout(Integer.parseInt(SysProperties.get("http.socket.timeout")));
			}

			post.setConfig(builder.build());
			HttpEntity requestEntity = getStringEntity(param, paramType);
			if (requestEntity != null) {
				post.setEntity(requestEntity);
			}

			HttpClientBuilder clientBuilder = HttpClientBuilder.create().setConnectionManager(initHttpConnectionManager());
			httpclient = clientBuilder.build();
			
			log.info("正在发送请求:" + url + "，参数：" + param);
			response = httpclient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				respData = EntityUtils.toString(responseEntity, "UTF-8");
			}
			log.info("获取响应成功:" + respData);
		} catch (Exception e) {
			log.error("发送请求失败", e);
			throw e;
		} finally {
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpclient);
		}
		return respData;
	}
	
	/**
	 * 
	 * @param url
	 * @param paramStr
	 * @param paramType 支持json_str、key_value
	 * @return
	 * @throws Exception
	 */
	public static String post(String url, String paramStr, String paramType) throws Exception {
		
		String respData = "";
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		try {
			HttpPost post = new HttpPost(url);
			RequestConfig.Builder builder = RequestConfig.custom();
			if (!StringUtils.isEmpty(SysProperties.get("http.connect.request.timeout"))) {
				builder.setConnectionRequestTimeout(Integer.parseInt(SysProperties.get("http.connect.request.timeout")));
			}

			if (!StringUtils.isEmpty(SysProperties.get("http.connect.timeout"))) {
				builder.setConnectTimeout(Integer.parseInt(SysProperties.get("http.connect.timeout")));
			}
			if (!StringUtils.isEmpty(SysProperties.get("http.socket.timeout"))) {
				builder.setSocketTimeout(Integer.parseInt(SysProperties.get("http.socket.timeout")));
			}

			post.setConfig(builder.build());
			StringEntity stringEntity = new StringEntity(paramStr, Consts.UTF_8);
			if (HTTP_PARAM_TYPE_KEYVALUE.equals(paramType)) {//key1=value1&key2=value2
				stringEntity.setContentType(CONTENT_TYPE_TEXT);
				
			}else if(HTTP_PARAM_TYPE_JSON_STR.equals(paramType)){
				stringEntity.setContentType(CONTENT_TYPE_JSON);
			}
			post.setEntity(stringEntity);

			HttpClientBuilder clientBuilder = HttpClientBuilder.create().setConnectionManager(initHttpConnectionManager());
			httpclient = clientBuilder.build();
			
			log.info("正在发送请求:" + url + "，参数：" + paramStr);
			response = httpclient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				respData = EntityUtils.toString(responseEntity, "UTF-8");
			}
			log.info("获取响应成功:" + respData);
		} catch (Exception e) {
			log.error("发送请求失败", e);
			throw e;
		} finally {
			HttpClientUtils.closeQuietly(response);
			HttpClientUtils.closeQuietly(httpclient);
		}
		return respData;
	}
	
	
	/**
	 * 
	 * @param param
	 * @param paramType
	 * @return
	 */
	private static HttpEntity getStringEntity(Map<String, String> param,String paramType){
		if (HTTP_PARAM_TYPE_FORM.equals(paramType)) {
			if (param != null && !param.isEmpty()) {
				// 创建参数队列
				List<NameValuePair> formParamList = new ArrayList<NameValuePair>();
				Iterator<Entry<String, String>> it = param.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					formParamList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}

				UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParamList, Consts.UTF_8);
				return uefEntity;
			}
		} else if (HTTP_PARAM_TYPE_KEYVALUE.equals(paramType)) {//key1=value1&key2=value2
			if (param != null) {
				Iterator<Entry<String, String>> it = param.entrySet().iterator();
				StringBuilder paramStr = new StringBuilder();
				while (it.hasNext()) {
					Entry<String, String> entry = it.next();
					String key = entry.getKey();
					String value = entry.getValue();
					paramStr.append(key).append("=").append(value == null ? "" : value);
					if (it.hasNext()) {
						paramStr.append("&");
					}
				}

				StringEntity stringEntity = new StringEntity(paramStr.toString(), Consts.UTF_8);
				stringEntity.setContentType(CONTENT_TYPE_TEXT);
				return stringEntity;
			}
		}else if(HTTP_PARAM_TYPE_JSON_STR.equals(paramType)){
			StringEntity stringEntity = new StringEntity(JSON.toJSONString(param), Consts.UTF_8);
			stringEntity.setContentType(CONTENT_TYPE_JSON);
			return stringEntity;
		}
		return null;
	}

	public static HttpClientConnectionManager initHttpConnectionManager() {
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
					return true;
				}

			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] {"TLSv1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslsf).build();
			PoolingHttpClientConnectionManager mg = new PoolingHttpClientConnectionManager(registry);
			if (!StringUtils.isEmpty(SysProperties.get("http.perroute.maxcon"))) {
				mg.setDefaultMaxPerRoute(Integer.parseInt(SysProperties.get("http.perroute.maxcon")));
			}
			if (!StringUtils.isEmpty(SysProperties.get("http.maxtotal"))) {
				mg.setMaxTotal(Integer.parseInt(SysProperties.get("http.maxtotal")));
			}
			return mg;
		} catch (Exception e) {
			log.error("客户端网络初始化异常", e);
		}
		return null;
	}
	
	

	public static void main(String ... str) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("d1", "张三");
		map.put("d2", 18);
		Iterator<String> keyIte = map.keySet().iterator();
		
		
		List<Map<String,Object>> friends = new ArrayList<Map<String,Object>>();
		Map<String,Object> f1 = new HashMap<String,Object>();
		f1.put("name", "王武");
		f1.put("age", 19);
		friends.add(f1);
		map.put("friends", friends);
		System.out.println(JSON.toJSONString(map));
	}

}
