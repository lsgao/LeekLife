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
import android.widget.TextView;

public class DesktopActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = DesktopActivity.class.getName();

	TitleBar titleBar;
	List<AppIcon> appList = new ArrayList<AppIcon>();


	List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
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

		appList.add((AppIcon) findViewById(R.id.app1_desktop));
		appList.add((AppIcon) findViewById(R.id.app2_desktop));
		appList.add((AppIcon) findViewById(R.id.app3_desktop));
		appList.add((AppIcon) findViewById(R.id.app4_desktop));
		appList.add((AppIcon) findViewById(R.id.app5_desktop));
		appList.add((AppIcon) findViewById(R.id.app6_desktop));
		appList.add((AppIcon) findViewById(R.id.app7_desktop));
		appList.add((AppIcon) findViewById(R.id.app8_desktop));
		appList.add((AppIcon) findViewById(R.id.app9_desktop));
		appList.add((AppIcon) findViewById(R.id.app10_desktop));
		appList.add((AppIcon) findViewById(R.id.app11_desktop));

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
//		for (ResolveInfo info : GlobalVariable.g_installedApps) {
//			Log.i(TAG, info.activityInfo.name + " --- " + info.activityInfo.packageName);
//		}
		if (null != apps) {
			apps.clear();
			apps = new ArrayList<ResolveInfo>();
		}

		for (int i = 0; i < Setting.APPS.length; i++) {
			String app_package_name = Setting.APPS[i][0];
			String[] packageNames = app_package_name.split(",");
			for (int k = 0; k < packageNames.length; k ++) {
				String packageName = packageNames[k];
				ResolveInfo resolveInfo = CommonUtil.getInstalledApp(this, packageName);
				if (null != resolveInfo) {
					apps.add(resolveInfo);
					break;
				} else {
					if (k == (packageNames.length - 1)) {
						apps.add(null);
					}
				}
			}
		}

		for (int i = 0; i < appList.size(); i ++) {
			setIconInfo(appList.get(i), i);
		}

	}

	private void setIconInfo(AppIcon app_icon, int pos) {
		if (null != apps.get(pos)) {
			app_icon.setResolveInfo(apps.get(pos));
			app_icon.setOnClickListener(AppIcon.ONCLICK_TYPE_RUN_APP);
		} else {
			if (app_icon.getType() == AppIcon.TYPE_SYSTEM) {
				app_icon.setOnClickListener(AppIcon.ONCLICK_TYPE_RUN_SYSTEM);
			} else {
				app_icon.setOnClickListener(AppIcon.ONCLICK_TYPE_DOWNLOAD_APP);
			}
		}
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
