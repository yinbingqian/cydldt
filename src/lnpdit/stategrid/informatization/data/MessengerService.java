package lnpdit.stategrid.informatization.data;

public class MessengerService {

	/*
	 * 
	 */
	public static final String CONTACT_CHOOSE_CONTACT = "lnpditnews.broadcast.com.contact.choose";
	/*
	 * 
	 */
	public static final int VISIBILITY_TRUE = 1;
	public static final int VISIBILITY_FALSE = 8;
	/*
	 * 
	 */
	public static final int LOADING_TIME = 10*1000;
	/*
	 * Webservice
	 */
	public static final String NAMESPACE = "MobileNewspaper";
//	public static final String IP = "http://200.20.30.145:82";
	public static final String IP = "http://200.20.30.201:82";
//	public static final String IP = "http://211.140.246.117";
//	public static final String IP = "http://219.148.199.62/dianli";
	public static final String URL = IP + "/phoneinvoke.asmx?wsdl";
	public static final String URL_WITHOUT_WSDL = IP + "/phoneinvoke.asmx";
	public static final String PIC_FILE = IP + "/manage/pic/";
	public static final String PIC_FILE_FC = IP + "/pic/";
	public static final String PIC_JOURNAL = IP + "/manage/magpic/";
	public static final String PIC_PUSH = IP + "/upload/";
	public static final String PIC_JOB = IP + "/upload/job/";
	public static final String PIC_YANBAO = IP + "/manage/pic/";
	public static final String URL_SERVER = IP + "/apksource/version.xml";
	public static final String VIDEO_PATH = IP + "/manage/videofile/";
	public static final String AUDIO_PATH = IP + "/audio/";
	public static final String COL_PATH = IP + "/columns.xml";
	public static final String HEAD_PIC_PATH = IP + "/upload/headpic/";
	public static final String COLUMN_PIC_PATH = IP + "/upload/column/";
	/*
	 * Webservice
	 */
	public static final String METHOD_UserInfoLogin = "UserInfoLogin";
	public static final String METHOD_GetAddressBookList = "GetAddressBookList";
	public static final String METHOD_MAS = "MAS";
	public static final String METHOD_GetWeather = "GetWeather";
	public static final String METHOD_WeatherAdd = "WeatherAdd";
	public static final String METHOD_GetJobPlan = "GetJobPlan";
	public static final String METHOD_JobPlanAdd = "JobPlanAdd";
	public static final String METHOD_NewsAdd = "NewsAdd";
	public static final String METHOD_GetJobBack = "GetJobBack";
	public static final String METHOD_JobBackAdd = "JobBackAdd";
	public static final String METHOD_GetDeptList = "GetDeptList";
	public static final String METHOD_GetAddressBookListByID = "GetAddressBookListByID";
	public static final String METHOD_GetNewsTitlePageSize = "GetNewsTitlePageSize";
	public static final String METHOD_GetNewsContent = "GetNewsContent";
	public static final String METHOD_GetMagazineInfo = "GetMagazineInfo";
	public static final String METHOD_GetMagazinePicInfo = "GetMagazinePicInfo";
	public static final String METHOD_GetVideoInfo = "GetVideoInfo";
	public static final String METHOD_GetTikuRandom = "GetTikuRandom";
	public static final String METHOD_GetTikuRandomById = "GetTikuRandomById";
	public static final String METHOD_GetTikuColumn = "GetTikuColumn";
	public static final String METHOD_GetNewsByDepartmentId = "GetNewsByDepartmentId";

	public static final String WEEK = "week";

}
