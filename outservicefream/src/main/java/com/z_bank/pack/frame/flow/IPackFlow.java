package com.z_bank.pack.frame.flow;

import java.util.Map;

public interface IPackFlow {
	
	public String parse(Map<String,Object> esbMap,String qudaoName,String serviceid,String logTag) throws Exception;

}
