package life.leek.launcher;

import java.util.ArrayList;
import java.util.List;

import life.leek.launcher.customwidget.AppIcon;
import life.leek.launcher.customwidget.TitleBar;
import life.leek.launcher.receiver.UIBroadcastReceiver;
import life.leek.launcher.setting.GlobalVariable;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.utils.CommonUtil;
import life.leek.launcher.utils.NetworkUtil;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DesktopActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = "DesktopActivity";

	private List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
	TitleBar titleBar;
	AppIcon app1;
	AppIcon app2;
	AppIcon app3;
	AppIcon app4;
	AppIcon app5;
	AppIcon app6;
	AppIcon app7;

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GlobalVariable.g_currentRunningActivity = this;
		GlobalVariable.g_wifi_connected = NetworkUtil.isWifiConnected(this);
		if (GlobalVariable.g_wifi_connected) {
			NetworkUtil.getWifiInfo(this);
		}
		setContentView(R.layout.activity_desktop);

		String appFilesPath = getApplicationContext().getFilesDir()
				.getAbsolutePath();
		if (!appFilesPath.endsWith("/")) {
			appFilesPath += "/";
		}
		String files_pattern = "/files/";
		if (appFilesPath.endsWith(files_pattern)) {
			appFilesPath = appFilesPath.substring(0, appFilesPath.length()
					- files_pattern.length());
			appFilesPath += "/";
		}
		GlobalVariable.setApkPath(appFilesPath);

		TextView versionTextView = (TextView) findViewById(R.id.version_desktop);
		versionTextView.setText(getString(R.string.version_name));
		titleBar = (TitleBar) findViewById(R.id.titlebar_desktop);

		sendBroadcast();

		app1 = (AppIcon) findViewById(R.id.app1_desktop);
		app2 = (AppIcon) findViewById(R.id.app2_desktop);
		app3 = (AppIcon) findViewById(R.id.app3_desktop);
		app4 = (AppIcon) findViewById(R.id.app4_desktop);
		app5 = (AppIcon) findViewById(R.id.app5_desktop);
		app6 = (AppIcon) findViewById(R.id.app6_desktop);
		app7 = (AppIcon) findViewById(R.id.app7_desktop);

		loadApps();

		upgrade();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		GlobalVariable.g_currentRunningActivity = this;
		loadApps();
	}

	private void loadApps() {
		GlobalVariable.g_installedApps = CommonUtil.getAllApps(this);
		if (null != apps) {
			apps.clear();
			apps = new ArrayList<ResolveInfo>();
		}
		for (int i = 0; i < Setting.APPS.length; i++) {
			String app_package_name = Setting.APPS[i][0];
			String[] packageNames = app_package_name.split(",");
			for (String packageName : packageNames) {
				for (ResolveInfo resolveInfo : GlobalVariable.g_installedApps) {
					String package_name = resolveInfo.activityInfo.packageName;
					// Log.i(TAG, "应用名称：" +
					// resolveInfo.loadLabel(getPackageManager()) + ", 包名：" +
					// resolveInfo.activityInfo.packageName);

					if (resolveInfo.activityInfo.packageName
							.equals(Setting.APP_PACKAGENAME)) {
					} else if (packageName.equals(package_name)) {
						apps.add(resolveInfo);
						break;
					}
				}
			}
		}
		boolean app1_installed = false;
		app1_installed = CommonUtil.isAppInstalled(this, Setting.APPS[0][0]);

		int app_total = apps.size();
		if (app1_installed) {
			if (app_total > 0) {
				app1.setResolveInfo(apps.get(0));
				app1.setVisibility(View.VISIBLE);
				app1.requestFocus();
			}
			if (app_total > 1) {
				app2.setResolveInfo(apps.get(1));
				app2.setVisibility(View.VISIBLE);
			}
			if (app_total > 2) {
				app3.setResolveInfo(apps.get(2));
				app3.setVisibility(View.VISIBLE);
			}
			if (app_total > 3) {
				app4.setResolveInfo(apps.get(3));
				app4.setVisibility(View.VISIBLE);
			}
			if (app_total > 4) {
				app5.setResolveInfo(apps.get(4));
				app5.setVisibility(View.VISIBLE);
			}
			if (app_total > 5) {
				app6.setResolveInfo(apps.get(5));
				app6.setVisibility(View.VISIBLE);
			}
			if (app_total > 6) {
				app7.setResolveInfo(apps.get(6));
				app7.setVisibility(View.VISIBLE);
			}
		} else {
			app1.setStaticInfo(
					Setting.APPS[0][1],
					getResources().getDrawable(
							CommonUtil
									.getDrawableResourceId(Setting.APPS[0][3])),
					Setting.APPS[0][0], Setting.APPS[0][2]);
			app1.setVisibility(View.VISIBLE);
			app1.requestFocus();

			if (app_total > 0) {
				app2.setResolveInfo(apps.get(0));
				app2.setVisibility(View.VISIBLE);
			}
			if (app_total > 1) {
				app3.setResolveInfo(apps.get(1));
				app3.setVisibility(View.VISIBLE);
			}
			if (app_total > 2) {
				app4.setResolveInfo(apps.get(2));
				app4.setVisibility(View.VISIBLE);
			}
			if (app_total > 3) {
				app5.setResolveInfo(apps.get(3));
				app5.setVisibility(View.VISIBLE);
			}
			if (app_total > 4) {
				app6.setResolveInfo(apps.get(4));
				app6.setVisibility(View.VISIBLE);
			}
			if (app_total > 5) {
				app7.setResolveInfo(apps.get(5));
				app7.setVisibility(View.VISIBLE);
			}
		}
		// switch (app_total) {
		// case 0:
		// app1.setVisibility(View.VISIBLE);
		// break;
		// case 1:
		// app2.setVisibility(View.VISIBLE);
		// break;
		// case 2:
		// app3.setVisibility(View.VISIBLE);
		// break;
		// case 3:
		// app4.setVisibility(View.VISIBLE);
		// break;
		// case 4:
		// app5.setVisibility(View.VISIBLE);
		// break;
		// case 5:
		// app6.setVisibility(View.VISIBLE);
		// break;
		// case 6:
		// app7.setVisibility(View.VISIBLE);
		// break;
		// default :
		// app7.setVisibility(View.VISIBLE);
		// break;
		// }

	}

	private void sendBroadcast() {
		Intent intent = new Intent(this, UIBroadcastReceiver.class);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		PendingIntent sender = null;
		intent.setAction(Setting.BROADCAST_REFRESH_UI);
		sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), 5000, sender);
	}

	public void refreshTitleBar(int wifiResId) {
		titleBar.updateTime();
		titleBar.updateWifiStatus(wifiResId);
	}

	public void refreshWifiStatus(int resId) {
		titleBar.updateWifiStatus(resId);
	}

	public void refreshTime() {
		titleBar.updateTime();
	}

	private void upgrade() {
		CommonUtil.upgrade(this);
	}

}
