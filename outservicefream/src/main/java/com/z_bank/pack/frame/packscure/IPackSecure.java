package com.z_bank.pack.frame.packscure;

import java.util.Map;

public interface IPackSecure {

	/**
	 * 对报文pack进行加密签名
	 * 
	 * @param pack  待加密数据
	 * @param config 加解密配置
	 * @param qudaoName 渠道名称
	 * @return
	 */
	public Map<String, String> encryptAndSign(String pack, Map<String, Object> config, String qudaoName) throws Exception;

	/**
	 * 对pack进行解密验签 
	 * @param pack 待解密数据
	 * @param config 加解密配置
	 * @param qudaoName 渠道名称
	 * @return
	 */
	public String decrytAndSignCheck(String pack, Map<String, Object> config, String qudaoName) throws Exception;

}
