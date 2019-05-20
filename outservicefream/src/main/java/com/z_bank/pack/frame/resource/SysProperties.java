package com.z_bank.pack.frame.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SysProperties {

	public static Logger log = Logger.getLogger(SysProperties.class);

	private static Properties FLOW_DEFINE_CACHE = new Properties();

	static {
		File file = new File(SysProperties.class.getResource("/flowcfg").getPath());
		if (file != null && file.exists() && file.isDirectory()) {
			File[] propFiles = file.listFiles(new FileFilter() {

				public boolean accept(File file) {
					return file.getName().endsWith(".properties");
				}
			});
			for (int i = 0; i < propFiles.length; i++) {
				BufferedInputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(new FileInputStream(propFiles[i]));
					FLOW_DEFINE_CACHE.load(inputStream);
					inputStream.close();
				} catch (IOException e) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e1) {
							log.error(e);
						}
					}
					log.error(e);
				}
			}
		}

	}

	public static String get(String key) {
		return FLOW_DEFINE_CACHE.getProperty(key);
	}

}
