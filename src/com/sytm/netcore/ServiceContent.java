package com.sytm.netcore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * 请求内容
 * 
 * @author wyq
 * 
 */
public class ServiceContent {
	private String classname = "";
	private String methodname = "";
	private String parametercontent = "";	
	private List<NameValuePair> params = new ArrayList<NameValuePair>();	
	private List<MultiStringData> stringparts = new ArrayList<MultiStringData>();
	private List<MultiFileData> fileparts = new ArrayList<MultiFileData>();
	private boolean multipost = false;
	public ServiceContent() {
	}
	public ServiceContent(String classname, String methodname, String parametercontent) {
		this.classname = classname;
		this.methodname = methodname;	
		this.parametercontent = parametercontent;
	}

	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public String getParametercontent() {
		return parametercontent;
	}
	public void setParametercontent(String parametercontent) {
		this.parametercontent = parametercontent;
	}
	public List<NameValuePair> getParams() {
		return params;
	}
	public void setParams(List<NameValuePair> params) {
		this.params = params;
	}
	public List<MultiStringData> getStringparts() {
		return stringparts;
	}
	public void setStringparts(List<MultiStringData> stringparts) {
		this.stringparts = stringparts;
	}
	public List<MultiFileData> getFileparts() {
		return fileparts;
	}
	public void setFileparts(List<MultiFileData> fileparts) {
		this.fileparts = fileparts;
	}
	
	public boolean isMultipost() {
		return multipost;
	}
	public void setMultipost(boolean multipost) {
		this.multipost = multipost;
	}
	//添加参数
	public void addParameter(String key,String value)
	{
		
		//添加参数
		this.params.add(new BasicNameValuePair(key,value));
		/*if(this.parametercontent !=null)
		{
			if(this.parametercontent.length()>0)
			{
				this.parametercontent+="^";
				int pos = this.parametercontent.indexOf(key+":");
				int pos1 = this.parametercontent.indexOf("^",pos);
				if(pos > -1)
				{
					String str1 = this.parametercontent.substring(0,pos);
					String str2 = "";
					if(pos1>-1){
						str2 = this.parametercontent.substring(pos1+1);
					}
					this.parametercontent = str1+str2;					
				}
				
			}
	
			
			this.parametercontent += key+":"+value;
			
		}*/		
		
	}
	/***
	 * 添加StringBody参数
	 * @param key
	 * @param value
	 */
	public void addStringPart(String key,String value)
	{
		this.stringparts.add(new MultiStringData(key,value));
		
	}
	/***
	 * 添加FileBody参数
	 * @param key
	 * @param file
	 */
	public void addFilePart(String key,File file,String filename)
	{
		this.fileparts.add(new MultiFileData(key, file, filename));		
	}
	/***
	 * 添加FileBody参数
	 * @param key
	 * @param file
	 */
	public void addFilePart(String key,File file)
	{
		this.fileparts.add(new MultiFileData(key, file));		
	}
	//重置清空
	public void reset()
	{
		this.classname = "";
		this.methodname = "";	
		this.parametercontent = "";
		this.params.clear();
		this.multipost = false;
		this.fileparts.clear();
		this.stringparts.clear();
	}
}
