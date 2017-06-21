package life.leek.launcher.customwidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import life.leek.launcher.R;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.setting.GlobalVariable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TitleBar extends FrameLayout {
	public static String TAG = "TitleBar";
	private View mContentView;
	TextView timeTextView;
	ImageView wifiImageView;

	public TitleBar(Context context) {
		super(context);
		init(context);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContentView = View.inflate(context, R.layout.view_title, null); 

		setFocusable(false);
		setFocusableInTouchMode(false);
		timeTextView = (TextView) mContentView.findViewById(R.id.time_title);
		wifiImageView = (ImageView) mContentView.findViewById(R.id.wifi_title);
		updateTime();
		initWifi();

		addView(mContentView);
	}

	/**
	 * 更新屏幕上的当前时间
	 */
	public void updateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(Setting.FORMAT_CURRENT_TIME, Locale.getDefault());
		timeTextView.setText(sdf.format(new Date()));
	}
	public void updateWifiStatus(int resId) {
		wifiImageView.setImageResource(resId);
	}
    public void initWifi() {
		if (GlobalVariable.g_wifi_connected) {
			wifiImageView.setImageResource(GlobalVariable.WIFI_SIGNAL_STRENGTH[GlobalVariable.g_wifi_level]);
		} else {
			wifiImageView.setImageResource(0);
		}
    }
}
