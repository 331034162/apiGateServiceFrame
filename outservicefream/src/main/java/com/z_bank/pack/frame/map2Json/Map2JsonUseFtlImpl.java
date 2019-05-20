package com.z_bank.pack.frame.map2Json;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;
import com.z_bank.pack.frame.utils.FreemarkerUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
@ZbankBean
public class Map2JsonUseFtlImpl implements IMap2Json {

	public static Logger log = Logger.getLogger(Map2JsonUseFtlImpl.class);

	private static Map<String, Template> TEMP_CACHE = new HashMap<String, Template>();

	/**
	 * 将ESB报文处理成json字符串
	 * 
	 * @param confPath
	 * @param esbMap
	 * @return
	 * @throws Exception
	 */
	public String parse2JsonString(Map<String, Object> model, String tempName) throws Exception {

		if (TEMP_CACHE.get(tempName) == null) {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
			cfg.setDirectoryForTemplateLoading(new File(SysProperties.get("request.map2json.ftl.dir")));//通过文件路径加载模板
			cfg.setDefaultEncoding("UTF-8");
			Template template = cfg.getTemplate(tempName + ".xml");
			if ("true".equals(SysProperties.get("template.cached"))) {
				TEMP_CACHE.put(tempName, template);
			}
			return FreemarkerUtil.parse(model, template);
		}
		return FreemarkerUtil.parse(model, TEMP_CACHE.get(tempName));

	}

}
