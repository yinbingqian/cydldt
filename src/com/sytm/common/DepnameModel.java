package com.sytm.common;

public class DepnameModel {

	private String depname ;
	private String id = "";
	private String organid = "";
	private String remarks = "";
	private String tags = "";
	private String firstAlpha;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrganid() {
		return organid;
	}
	public void setOrganid(String organid) {
		this.organid = organid;
	}
	public String getDepname() {
		return depname;
	}
	public void setDepname(String depname) {
		this.depname = depname;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getFirstAlpha() {
		return firstAlpha;
	}
	public void setFirstAlpha(String firstAlpha) {
		this.firstAlpha = firstAlpha;
	}
	
}
