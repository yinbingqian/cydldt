package com.sytm.netcore;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.StringBody;

import com.sytm.common.Constant;

/**
 * Part-StringBody
 * 
 * @author wyq
 * 
 */
public class MultiStringData {
	private String key = "";
	private StringBody value;

	public MultiStringData() {

	}

	public MultiStringData(String key, StringBody value) {
		this.key = key;
		this.value = value;
	}

	@SuppressWarnings("deprecation")
	public MultiStringData(String key, String value) {
		this.key = key;
		StringBody sb;
		try {
			sb = new StringBody(value, Constant.CHARSET);
			this.value = sb;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public StringBody getValue() {
		return value;
	}

	public void setValue(StringBody value) {
		this.value = value;
	}

}
