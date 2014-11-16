package com.sytm.netcore;

import org.json.JSONException;

import com.sytm.util.JsonUtils;

/**
 * 服务端返回结果
 * 
 * @return
 */
public class ServiceResult {

	private boolean IsError = false;
	private boolean IsWarn = false;
	private String Message = "";
	private String Data = null;

	
	public boolean GetIsError() {
		return IsError;
	}
   

	public void setIsError(boolean error) {
		this.IsError = error;
	}


	public boolean getIsWarn() {
		return IsWarn;
	}


	public void setIsWarn(boolean warn) {
		this.IsWarn = warn;
	}


	public String getMessage() {
		return Message;
	}


	public void setMessage(String message) {
		this.Message = message;
	}


	public String getData() {
		return Data;
	}


	public void setData(String data) {
		this.Data = data;
	}
	
    public void initError(String message)
    {
    	this.IsError = true;
    	this.IsWarn  = false;
    	this.Message = message;    	
    }
    public void initWarn(String message)
    {
    	this.IsError = false;
    	this.IsWarn = true;
    	this.Message = message;    	
    }
	/***
	 * 反序列json
	 * @param str
	 * @return
	 */
	public ServiceResult DeserializeObject(String jsonString)
	{		
		try {
			return (ServiceResult)JsonUtils.parseObject(jsonString, ServiceResult.class);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
