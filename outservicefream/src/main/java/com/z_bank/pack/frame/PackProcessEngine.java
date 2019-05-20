package com.z_bank.pack.frame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.z_bank.pack.frame.container.ZbankBeanContainer;
import com.z_bank.pack.frame.esbparser.IEsbXml2Map;
import com.z_bank.pack.frame.flow.IPackFlow;
import com.z_bank.pack.frame.resource.SysProperties;

/**
 * 报文处理引擎
 * 
 * @author zhanglulu
 *
 */
public class PackProcessEngine {

	public static Logger log = Logger.getLogger(PackProcessEngine.class);

	/**
	 * 处理接收到的esb报文
	 * 
	 * @param pack esb报文
	 * @param qudaoName 渠道名称代号
	 * @return
	 * @throws Exception
	 */
	public static String packProcess(String pack) throws Exception {
		String logTag = UUID.randomUUID().toString();
		log.info(logTag + "----begin----");
		log.info(logTag + "----ESB XML报文是：" + pack);
		long begin = System.currentTimeMillis();
		try {

			IEsbXml2Map esbXml2Map = (IEsbXml2Map)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get("esbxml2map.engine.class"));
			Map<String, Object> map = esbXml2Map.parse(pack);
			log.info(logTag + "----ESB XML转换成map之后的结果是：" + map);

			String qudaoName = ((Map)((Map)map.get("sys-header")).get("SYS_HEAD")).get("MESSAGE_TYPE").toString();
			String serviceid = ((Map)((Map)map.get("sys-header")).get("SYS_HEAD")).get("MESSAGE_CODE").toString();
			log.info(logTag + "渠道名称是：" + qudaoName + "，接口名称是：" + serviceid);
			IPackFlow packflow = (IPackFlow)ZbankBeanContainer.getInstance().getZbankBean(SysProperties.get(qudaoName + ".packeflow.class"));
			String resultString = packflow.parse(map, qudaoName, serviceid, logTag);
			log.info(logTag + "----耗時：" + (System.currentTimeMillis() - begin) + "毫秒");
			log.info(logTag + "----end----");
			return resultString;
		} catch (Exception e) {
			log.error(logTag + "----" + e.getMessage(), e);
			log.info(logTag + "----耗時：" + (System.currentTimeMillis() - begin) + "毫秒");
			log.info(logTag + "----end----");
			throw e;
		}

	}

	public static void main(String ... arsgs) throws Exception {
		File file = new File("D:\\testtemp\\bohaibank_292001_esb.xml");
		BufferedReader bf = new BufferedReader(new FileReader(file));
		String str = bf.readLine();
		StringBuilder sb = new StringBuilder();
		while (str != null) {
			sb.append(str);
			str = bf.readLine();
		}

		System.out.println(PackProcessEngine.packProcess(sb.toString()));

	}
}
