package lnpdit.stategrid.informatization.adapter;

public class ImageAndTextNewsList {

	private String imageUrl;
	private String text_webid;
	private String text_value;
	private String text_title;
	private String text_newstitle;
	private String text_time;

	public ImageAndTextNewsList(String textwebid,
			String textheadurl, String textvalue, String texttitle,
			String textnewstitle, String texttime) {
		this.imageUrl = textheadurl;
		this.text_webid = textwebid;
		this.text_value = textvalue;
		this.text_title = texttitle;
		this.text_newstitle = textnewstitle;
		this.text_time = texttime;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getWebId() {
		return text_webid;
	}

	public String getValue() {
		return text_value;
	}

	public String getTitle() {
		return text_title;
	}
	
	public String getNewsTitle() {
		return text_newstitle;
	}

	public String getTime() {
		return text_time;
	}
	
}
