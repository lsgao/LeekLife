package life.leek.launcher.setting;

import life.leek.launcher.R;
import android.app.Activity;

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
}
