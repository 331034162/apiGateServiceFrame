package com.z_bank.pack.frame.map2Xml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;
import com.z_bank.pack.frame.utils.FreemarkerUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
@ZbankBean
public class Map2XmlImpl implements IMap2Xml{
	
	private static Map<String, Template> TEMP_CACHE = new HashMap<String, Template>();

	/**
	 * 使用freemarker
	 */
	@Override
	public String map2XmlString(Map<String, Object> model, String xmlName) throws Exception {
		if (TEMP_CACHE.get(xmlName) == null) {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
			cfg.setDirectoryForTemplateLoading(new File(SysProperties.get("reponse.map2xml.xml.dir")));// 通过文件路径加载模板
			cfg.setDefaultEncoding("UTF-8");
			Template template = cfg.getTemplate(xmlName + ".xml");
			if ("true".equals(SysProperties.get("template.cached"))) {
				TEMP_CACHE.put(xmlName, template);
			}
			return FreemarkerUtil.parse(model, template);
		}
		return FreemarkerUtil.parse(model, TEMP_CACHE.get(xmlName));
	}

}
