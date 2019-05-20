package com.z_bank.pack.frame.map2Json;

import java.util.Map;

public interface IMap2Json {
	
	
	/**
	 * 将model数据转换成json字符串
	 * @param model  数据
	 * @param tempName  json模板描述名称
	 * @param parseType 解析类型  1、按照xml解析  2、
	 * @return
	 */
	public String parse2JsonString(Map<String,Object> model,String tempName) throws Exception;
	

}
