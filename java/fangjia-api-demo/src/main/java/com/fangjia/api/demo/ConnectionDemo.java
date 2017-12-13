package com.fangjia.api.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 不使用第三方库调用API的示例
 * @author wenyan
 *
 */
public class ConnectionDemo {

	// HTTP内容类型。相当于form表单的形式，提交参数
	public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";
	// 请求超时时间
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	// 将读超时时间
	public static final int READ_TIMEOUT = 10 * 1000;
	
	public static final int HTTP_STATUS_OK = 200;
	
	
	
	/**
	 * 通过GET方式请求API
	 * 
	 * @param uri
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String doHttpGetRequest(String uri, Map<String, String> params, String charset) {

		StringBuilder sb = new StringBuilder();
		if (null != params && !params.isEmpty()) {
			for (String paramName : params.keySet()) {
				String val = params.get(paramName);
				String encVal;
				try {
					encVal = URLEncoder.encode(val, charset);
					sb.append(paramName).append("=").append(encVal).append("&");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}
			uri = uri + "?" + sb.substring(0, sb.length() - 1);
		}
		URL url;
		HttpURLConnection httpURLConnection;
		try {
			url = new URL(uri);
			// 打开连接
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置连接超时时间（以毫秒为单位）
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
			// 设置读取数据超时时间（以毫秒为单位）。
			httpURLConnection.setReadTimeout(READ_TIMEOUT);
			// 请求不能使用缓存
			httpURLConnection.setUseCaches(false);
			// 设置字符编码
			httpURLConnection.setRequestProperty("Accept-Charset", charset);
			// 设置内容类型
			httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE_FORM_URL);
			// 设定请求的方法，默认是GET
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			if(HTTP_STATUS_OK == httpURLConnection.getResponseCode()){
				InputStream is = httpURLConnection.getInputStream();
				return readFormInputStream(is, charset);
			}else{
				//TODO
			}
		} catch (MalformedURLException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 通过POST方式请求API
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String doHttpPostRequest(String uri, Map<String, String> params, String charset) {
		
		URL url;
		HttpURLConnection httpURLConnection = null;
		try {
			url = new URL(uri);
			// 连接类的父类，抽象类
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			// 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
        	
			// 设置一个指定的超时值（以毫秒为单位）
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
			// 将读超时设置为指定的超时，以毫秒为单位。
			httpURLConnection.setReadTimeout(READ_TIMEOUT);
			// 设置字符编码
			httpURLConnection.setRequestProperty("Accept-Charset", charset);
			// 设置内容类型
			httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE_FORM_URL);
			 // 获取URLConnection对象对应的输出流
            if(params != null && !params.isEmpty()){
            	PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            	List<String> keys = new ArrayList<String>(params.keySet());
            	StringBuilder sb = new StringBuilder();
            	for(int i=0; i < keys.size(); i++){
            		sb.append(keys.get(i)).append("=").append(params.get(keys.get(i)));
            		if(i != keys.size() -1){
            			sb.append("&");
            		}
            	}
                // 发送请求参数
                printWriter.write(sb.toString());
                printWriter.flush();
            }
			if(HTTP_STATUS_OK == httpURLConnection.getResponseCode()){
				InputStream is = httpURLConnection.getInputStream();
				return readFormInputStream(is, charset);
			}else{
				//TODO
			}
		} catch (MalformedURLException e) {
			// TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 从流中读取内容
	 * @param inputStream
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	static String readFormInputStream(InputStream inputStream, String charset) throws IOException{
        ByteArrayOutputStream baos;
        try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while (-1 != (len = inputStream.read(buffer))) {
				baos.write(buffer, 0, len);
				baos.flush();
			}
			return baos.toString(charset);
		} finally {
			if (null != inputStream) {
				inputStream.close();
			} 
		}
		
	}

	
	//房价网token获取接口测试	
	public static void main(String[] args) {
		
		// api url
		String url = "http://open.fangjia.com/accessToken";
		// 您的用户名
		String username = "your username";
		// 房价网提供给您密码
		String password = "your password";
		// 房价网提供给您的appKey
		String appKey = "your appKey";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", username);
		params.put("password", password);
		params.put("appKey", appKey);

		// 使用get方法调用api
		System.out.println(doHttpGetRequest(url, params, "UTF-8"));
		// 使用POST方法调用api
		System.out.println(doHttpPostRequest(url, params, "UTF-8"));
		
		String searchUrl = "http://open.fangjia.com/property/search";
		
		Map<String, String> searchParams = new HashMap<String, String>();
		searchParams.put("city", "上海");
		searchParams.put("keyword", "呼玛");
		searchParams.put("token", "The token obtained from the accessToken api");
		
		// 使用get方法调用api
		System.out.println(doHttpGetRequest(searchUrl, searchParams, "UTF-8"));
		// 使用POST方法调用api
		System.out.println(doHttpPostRequest(searchUrl, searchParams, "UTF-8"));
	}

}
