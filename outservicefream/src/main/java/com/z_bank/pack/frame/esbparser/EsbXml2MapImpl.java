package com.z_bank.pack.frame.esbparser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.z_bank.pack.frame.resource.ZbankBean;

@ZbankBean
public class EsbXml2MapImpl implements IEsbXml2Map{
	

	public Map<String, Object> parse(String esbDocStr) throws Exception {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(new ByteArrayInputStream(esbDocStr.getBytes("UTF-8")));
		// 获取根元素
		Element rootElement = doc.getRootElement();
		List<Element> elements = rootElement.elements();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i).getName();
			List<Element> list = elements.get(i).elements();
			Map<String, Object> temp = new HashMap<String, Object>();
			for (int j = 0; j < list.size(); j++) {
				Element e = list.get(j);
				if ("data".equals(e.getName())) {
					temp.put(e.attributeValue("name"), processDataElement(e));
					map.put(name, temp);
				} else if ("struct".equals(e.getName())) {
					map.put(name, processStructElement(e));
				} else if ("array".equals(e.getName())) {
					map.put(name, processArrayElement(e));
				}
			}
		}
		return map;
	}

	private  Object processDataElement(Element element) {
		List<Element> subList = element.elements();
		for (int j = 0; j < subList.size(); j++) {
			Element subEle = subList.get(j);
			if ("struct".equals(subEle.getName())) {
				return processStructElement(subEle);
			} else if ("field".equals(subEle.getName())) {
				return subEle.getText();
			} else if ("array".equals(subEle.getName())) {
				return processArrayElement(subEle);
			} 
		}
		return null;
	}

	private  Map<String, Object> processStructElement(Element structElement) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Element> list = structElement.elements();
		for (int i = 0; i < list.size(); i++) {
			if ("data".equals(list.get(i).getName())) {
				map.put(list.get(i).attributeValue("name"), processDataElement(list.get(i)));
			} else if ("array".equals(list.get(i).getName())) {
				map.put(list.get(i).attributeValue("name"), processArrayElement(list.get(i)));
			} 

		}
		return map;
	}

	private  List<Object> processArrayElement(Element arrayElement) {
		List<Object> resultList = new ArrayList<Object>();
		List<Element> list = arrayElement.elements();
		for (int i = 0; i < list.size(); i++) {
			Element subEle = list.get(i);
			if ("data".equals(subEle.getName())) {
				resultList.add(processDataElement(subEle));
			} else if ("struct".equals(subEle.getName())) {
				resultList.add(processStructElement(subEle));
			}
		}
		return resultList;
	}

	public static void main(String[] args) throws Exception {
		Map<String, Object> map = new EsbXml2MapImpl().parse("test3.xml");
		System.out.println(map);

	}

}
