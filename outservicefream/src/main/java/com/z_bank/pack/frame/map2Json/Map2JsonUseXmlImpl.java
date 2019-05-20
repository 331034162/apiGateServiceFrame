package com.z_bank.pack.frame.map2Json;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.z_bank.pack.frame.resource.SysProperties;
import com.z_bank.pack.frame.resource.ZbankBean;
@ZbankBean
public class Map2JsonUseXmlImpl implements IMap2Json {

	public static Logger log = Logger.getLogger(Map2JsonUseXmlImpl.class);

	private static Map<String, Document> DOC_CACHE = new HashMap<String, Document>();
	

	/**
	 * 将ESB报文处理成json字符串
	 * 
	 * @param confPath
	 * @param esbMap
	 * @return
	 * @throws Exception
	 */
	public String parse2JsonString(Map<String, Object> model, String tempName) throws Exception {
		return JSONObject.toJSONString(parse2Map(tempName, model));
	}

	/**
	 * 将model报文处理成confPath定义的map对象
	 * 
	 * @param confPath
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> parse2Map(String tempName, Map<String, Object> model) throws Exception {
		Document doc = null;
		if (DOC_CACHE.get(tempName) != null) {
			doc = DOC_CACHE.get(tempName);
		} else {
			SAXReader saxReader = new SAXReader();
			// 获得document文档对象
			doc = saxReader.read(new File(SysProperties.get("request.map2json.xml.dir")+tempName+".xml"));
			if ("true".equals(SysProperties.get("template.cached"))) {
				DOC_CACHE.put(tempName, doc);
			}
		}

		Element rootElement = doc.getRootElement();
		if ("map".equals(rootElement.attributeValue("type"))) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (!StringUtils.isEmpty(rootElement.attributeValue("name"))) {
				map.put(rootElement.attributeValue("name"), processMapElement(rootElement.element("map"), model, model));
			} else {
				map = processMapElement(rootElement.element("map"), model, model);
			}
			return map;
		} else {
			log.error("不支持的结果类型:" + rootElement.attributeValue("type") + "!!!!");
			throw new Exception("不支持的结果类型:" + rootElement.attributeValue("type") + "!!!!");
		}
	}

	/**
	 * 处理map标签
	 * 
	 * @param ele
	 * @param searchMap
	 * @param esbMap
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> processMapElement(Element ele, Map<String, Object> searchMap, Map<String, Object> esbMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Element> keyEles = ele.elements("key");
		if (keyEles == null && keyEles.size() == 0) {
			log.error(ele.getPath() + "map元素必须定义子元素<key>！！");
			throw new Exception(ele.getPath() + "map元素必须定义子元素<key>！！");
		}
		for (int i = 0; i < keyEles.size(); i++) {
			Element e = keyEles.get(i);
			String name = e.attributeValue("name");
			if (StringUtils.isEmpty(name)) {
				log.error(e.getPath() + " key元素name属性不能为空！！");
				throw new Exception(e.getPath() + " key元素name属性不能为空！！");
			}
			resultMap.put(name, processKeyElement(e, searchMap, esbMap));
		}
		return resultMap;
	}

	/**
	 * 处理array标签
	 * 
	 * @param ele
	 * @param searchList
	 * @param esbMap
	 * @return
	 * @throws Exception
	 */
	private static List<Object> processArrayElement(Element ele, List<Object> searchList, Map<String, Object> esbMap) throws Exception {
		List<Object> rl = new ArrayList<Object>();
		Element eleEle = ele.element("element");
		if (eleEle == null) {
			log.error(ele.getPath() + "array元素必须定义子元素<element>");
			throw new Exception(ele.getPath() + "array元素必须定义子元素<element>");
		}
		/**
		 * && searchList.size() > 0 added 20190422
		 */
		if (searchList != null && searchList.size() > 0) {
			if ("map".equals(eleEle.attributeValue("type"))) {
				Element mapEle = eleEle.element("map");
				if (mapEle != null) {
					for (int i = 0; i < searchList.size(); i++) {
						Map<String, Object> temp = (Map<String, Object>)searchList.get(i);
						rl.add(processMapElement(mapEle, temp, esbMap));
					}
				} else {
					log.error(eleEle.getPath() + "element元素必须定义子元素<map>");
					throw new Exception(eleEle.getPath() + "element元素必须定义子元素<map>");
				}
			} else {
				log.info(eleEle.getPath() + "element元素type！=map，" + ele.getParent().attributeValue("value") + "的值将直接赋给" + ele.getParent().attributeValue("name") + "["
						+ ele.getParent().getPath() + "]" + searchList);
				rl.add(searchList);
			}
		}
		return rl;
	}

	/**
	 * 处理key 标签
	 * 
	 * @param ele
	 * @param searchMap
	 * @param esbMap
	 * @return
	 * @throws Exception
	 */
	private static Object processKeyElement(Element ele, Map<String, Object> searchMap, Map<String, Object> esbMap) throws Exception {
		/**
		 * added 20190422
		 */
		if (searchMap == null) {
			return null;
		}

		String keyValue = ele.attributeValue("value");
		String valType = ele.attributeValue("type");
		if (StringUtils.isEmpty(keyValue)) {
			log.error(ele.getPath() + "元素value属性不能为空！！");
			throw new Exception(ele.getPath() + "元素value属性不能为空！！");
		}
		if (StringUtils.isEmpty(valType)) {
			log.error(ele.getPath() + "key 元素type属性不能为空！！");
			throw new Exception(ele.getPath() + "key 元素type属性不能为空！！");
		}
		/**
		 * 如果以$开头，则需要从searchMap中取值，即数组元素的map，否则，就从esbMap取值
		 */
		Map<String, Object> tempSearchMap = keyValue.startsWith("$") ? searchMap : esbMap;
		String[] levels = keyValue.split("[.]");
		Object obj = null;

		StringBuilder valuepath = new StringBuilder();// 用于记录已经处理的值路径，便于在解析出错时查找
		for (int k = 0; k < levels.length; k++) {
			String key = levels[k];
			// 处理value中包含#的情况 $BANK_ID#0#1 或者 BANK_ID#0#1
			if (key.contains("#")) {
				String[] subKeys = null;
				if (!key.startsWith("$")) {
					subKeys = key.split("#");
				} else {
					subKeys = key.substring(1).split("#");
				}
				obj = tempSearchMap.get(subKeys[0]);
				valuepath.append(subKeys[0]);
				for (int j = 1; j < subKeys.length; j++) {
					if (obj != null && isObjectOfList(obj)) {
						obj = ((List<Object>)obj).get(Integer.parseInt(subKeys[j]));
						valuepath.append("#").append(subKeys[j]);
					} else if (obj != null && !isObjectOfList(obj)) {// added 20190423
						log.error(ele.getPath() + "的value=[" + keyValue + "]." + valuepath.toString() + "的值必须是list！！而当前是" + obj.getClass().getName() + "类型." + valuepath.toString()
								+ "=" + obj);
						throw new Exception(ele.getPath() + "的value=[" + keyValue + "]." + valuepath.toString() + "的值必须是list！！而当前是" + obj.getClass().getName() + "类型"
								+ valuepath.toString() + "=" + obj);
					}
				}
			} else {
				if (!key.startsWith("$")) {
					valuepath.append(key);
					obj = tempSearchMap.get(key);
				} else {
					valuepath.append(key);
					obj = tempSearchMap.get(key.substring(1));
				}
			}

			if (obj == null) {
				return null;
			}

			if (k != levels.length - 1) {
				/**
				 * 如果当前的节点值value还没有处理完，但是要搜索的对象已经不是map了，抛出异常
				 */
				if (!isObjectOfMap(obj)) {
					log.error(ele.getPath() + "的value=[" + keyValue + "]." + valuepath.toString() + "的值必须是map！！而当前是" + obj.getClass().getName() + "类型." + valuepath.toString() + "="
							+ obj);
					throw new Exception(ele.getPath() + "的value=[" + keyValue + "]." + valuepath.toString() + "的值必须是map！！而当前是" + obj.getClass().getName() + "类型"
							+ valuepath.toString() + "=" + obj);

				}
				tempSearchMap = (Map<String, Object>)obj;
			}

		}

		if ("map".equals(valType)) {
			if (!"false".equals(ele.attributeValue("deep-parse"))) {
				Element mapele = ele.element("map");
				if (mapele == null) {
					log.error(ele.getPath() + "元素的属性deep-parse=" + ele.attributeValue("deep-parse") + "。必须定义子元素<map>");
					throw new Exception(ele.getPath() + "元素的属性deep-parse=" + ele.attributeValue("deep-parse") + "。必须定义子元素<map>");
				}
				return processMapElement(mapele, (Map<String, Object>)obj, esbMap);
			} else {
				return obj;
			}
		} else if ("array".equals(valType)) {
			if (!"false".equals(ele.attributeValue("deep-parse"))) {
				Element arrayele = ele.element("array");
				if (arrayele == null) {
					log.error(ele.getPath() + "元素的属性deep-parse=" + ele.attributeValue("deep-parse") + "。必须定义子元素<array>");
					throw new Exception(ele.getPath() + "元素的属性deep-parse=" + ele.attributeValue("deep-parse") + "。必须定义子元素<array>");
				}
				return processArrayElement(arrayele, (List<Object>)obj, esbMap);
			} else {
				return obj;
			}
		}
		return valueType(obj, valType);
	}

	/**
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	public static Object valueType(Object value, String type) {
		if (value != null && !"".equals(value)) {
			if ("string".equals(type)) {
				return value.toString();
			}
			if ("int".equals(type)) {
				return Integer.parseInt(value.toString());
			}
			if ("long".equals(type)) {
				return Long.parseLong(value.toString());
			}
			if ("float".equals(type)) {
				return Float.parseFloat(value.toString());
			}
			if ("double".equals(type)) {
				return Double.parseDouble(value.toString());
			}
			if ("bigDecimal".equals(type)) {
				return new BigDecimal(value.toString());
			}
			if ("bigInteger".equals(type)) {
				return new BigInteger(value.toString());
			}
		}
		return "".equals(value) && !"string".equals(type) ? null : value;
	}

	private static boolean isObjectOfMap(Object obj) {
		return Arrays.asList(obj.getClass().getInterfaces()).contains(Map.class);
	}

	private static boolean isObjectOfList(Object obj) {
		return Arrays.asList(obj.getClass().getInterfaces()).contains(List.class);
	}

}
