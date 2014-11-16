package com.sytm.netcore;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.sytm.common.Constant;

/***
 * 网络请求类
 * 
 * @author wyq
 * 
 */
public class Network {

	public static String TAG = "Network";

	

	/**
	 * 服务端平台请求
	 * 
	 * @param serverurl
	 * @param scobj
	 * @return ServiceResult
	 */
	public static ServiceResult postDataService(ServiceContent scobj) {
		return postDataService(Constant.Service_Url, scobj);
	}

	/**
	 * 服务端平台请求
	 * 
	 * @param serverurl
	 * @param scobj
	 * @return ServiceResult
	 */
	@SuppressWarnings("deprecation")
	public static ServiceResult postDataService(String serverurl,
			ServiceContent scobj) {
		String result = "";
		int httpReturnCode;
		ServiceResult srobj = new ServiceResult();
		try {
			HttpPost request = new HttpPost(serverurl); // 根据内容来源地址创建一个Http请求
			Log.i("Http请求", serverurl);
			/*request.setHeader("Content-Type",
					"application/x-www-form-urlencoded");*/
			request.setHeader("User-Agent", "tmclient");
			// multipart方式
			List<MultiStringData> listString = scobj.getStringparts();
			List<MultiFileData> listFile = scobj.getFileparts();
			if (scobj.isMultipost()) {
//				MultipartEntity meb = new MultipartEntity(HttpMultipartMode.RFC6532, null, Charset.forName(HTTP.UTF_8));
				MultipartEntityBuilder meb = MultipartEntityBuilder.create();
				meb.setMode(HttpMultipartMode.STRICT);
		            // 设置请求的编码格式
//				meb.setCharset(Charset.forName(HTTP.UTF_8));
				if (listString.size() > 0) {
					for (MultiStringData multiStringData : listString) {
						meb.addPart(multiStringData.getKey(),multiStringData.getValue());
//						meb.addTextBody(multiStringData.getKey(), multiStringData.getValue());
					}
				}
				if (listFile.size() > 0) {
					for (MultiFileData multiFileData : listFile) {
						meb.addPart(multiFileData.getKey(),
								multiFileData.getValue());
//						meb.addBinaryBody(multiFileData.getKey(), multiFileData.getValue());

					}
				}
				meb.addPart("ClassName", new StringBody(scobj.getClassname()));
				meb.addPart("MethodName", new StringBody(scobj.getMethodname()));
//				meb.addTextBody("ClassName", scobj.getClassname());
//				meb.addTextBody("MethodName", scobj.getMethodname());

				HttpEntity  reqEntity = meb.build();
				request.setEntity(reqEntity);
			} else {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ClassName", scobj
						.getClassname())); // classname
				params.add(new BasicNameValuePair("MethodName", scobj
						.getMethodname())); // Methodname
				params.addAll(scobj.getParams()); // 添加请求参数
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); // 设置参数的编码
			}
			// 超时设置
			/* 从连接池中取连接的超时时间 */
			HttpClient client = new DefaultHttpClient();
			// 请求超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 12000);
			// 读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,20000);
			HttpResponse httpResponse = client.execute(request); // 解析返回的内容
			httpReturnCode = httpResponse.getStatusLine().getStatusCode();
			switch (httpReturnCode) {
			case 200:
				result = EntityUtils.toString(httpResponse.getEntity());
				Log.i(TAG, result);
				srobj = srobj.DeserializeObject(result);
				break;
			case 404:
				srobj.initError("未找到远程请求地址！");
				break;
			default:
				srobj.initError("数据请求产生错误！");
				break;
			}
		} catch (Exception e) {
			Log.e("Network", e+"");
			srobj.initError("数据请求错误！请检查网络连接!");
		}
		return srobj;
	}
	
	
}
