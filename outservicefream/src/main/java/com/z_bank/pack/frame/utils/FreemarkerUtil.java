package com.z_bank.pack.frame.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import com.z_bank.pack.frame.map2Json.Map2JsonUseXmlImpl;

import freemarker.template.Template;

public class FreemarkerUtil {

	private static Logger log = Logger.getLogger(Map2JsonUseXmlImpl.class);

	public static String parse(Map<String, Object> model, Template template) throws Exception {

		StringWriter out = new StringWriter();
		try {
			template.process(model, out);
			out.flush();
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				log.error(e);
			}
		}
		return out.getBuffer().toString();

	}
	
}
