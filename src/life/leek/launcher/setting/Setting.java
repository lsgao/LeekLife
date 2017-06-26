package life.leek.launcher.setting;

public class Setting {
	public final static String APP_PACKAGENAME = "life.leek.launcher";
//	public final static String[][] APP_ARRAY = { 
//		{ "com.sillatv", // 신라티비
//			"tv.tool.netspeedtest", // 网络测速
//			"com.dangbeimarket", // 当贝市场
//			"com.android.settings,com.android.tv.settings", // 设置
//			"com.elinkway.tvlive2", // 电视家
//			"com.dangbei.myapp" // 我的应用
//		},
//		{ "신라TV", "인테넷테스트", "앱스토어", "설정", "중국TV", "기타앱" } };
	public final static String[][] APPS = { 
		{"com.sillatv", "신라TV", "http://update.sillatv.com/AndroidBox/update/SillaTV.apk", "sillatv"},// 신라티비
		{"tv.tool.netspeedtest", "인테넷테스트"}, // 网络测速
		{"com.dangbeimarket", "앱스토어"}, // 当贝市场
		{"com.android.settings,com.android.tv.settings", "설정"}, // 设置
		{"com.elinkway.tvlive2", "중국TV"}, // 电视家
		{"com.dangbei.myapp", "기타앱"} // 我的应用
	};
	// 日期时间显示格式 - 当前时间格式
	public final static String FORMAT_CURRENT_TIME = "HH:mm";

	public final static String BROADCAST_REFRESH_UI = "REFRESH_TIME";

	public final static String ANDROID_4_PREFIX = "4.";
	public final static String ANDROID_5_PREFIX = "5.";
	public final static String ANDROID_6_PREFIX = "6.";

	// 时间参数（毫秒）- 连接超时
	public final static int TIME_HTTP_CONNECTION_TIMEOUT = 3 * 1000; //3seconds

	public final static String KEY_URL = "url";
	public final static String KEY_CURRENT_VERSION = "current_version";
	public final static String KEY_VERSION_URL = "version_url";
	public final static String KEY_PACKAGE_NAME = "package_name";
	public final static String KEY_MAIN_ACTIVITY = "main_activity";
	public final static String KEY_HTTP = "http://";

	public final static String UPGRADE_APK_URL = "http://update.leekleek.com/LeekLife-App/LeekLife.apk";
	public final static String UPGRADE_VERSION_URL = "http://update.leekleek.com/LeekLife-App/version.php";

	public final static String ENCODING = "UTF-8";
}
