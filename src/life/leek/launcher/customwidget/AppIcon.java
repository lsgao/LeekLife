package life.leek.launcher.customwidget;

import life.leek.launcher.R;
import life.leek.launcher.setting.GlobalVariable;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.utils.CommonUtil;
import life.leek.launcher.utils.DownloadApkFileManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppIcon extends RelativeLayout {
	@SuppressWarnings("unused")
	private static final String TAG = "AppIcon";

	public static final int ONCLICK_TYPE_DOWNLOAD_APP = 0;
	public static final int ONCLICK_TYPE_RUN_APP = 1;
	public static final int ONCLICK_TYPE_RUN_SYSTEM = 2;

	public static final int TYPE_USER = 0;
	public static final int TYPE_PREINSTALLED = 1;
	public static final int TYPE_SYSTEM = 2;

	private Context mParent;
	private View mContext;
	private TextView nameTextView;
	private ImageView iconImageView;
	private ResolveInfo m_info;
	private int m_index = -1;
	private String m_packageName;
	private String m_displayText;
	private String m_downloadUrl;
	private Drawable m_icon;
	private int m_type;
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				nameTextView.setTextColor(mParent.getResources().getColor(R.color.icon_text_focus));
				setBackground(true);
			}else {
				nameTextView.setTextColor(mParent.getResources().getColor(R.color.icon_text));
				setBackground(false);
			}
			invalidate();
		}
	};
//
//	public AppIcon(Context context) {
//		super(context);
//		init(context);
//	}

	public AppIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppIcon);
		m_index = typedArray.getInteger(R.styleable.AppIcon_index, 0) - 1;
		typedArray.recycle();
		init(context);
	}

	public void setOnClickListener(int type) {
		if (type == ONCLICK_TYPE_DOWNLOAD_APP) {
			this.setOnClickDownloadApp();
		} else if (type == ONCLICK_TYPE_RUN_APP) {
			if (null != m_info) {
				this.setOnClickRunApp();
			} else {
				this.setOnClickDownloadApp();
			}
		} else if (type == ONCLICK_TYPE_RUN_SYSTEM) {
			this.setOnClickRunSystem();
		}
	}

	/**
	 * 点击事件设定——下载应用
	 */
	private void setOnClickDownloadApp() {
		if (m_type == TYPE_USER) {
			this.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// 下载应用
					DownloadApkFileManager downloadmgr = new DownloadApkFileManager(
							m_downloadUrl,
							m_packageName,
							GlobalVariable.g_currentRunningActivity,
							new DownloadApkFileManager.ApkFileDownloadingListener() {
								@Override
								public void onNetworkFailed() {
									showTip(GlobalVariable.g_currentRunningActivity.getString(R.string.apk_download_err_msg), Toast.LENGTH_SHORT);
								}
	
								@Override
								public void onInstallFailed() {
									showTip(GlobalVariable.g_currentRunningActivity.getString(R.string.install_error_msg), Toast.LENGTH_SHORT);
								}
							});
					downloadmgr.downloadApkFile();
				}
			});
		}
		invalidate();
	}

//	private void setOnClickOpenMarket() {
//		this.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				// 跳转到下载应用页
//				Uri uri = Uri.parse("market://details?id=" + m_packageName);
//				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//			    try {
//			        goToMarket.setClassName("com.dangbeimarket", "com.dangbeimarket.activity.NewDetailActivity");
//			        mParent.startActivity(goToMarket);
//			    } catch (ActivityNotFoundException e) {
//			    	Log.e(TAG, e.getMessage(), e);
//			    }
//			}
//		});
//		invalidate();
//	}

	/**
	 * 点击事件设定——运行应用
	 * @param info
	 */
	private void setOnClickRunApp() {
		if (m_type == TYPE_USER || m_type == TYPE_PREINSTALLED) {
			this.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// 该应用的包名
					String pkg = m_info.activityInfo.packageName;
					// 应用的主activity类
					String cls = m_info.activityInfo.name;
					ComponentName componet = new ComponentName(pkg, cls);
	
					Intent intent = new Intent();
					intent.setComponent(componet);
					mParent.startActivity(intent);
				}
			});
		}
		invalidate();
	}

	private void setOnClickRunSystem() {
		if (m_type == TYPE_SYSTEM) {
			this.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(m_downloadUrl);
					mParent.startActivity(intent);
				}
			});
		}
		invalidate();
	}

	private void init(Context context) {
		mParent = context;
		mContext = View.inflate(context, R.layout.view_appicon, null);

		nameTextView = (TextView) mContext.findViewById(R.id.text_appicon);
		iconImageView = (ImageView) mContext.findViewById(R.id.image_appicon);
		
		nameTextView.setSingleLine();
		nameTextView.setEllipsize(TruncateAt.END);
		mContext.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		addView(mContext);
		setBackground(false);

		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnFocusChangeListener(focusChangeListener);
		m_packageName = Setting.APPS[m_index][0];
		m_displayText = Setting.APPS[m_index][1];
		m_downloadUrl = Setting.APPS[m_index][2];
		m_icon = getResources().getDrawable(CommonUtil.getDrawableResourceId(Setting.APPS[m_index][3]));
		try {
			m_type = Integer.valueOf(Setting.APPS[m_index][4]);
		} catch (NumberFormatException e) {
			m_type = TYPE_USER;
		}

		nameTextView.setText(m_displayText);
		iconImageView.setImageDrawable(m_icon);
	}

	private void setBackground(boolean on_focus) {
		if (m_index != -1) {
			int res_id = -1;
			if (on_focus) {
				res_id = CommonUtil.getDrawableResourceId("app" + (m_index + 1) + "_focus");
			} else {
				res_id = CommonUtil.getDrawableResourceId("app" + (m_index + 1));
			}
			if (res_id != -1) {
				setBackgroundResource(res_id);
			}
		}
	}
	
	public void setResolveInfo(ResolveInfo info) {
		this.m_info = info;
	}

	private void showTip(CharSequence text, int duration) {
		Looper.prepare();
		Toast.makeText(GlobalVariable.g_currentRunningActivity, text, duration).show();
		Looper.loop();
	}

	public void setType(int m_type) {
		this.m_type = m_type;
	}

	public int getType() {
		return this.m_type;
	}
}
