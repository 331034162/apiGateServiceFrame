package com.z_bank.pack.frame.esbparser;

import java.util.Map;

public interface IEsbXml2Map {

	/**
	 * 将xmlStr字符串解析成Map对象
	 * 
	 * @param xmlStr
	 * @return
	 */
	public Map<String, Object> parse(String xmlStr) throws Exception;

}
