package com.sytm.netcore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import android.util.Log;

public class HttpUtils {
	public static List<NameValuePair> map2pairList(Map<String, String> map){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		
		for(;it.hasNext();){
			Entry<String, String> entry = it.next();
			NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			list.add(pair);
		}
		
		return list;
	}
	
	public static String get(String url, HttpContext context){
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		Log.i("URL", url.toString());
		String str = "";
		try {
			HttpResponse response = client.execute(request, context);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String get(String url, HttpContext context, String charactor){
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		String str = "";
		try {
			HttpResponse response = client.execute(request, context);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity, charactor);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static String post(String url, Map<String, String> params, HttpContext context){
		HttpClient client = new DefaultHttpClient();
		UrlEncodedFormEntity uefEntity;
		String str = "";
		try {
			uefEntity = new UrlEncodedFormEntity(map2pairList(params), "UTF-8");
			HttpPost httppost = new HttpPost(url); 
			Log.i("URL", url.toString());
			httppost.setEntity(uefEntity);
			HttpResponse response = client.execute(httppost, context);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.getConnectionManager().shutdown();
		}
		return str;
	}
	
	public static String post(String url, Map<String, String> params, HttpContext context, String charactor){
		HttpClient client = new DefaultHttpClient();
		UrlEncodedFormEntity uefEntity;
		String str = "";
		try {
			uefEntity = new UrlEncodedFormEntity(map2pairList(params), charactor);
			HttpPost httppost = new HttpPost(url); 
			Log.i("URL", url.toString());
			httppost.setEntity(uefEntity);
			HttpResponse response = client.execute(httppost, context);
			HttpEntity entity = response.getEntity();
			str = EntityUtils.toString(entity, charactor);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.getConnectionManager().shutdown();
		}
		return str;
	}
	public static String post(String url,Map<String, String> params){
		HttpClient client = new DefaultHttpClient(); 
		UrlEncodedFormEntity uefEntity;
		String str = "";
		try {
			uefEntity = new UrlEncodedFormEntity(map2pairList(params), "utf-8");
			HttpPost httpPost = new HttpPost(url);
			Log.i("URL", url.toString());
			httpPost.setEntity(uefEntity);
			HttpResponse response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();
			str =  EntityUtils.toString(entity,"utf-8");
			Log.i("Result", str);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			client.getConnectionManager().shutdown();
		}
		
		return str;
		
	}

	
	
		
}
