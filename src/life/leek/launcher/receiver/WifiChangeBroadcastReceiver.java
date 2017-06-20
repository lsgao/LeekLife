package life.leek.launcher.receiver;

import java.util.List;

import life.leek.launcher.DesktopActivity;
import life.leek.launcher.R;
import life.leek.launcher.setting.GlobalVariable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {
	private Context mContext;
	WifiManager mWifiManager;
	boolean mWifiEnabled;
	int mWifiRssi;
	String mWifiSsid;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		// final String action = intent.getAction();
		updateWifiState(intent);
	}

	private void updateWifiState(Intent intent) {
		final String action = intent.getAction();
		if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			mWifiEnabled = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN) == WifiManager.WIFI_STATE_ENABLED;

		} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			final NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			boolean wasConnected = GlobalVariable.g_wifi_connected;
			GlobalVariable.g_wifi_connected = networkInfo != null
					&& networkInfo.isConnected();
			// If we just connected, grab the inintial signal strength and ssid
			if (GlobalVariable.g_wifi_connected && !wasConnected) {
				// try getting it out of the intent first
				WifiInfo info = (WifiInfo) intent
						.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
				if (info == null) {
					info = mWifiManager.getConnectionInfo();
				}
				if (info != null) {
					mWifiSsid = huntForSsid(info);
				} else {
					mWifiSsid = null;
				}
			} else if (!GlobalVariable.g_wifi_connected) {
				mWifiSsid = null;
			}
		} else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
			mWifiRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -200);
			GlobalVariable.g_wifi_level = WifiManager.calculateSignalLevel(mWifiRssi,
					GlobalVariable.WIFI_LEVEL_COUNT);
		} else {
			WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
			if (wifiInfo.getBSSID() != null) {
				GlobalVariable.g_wifi_level = WifiManager.calculateSignalLevel(
						wifiInfo.getRssi(), 5);
			}
		}

		updateWifiIcons();
	}

	private String huntForSsid(WifiInfo info) {
		String ssid = info.getSSID();
		if (ssid != null) {
			return ssid;
		}
		// OK, it's not in the connectionInfo; we have to go hunting for it
		List<WifiConfiguration> networks = mWifiManager.getConfiguredNetworks();
		for (WifiConfiguration net : networks) {
			if (net.networkId == info.getNetworkId()) {
				return net.SSID;
			}
		}
		return null;
	}

	private void updateWifiIcons() {
		int wifi_icon_id = 0;
		if (GlobalVariable.g_wifi_connected) {
			wifi_icon_id = GlobalVariable.WIFI_SIGNAL_STRENGTH[GlobalVariable.g_wifi_level];
		} else {
			wifi_icon_id = mWifiEnabled ? R.drawable.wifi0 : 0;
		}
		if (GlobalVariable.g_currentRunningActivity instanceof DesktopActivity) {
			((DesktopActivity) GlobalVariable.g_currentRunningActivity)
					.refreshWifiStatus(wifi_icon_id);
		}
	}
}
