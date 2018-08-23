package life.leek.launcher.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import life.leek.launcher.R;
import life.leek.launcher.service.DownloadApkIntentService;
import life.leek.launcher.setting.GlobalVariable;
import life.leek.launcher.setting.Setting;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

public class CommonUtil {
	public static String TAG = CommonUtil.class.getName();

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
	 * 获取已安装的指定包名的APP
	 * @param context
	 * @return
	 */
	public static ResolveInfo getInstalledApp(Context context, String package_name) {
		if ( null == package_name || package_name.equals("")) {
			return null;
		}
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (null == GlobalVariable.g_installedApps) {
			GlobalVariable.g_installedApps = CommonUtil.getAllApps(context);
		}
		for (ResolveInfo resolveInfo :  GlobalVariable.g_installedApps) {
			if (resolveInfo.activityInfo.packageName.equals(package_name)) {
				return resolveInfo;
			}
		}
		return null;
	}

	/**
	 * 判断应用是否安装
	 * @param context
	 * @param package_name 应用包名
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String package_name) {
		final PackageManager packageManager = context.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals(package_name)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 通过包名查找APP是否在设置文件中
	 * @param package_name
	 * @return
	 */
	public static int findApp(String package_name) {
		for (int i = 0; i < Setting.APPS.length; i++) {
			String str = Setting.APPS[i][0];
			String[] packageNames = str.split(",");
			for (String packageName : packageNames) {
				if (packageName.equals(package_name)) {
					return i;
				}
			}
		}
		return -1;
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

	public static String getAndroidVersion() {
		int sdk = Build.VERSION.SDK_INT;
		String version = null;
		switch (sdk) {
		case 1:
			version = "1.0"; // no code name
			break;
		case 2:
			version = "1.1"; // no code name
			break;
		case 3:
			version = "1.5"; // Cupcake(NDK 1)
			break;
		case 4:
			version = "1.6"; // Donut(NDK 2)
			break;
		case 5:
			version = "2.0"; // Eclair
			break;
		case 6:
			version = "2.0.1"; // Eclair
			break;
		case 7:
			version = "2.1"; // Eclair(NDK 3)
			break;
		case 8:
			version = "2.2.x"; // Froyo(NDK 4)
			break;
		case 9:
			version = "2.3 - 2.3.2"; // Gingerbread(NDK 5)
			break;
		case 10:
			version = "2.3.3 - 2.3.7"; // Gingerbread
			break;
		case 11:
			version = "3.0"; // Honeycomb
			break;
		case 12:
			version = "3.1"; // Honeycomb(NDK 6)
			break;
		case 13:
			version = "3.2.x"; // Honeycomb
			break;
		case 14:
			version = "4.0.1 - 4.0.2"; // Ice Cream Sandwich(NDK 7)
			break;
		case 15:
			version = "4.0.3 - 4.0.4"; // Ice Cream Sandwich(NDK 8)
			break;
		case 16:
			version = "4.1.x"; // Jelly Bean
			break;
		case 17:
			version = "4.2.x"; // Jelly Bean
			break;
		case 18:
			version = "4.3.x"; // Jelly Bean
			break;
		case 19:
			version = "4.4 - 4.4.4"; // KitKat
			break;
		case 20:
			version = "4.4W"; // KitKat Watch
			break;
		case 21:
			version = "5.0"; // Lollipop
			break;
		case 22:
			version = "5.1"; // Lollipop
			break;
		case 23:
			version = "6.0"; // Marshmallow
			break;
		case 24:
			version = "7.0"; // Nougat
			break;
		case 25:
			version = "7.1"; // Nougat
		default:
			break;
		}
		return version;
	}

	/**
	 * 升级
	 * @param context
	 */
	public static void upgrade(Context context) {
		final String curVersion = GlobalVariable.g_currentRunningActivity.getString(R.string.version_name);
		Intent intent = new Intent(context, DownloadApkIntentService.class);
		intent.putExtra(Setting.KEY_URL, Setting.UPGRADE_APK_URL);
		intent.putExtra(Setting.KEY_CURRENT_VERSION, curVersion);
		intent.putExtra(Setting.KEY_VERSION_URL, Setting.UPGRADE_VERSION_URL);
		intent.putExtra(Setting.KEY_PACKAGE_NAME, context.getPackageName());
		intent.putExtra(Setting.KEY_MAIN_ACTIVITY, "life.leek.launcher.DesktopActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(intent);
	}

	/** 
     * 软件静默安装 
     * @param apkAbsolutePath apk文件所在路径 
     * @return 安装结果:获取到的result值<br> 
     */  
    public static boolean silentInstall(String apk_absolute_path, String package_name, String main_activity_name) {
    	boolean result = false;
        Process process = null;
        DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("pm install -r " + apk_absolute_path + "\n");
			os.writeBytes("am start -n " + package_name + "/" + main_activity_name + "\n");
			os.writeBytes("exit\n");

			os.flush();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
			result = false;
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
			if (process != null) {
				process.destroy();
			}
		}
		result = true;
        return result;
    }

}
