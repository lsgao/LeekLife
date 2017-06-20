package life.leek.launcher.utils;

import java.lang.reflect.Field;
import java.util.List;

import life.leek.launcher.R;
import life.leek.launcher.setting.DynamicSetting;
import life.leek.launcher.setting.GlobalVariable;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class CommonUtil {
	public static String TAG = "CommonUtil";

	/**
	 * 获取所有APP
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> getAllApps(Context context) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// new ImageView(DesktopActivity.this);

		return context.getPackageManager().queryIntentActivities(mainIntent, 0);
	}

	/**
	 * 通过包名查找APP
	 * @param package_name
	 * @return
	 */
	public static int findApp(String package_name) {
		for (int i = 0; i < DynamicSetting.APP_ARRAY[0].length; i++) {
			String str = DynamicSetting.APP_ARRAY[0][i];
			String[] packageNames = str.split(",");
			for (String packageName : packageNames) {
				if (packageName.equals(package_name)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void getWifiInfo(Context context) {
		WifiInfo wifiInfo = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		if (wifiInfo.getBSSID() != null) {
			// wifi名称
			String ssid = wifiInfo.getSSID();
			// wifi信号强度
			int signalLevel = WifiManager.calculateSignalLevel(
					wifiInfo.getRssi(), 5);
			GlobalVariable.g_wifi_level = signalLevel;
			// wifi速度
			int speed = wifiInfo.getLinkSpeed();
			// wifi速度单位
			String units = WifiInfo.LINK_SPEED_UNITS;
			Log.i(TAG, "wifi名称: " + ssid + ", wifi信号强度: " + signalLevel + ", wifi速度: " + speed + ", wifi速度单位" + units);
		}
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 根据资源ID获取资源ID
	 * @param name
	 * @return
	 */
	public static int getDrawableResourceId(String name){
    	Field field;
		try {
			field = R.drawable.class.getField(name);
			return Integer.parseInt(field.get(null).toString());
		} catch (NoSuchFieldException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (NumberFormatException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return -1;
    }

}
