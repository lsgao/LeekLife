package life.leek.launcher.setting;

import java.util.List;

import life.leek.launcher.R;
import life.leek.launcher.utils.CommonUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ResolveInfo;
import android.os.Build;

public class GlobalVariable {
	public static Activity g_currentRunningActivity;

	public static boolean g_wifi_connected = false;
	public static int g_wifi_level = 0;
	public static final int[] WIFI_SIGNAL_STRENGTH = {
			R.drawable.wifi1,
			R.drawable.wifi2,
			R.drawable.wifi3,
			R.drawable.wifi4,
			R.drawable.wifi5 };

	public static final int WIFI_LEVEL_COUNT = WIFI_SIGNAL_STRENGTH.length;

	// Android系统版本号
	private static String m_buildVersion = null;
	/**
	 * 系统版本号
	 * @return
	 */
	public static String getBuildVersionRelease() {
		if (null == m_buildVersion || ("").equals(m_buildVersion)) {
			m_buildVersion = CommonUtil.getAndroidVersion();
			if (null == m_buildVersion || ("").equals(m_buildVersion)) {
				m_buildVersion = Build.VERSION.RELEASE;
			}
			return m_buildVersion;
		} else {
			return m_buildVersion;
		}
	}

	private static String m_apk_path = "";
	public static void setApkPath(String apkPath) {
		m_apk_path = apkPath;
	}

	@SuppressLint("SdCardPath")
	public static String getApkPath() {
		if (null == m_apk_path || ("").equals(m_apk_path)) {
			String version_release = GlobalVariable.getBuildVersionRelease();
			if (version_release.startsWith(Setting.ANDROID_4_PREFIX)) {
				m_apk_path = "/data/data/" + Setting.APP_PACKAGENAME + "/";
			} else if (version_release.startsWith(Setting.ANDROID_5_PREFIX)) {
				m_apk_path = "/data/data/" + Setting.APP_PACKAGENAME + "/";
			} else if (version_release.startsWith(Setting.ANDROID_6_PREFIX)) {
				m_apk_path = "/data/user/0/" + Setting.APP_PACKAGENAME + "/";
			} else {
				m_apk_path = "/data/data/" + Setting.APP_PACKAGENAME + "/";
			}
		}
		return m_apk_path;
	}

	public static List<ResolveInfo> g_installedApps = null;
}
