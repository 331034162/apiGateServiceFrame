package com.z_bank.pack.frame.map2Xml;

import java.util.Map;

public interface IMap2Xml {

	/**
	 * 使用freemarker将model的数据插值到xmlName文档中
	 * 
	 * @param model
	 * @param xmlName
	 * @return
	 */
	public String map2XmlString(Map<String, Object> model, String xmlName) throws Exception;

}
