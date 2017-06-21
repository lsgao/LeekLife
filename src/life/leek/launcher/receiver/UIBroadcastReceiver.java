package life.leek.launcher.receiver;

import life.leek.launcher.DesktopActivity;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.setting.GlobalVariable;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UIBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if ((Setting.BROADCAST_REFRESH_UI).equals(action))
		if (GlobalVariable.g_currentRunningActivity instanceof DesktopActivity) {
			((DesktopActivity) GlobalVariable.g_currentRunningActivity)
					.refreshTime();
		}
	}
}
