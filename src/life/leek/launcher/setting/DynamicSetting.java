package life.leek.launcher.setting;

public class DynamicSetting {
	public final static String APP_PACKAGENAME = "life.leek.launcher";
	public final static String[][] APP_ARRAY = { 
		{ "com.sillatv", // 신라티비
			"tv.tool.netspeedtest", // 网络测速
			"com.dangbeimarket", // 当贝市场
			"com.android.settings,com.android.tv.settings", // 设置
			"com.elinkway.tvlive2", // 电视家
			"com.dangbei.myapp" // 我的应用
		},
		{ "신라TV", "인테넷테스트", "앱스토어", "설정", "중국TV", "기타앱" } };
	// 日期时间显示格式 - 当前时间格式
	public final static String FORMAT_CURRENT_TIME = "HH:mm";

	public final static String BROADCAST_REFRESH_UI = "REFRESH_TIME";
}
