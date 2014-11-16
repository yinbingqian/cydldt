package com.sytm.netcore;

import java.io.File;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.protocol.HTTP;

/***
 * Part FileData
 * 
 * @author wyq
 * 
 */

public class MultiFileData {
	private String key = "";
	private FileBody value;

	public MultiFileData(String key, FileBody value) {
		this.key = key;
		this.value = value;
	}

	public MultiFileData(String key, File file) {
		this.key = key;
		FileBody fb;
		fb = new FileBody(file);
		this.value = fb;

	}
	@SuppressWarnings("deprecation")
	public MultiFileData(String key, File file,String filename) {
		this.key = key;
		FileBody fb;
		fb = new FileBody(file);
		fb = new FileBody(file, filename, "multipart/form-data", HTTP.UTF_8);
		this.value = fb;

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FileBody getValue() {
		return value;
	}

	public void setValue(FileBody value) {
		this.value = value;
	}

}
