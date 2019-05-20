package com.z_bank.pack.frame.encrypt;

import java.util.Map;

import org.apache.log4j.Logger;

import com.z_bank.pack.frame.resource.SysProperties;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class SafeConTrolUtil {

	public static Logger log = Logger.getLogger(SafeConTrolUtil.class);
	
	/**
	 * Base64加密
	 */
	private static BASE64Encoder enc = new BASE64Encoder();
	/**
	 * Base64解密
	 */
	private static BASE64Decoder dec = new BASE64Decoder();
	
	/**
	 * 加密加签方法A
	 * 1、取得参数字符串的utf-8编码字节流
	 * 2、用渤海的公钥对字节流进行RAS加密，得到加密字节流，转base64编码，得到加密字符串
	 * 3、对加密字节流使用合作方私钥计算RSA签名字节流，转base64编码，得到签名字符串
	 * 4、组装参数d1=签名字符串&d2=加密字符串，d1=xxx&d2=xxx，post到指定URl
	 * @param pack 请求报文
	 * @param qudaoName 渠道名称标示
	 */
	public static Map<String,String> EncryptA(String pack,String qudaoName,Map<String,String> returnMap) throws Exception{
		try {
			//对方公钥
			String pub = SysProperties.get(qudaoName + ".packscure.pubkey");
			//我方私钥
			String pri = SysProperties.get(qudaoName + ".packscure.prikey");
			byte[] enData = RSAUtil.encryptByPublicKey(pack.getBytes("UTF-8"), pub);
			//加密之后的参数为
			String enDataStr = enc.encode(enData);
			//加签之后的参数为
			String signStr = RSAUtil.sign(enData, pri);
			String sign=SysProperties.get(qudaoName + ".aeskey.name");
			String crypt=SysProperties.get(qudaoName + ".entrypack.name");
			returnMap.put(sign, signStr);
			returnMap.put(crypt, enDataStr);
			return returnMap;
		} catch (Exception e) {
			log.error("组装req报文异常----" + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 1、根据返回字符串d1=xxx&d2=xxx，解析出签名字符串和加密字符串；
	 * 2、对签名字符串、加密字符串base64解码，得到签名字节流、加密字节流；
	 * 3、根据签名字节流、加密字节流、用渤海银行公钥，验签
	 * 4、使用合作方私钥，对加密字节流解密，得到原始字节流
	 * 5、得到原始字节流的UTF-8编码字符串
	 * @param pack 响应报文
	 * @param qudaoName 渠道名称标示
	 * @return
	 * @throws Exception
	 */
	public static String DecryptA(String pack,String qudaoName) throws Exception{
		try {
			//对方公钥
			String pub = SysProperties.get(qudaoName + ".packscure.pubkey");
			//我方私钥
			String pri = SysProperties.get(qudaoName + ".packscure.prikey");
			String sign=SysProperties.get(qudaoName + ".aeskey.name");
			String crypt=SysProperties.get(qudaoName + ".entrypack.name");
			int index = pack.indexOf("&".concat(crypt).concat("="));
			if(!pack.startsWith(sign.concat("=")) || index < 0) {
				throw new Exception("返回报文格式错误");
			}
			String d1 = pack.substring(3, index);
			String d2 = pack.substring(index + 4);
			byte[] rawD2 = dec.decodeBuffer(d2);
			boolean check = RSAUtil.verify(rawD2, pub, d1);
			if (!check) {
				throw new RuntimeException("数据验签不通过");
			}
			byte[] data = RSAUtil.decryptByPrivateKey(rawD2, pri);
			String deStr2 = new String(data, "UTF-8");
			return deStr2;
		} catch (Exception e) {
			log.error("返回报文解密验签失败----" + e.getMessage(), e);
			throw e;
		}

	}
	
	
	
	
	
}
