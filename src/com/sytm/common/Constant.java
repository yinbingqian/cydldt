package com.sytm.common;

import java.nio.charset.Charset;
import android.os.Environment;

public class Constant {
	/**
	 * 每页显示数量
	 */
	public final static int size = 20;
	/**
	 * 定位服务完整包名
	 */
	public final static String Locationservice = "com.sytm.application.LocationService";
	/**
	 * 服务平台地址
	 */
	 public final static String Service_Domain = "http://xj.sytm.net/";
	public final static String Service_Url = Service_Domain
			+ "ServiceCenter.axd";
	/**
	 * 版本更新地址
	 */
	public final static String AppUpload_Url = Service_Domain
			+ "ServiceCenter.axd";
	/**
	 * 基站地址
	 */
	public final static String CellInfo_Domain = "http://ms.sytm.net/";
	public final static String CellInfo_Url = CellInfo_Domain + "Mtbs.axd";
	/**
	 * 测试
	 */
	 public final static String Service_AccessToken = "123456";
	/**
	 * 正式
	 */
//	public final static String Service_AccessToken = "654321";
	/**
	 * 百度Key
	 */
//	 public final static String BaiDu_Key1 ="0Cbfbb850cbf43dd103961220c6000d6";

	public final static String BaiDu_Key1 = "tTrYcwb0BT1I0v6fiykcMyCL";
	/**
	 * 浏览器Key
	 */
	public final static String BaiDu_KeyB = "56533b80189b1dcadd1557183fb82581";
	/**
	 * Geocoding_API
	 */
	public final static String Geocoding_API_Url = "http://api.map.baidu.com/geocoder/v2/?ak="
			+ BaiDu_KeyB + "&output=json&pois=1";
	/**
	 * 字符集
	 */
	public final static Charset CHARSET = Charset.forName("UTF-8");
	/**
	 * 上传格式
	 */
	public static final String UpFormat = "doc,docx,jpg,xls,xlsx,ppt,pptx,txt,png,bmp";
	/**
	 * 文件格式
	 */
	public static final String FileFormat = "doc,docx,xls,xlsx,ppt,pptx,txt";
	/**
	 * word文档
	 * 
	 */
	public static final String WordFormat = "doc,docx";
	/**
	 * xls文档
	 * 
	 */
	public static final String XlsFormat = "xls,xlsx";
	/**
	 * ppt文档
	 * 
	 */
	public static final String PptFormat = "ppt,pptx";
	/**
	 * txt文档
	 * 
	 */
	public static final String TxtFormat = "txt";
	/**
	 * 图片格式
	 */
	public static final String ImgFormat = "jpg,png,bmp";
	/**
	 * IM服务host地址
	 */
	public final static String IMService_Host = "61.137.188.77";
	// public final static String IMService_Host = "192.168.0.16";
	/**
	 * IM客户端链接端口
	 */
	public final static int IMService_Port = 5222;
	/**
	 * 正式
	 */
	public static final String Push_API_Key = "tTrYcwb0BT1I0v6fiykcMyCL";
	public static final String RESPONSE_METHOD = "method";
	public static final String RESPONSE_CONTENT = "content";
	public static final String RESPONSE_ERRCODE = "errcode";
	public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
	public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";

	/**
	 * 定位服务名称
	 */
	public static final String LocationService_Name = "com.sytm.application.LocationService";

	/*********** 推送指令 ************/

	/**
	 * 短消息
	 */
	public static final String Push_Command_Message = "ShowMessage";
	/**
	 * 获取位置
	 */
	public static final String Push_Command_GetLoc = "GetLocation";
	/**
	 * 客户端立即定位
	 */
	public static final String Push_Command_GetLoc2 = "GetLocation2";
	/**
	 * 震动
	 */
	public static final String Push_Command_Shock = "Shock";
	/**
	 * 收到汇报
	 */
	public static final String Push_Command_SendReport = "SendReport";
	/**
	 * 收到汇报回复
	 */
	public static final String Push_Command_ReplayReport = "ReplayReport";
	/***
	 * 收到公告
	 */
	public static final String Push_Command_Announcement = "Announcement";
	/**
	 * 收到请假信息
	 */
	public static final String Push_Command_AskLeave = "AskLeave";
	/**
	 * 收到请假审批信息
	 */
	public static final String Push_Command_AskLeaveAudit = "AskLeaveAudit";
	/**
	 * 收到请假信息
	 */
	public static final String Push_Command_AskTravel = "AskTravel";
	/**
	 * 收到请假审批信息
	 */
	public static final String Push_Command_AskTravelAudit = "AskTravelAudit";
	/***
	 * 错误日志保存路径
	 */
	public static final String CrashLogPath = Environment
			.getExternalStorageDirectory().getPath() + "/sytm/crash/";
	/***
	 * 下载文件保存路径
	 */
	public static final String DownLoadPath = Environment
			.getExternalStorageDirectory().getPath() + "/sytm/downloadFile/";
	/***
	 * APK更新保存路径
	 */
	public static final String APKPath = Environment
			.getExternalStorageDirectory().getPath() + "/sytm/apkFile/";

	/**
	 * 根据后缀名打开文件
	 */
	public static final String[][] MIME_MapTable = {
			// {后缀名，MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" },
			{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" },
			{ ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" },
			{ ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

}
