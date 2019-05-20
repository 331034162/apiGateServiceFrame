package com.z_bank.pack.frame.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;
import com.z_bank.pack.frame.utils.ClassScannerUtil;

public class ZbankBeanContainer {

	private static Logger log = Logger.getLogger(ZbankBeanContainer.class);

	public static Map<String, Object> BEAN_CONTAINER = new HashMap<String, Object>();

	public static ZbankBeanContainer instance;

	private ZbankBeanContainer() {
		String scanpkg = SysProperties.get("z_bank.scanner.package");
		if (StringUtils.isEmpty(SysProperties.get("z_bank.scanner.package"))) {
			log.error("z_bank.scanner.package在配置文件中未做任何配置!!");
		}
		try {
			Set<Class<?>> classSet = ClassScannerUtil.scanClasses(scanpkg);

			if (classSet == null) {
				log.error("z_bank.scanner.package在配置文件中配置有误或者未做任何配置!!");
			}
			Iterator<Class<?>> ite = classSet.iterator();
			while (ite.hasNext()) {
				Class<?> clazz = ite.next();
				if (clazz.getAnnotation(ZbankBean.class) != null) {
					BEAN_CONTAINER.put(clazz.getName(), clazz.newInstance());
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	public static ZbankBeanContainer getInstance() {
		if (instance == null) {
			instance = new ZbankBeanContainer();
		}

		return instance;
	}

	public Object getZbankBean(String clasName) {
		Object obj = BEAN_CONTAINER.get(clasName);
		if (obj == null) {
			log.error(clasName + "未找到，原因是" + clasName + "未加@ZbankBean注解，或者该类不在包扫描配置z_bank.scanner.package=" + SysProperties.get("z_bank.scanner.package") + "下!");
		}
		return obj;
	}

}
