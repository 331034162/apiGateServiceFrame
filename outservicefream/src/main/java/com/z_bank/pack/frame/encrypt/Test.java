package com.z_bank.pack.frame.encrypt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.List;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Test {


	public static void main(String[] args) {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void sendPostReq() throws Exception{
		URL url = new URL("http://221.239.93.139:9080/HttpICSrecv/CTHJZBYH1/292002.hp");
		//post参数
		 Map<String,Object> params = new LinkedHashMap<>();
		      params.put("geomInfo", "");
		      params.put("f", "json");
		 
		 //开始访问
		      String postData = "d1=agl8Byok+MWJM+dzEmd+VtTnHqD77P9H+FXiGNzeCvGh5+7789OQfCPelkQzkXlAMt5Iv3f+5+5aHv7hdOFABCa52/NLaXYcrIU51+02JhF/k0c9ht+5Js/B3FHrEhY+EcXas8TNR2/JQHm+F9dcKaZnaAFQGLp6h+h9LleaI80=&d2=LM5E3zojdbe3R+BtcZb/CISzSl8q2V6PP0a0OxCl33cFseBHaBOmqMoOK5FTzsbiMcB/B6/BZojqTaPTnQtIFd8x7AGixTK03pfbqQKn8spQB2yHJEIg5VPtOZG79SeF8dSZ9heY+5xZKZGVa+kBlar0LpZJok/sWmElLAvZ3SdntXrjW3js2RBIf+ymKSTgSpz1Uyruv9zm4LHclreqY82vfWYce0uK973Kddr6SBk6bELp3WfWR5m0F7izD/QIw49iWtRP4qQvb10cPHCSG3oODPxaq2LJedNG7PgtHKc6CzGGlMzhj/ovT8eyReN6Ukc7ceGUzkmuN3/Hg/dAxg==";
//		  StringBuilder postData = new StringBuilder();
//		  for (Map.Entry<String,Object> param : params.entrySet()) {
//		      if (postData.length() != 0) postData.append('&');
//		      postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
//		      postData.append('=');
//		      postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
//		  }
		  byte[] postDataBytes = postData.getBytes("UTF-8");
		 
		  HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		  conn.setRequestMethod("POST");
		  conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		  conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
		  conn.setDoOutput(true);
		  conn.getOutputStream().write(postDataBytes);
		 
		  Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		 
		  StringBuilder sb = new StringBuilder();
		  for (int c; (c = in.read()) >= 0;)
		      sb.append((char)c);
		  String response = sb.toString();
		  System.out.println(response);
		  System.out.println("over");
	}
	
	
	
	/**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            System.out.println(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
	
	
}
