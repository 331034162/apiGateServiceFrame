package com.z_bank.pack.frame.packscure;

import java.util.HashMap;
import java.util.Map;

import com.z_bank.pack.frame.encrypt.SafeConTrolUtil;
import com.z_bank.pack.frame.resource.ZbankBean;

/**
 * 渤海加密加签及解密解签的调用
 * @author guanlivv
 *
 */

@ZbankBean
public class PackeSecureImpl implements IPackSecure{

	@Override
	public Map<String,String> encryptAndSign(String pack, Map<String, Object> config,String qudaoName)  throws Exception{
		try {
			Map<String,String> returnMap = new HashMap<String,String>();
			return SafeConTrolUtil.EncryptA(pack, qudaoName, returnMap);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String decrytAndSignCheck(String pack, Map<String, Object> config,String qudaoName) throws Exception{
		try {
			return SafeConTrolUtil.DecryptA(pack, qudaoName);
		} catch (Exception e) {
			throw e;
		}
	}

}
