package com.fangjia.api.demo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 使用HttpClient访问房价网API接口示例
 * 
 * @author wenyan
 *
 */
public class HttpClientDemo {

	// 请求超时时间
	public static final int CONNECT_TIMEOUT = 10 * 1000;
	
	// 数据读取超时时间
	public static final int SOCKET_TIMEOUT = 10 * 1000;

	
	/**
	 * 创建一个HttpClient
	 * @return
	 */
	public static CloseableHttpClient createHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setSocketTimeout(SOCKET_TIMEOUT)
				.build();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		return httpClient;
	}

	/**
	 * 通过POST方式请求API
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String doHttpPostRequest(String url, Map<String, String> params, String charset) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = createHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (null != params) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				for (String name : params.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(name, params.get(name)));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, charset);
				// 解决中文乱码问题
				entity.setContentEncoding("UTF-8");
				httpPost.setEntity(entity);
			}
			HttpResponse result = httpClient.execute(httpPost);
			/** 请求发送成功，并得到响应 **/
			if (result.getStatusLine().getStatusCode() == 200) {
				/** 读取服务器返回过来的json字符串数据 **/
				return EntityUtils.toString(result.getEntity(), "UTF-8");
			} else {
				// TODO
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 通过GET方式请求API
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String doHttpGetRequest(String url, Map<String, String> params, String charset) {

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
			url = url + "?" + sb.substring(0, sb.length() - 1);
		}
		CloseableHttpClient httpClient = null;
		try {
			httpClient = createHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = httpClient.execute(request);
			/** 请求发送成功，并得到响应 **/
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				// TODO
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	// 房价网token获取接口测试
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
		System.out.println(doHttpPostRequest(url, params, "UTF-8"));
		
		Map<String, String> searchParams = new HashMap<String, String>();
		searchParams.put("city", "上海");
		searchParams.put("keyword", "呼玛");
		searchParams.put("token", "The token obtained from the accessToken api");
		
		String searchUrl = "http://open.fangjia.com/property/search";
		// 使用get方法调用api
		System.out.println(doHttpGetRequest(searchUrl, searchParams, "UTF-8"));
		// 使用POST方法调用api
		System.out.println(doHttpPostRequest(searchUrl, searchParams, "UTF-8"));

	}
}
