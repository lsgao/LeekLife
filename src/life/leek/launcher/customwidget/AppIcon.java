package life.leek.launcher.customwidget;

import life.leek.launcher.R;
import life.leek.launcher.setting.DynamicSetting;
import life.leek.launcher.utils.CommonUtil;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppIcon extends RelativeLayout {
	private Context mParent;
	private View mContext;
	private PackageManager mPackageManager;
	private TextView nameTextView;
	private ImageView iconImageView;
	private ResolveInfo m_info;
	private int m_index = -1;
	private OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				nameTextView.setTextColor(mParent.getResources().getColor(R.color.icon_text_focus));
				setBackground(hasFocus);
			}else {
				nameTextView.setTextColor(mParent.getResources().getColor(R.color.icon_text));
				setBackground(hasFocus);
			}
			invalidate();
		}
	};

	public AppIcon(Context context) {
		super(context);
		init(context);
	}

	public AppIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AppIcon);
		m_index = typedArray.getInteger(R.styleable.AppIcon_index, -1);
		typedArray.recycle();
		init(context);
	}

	public void setResolveInfo(ResolveInfo info) {
		this.m_info = info;

		setFocusable(true);
		setFocusableInTouchMode(true);
		setText();
		iconImageView.setImageDrawable(m_info.activityInfo.loadIcon(mPackageManager));
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
		invalidate();
	}

	private void init(Context context) {
		mParent = context;
		mContext = View.inflate(context, R.layout.view_appicon, null);
		mPackageManager = context.getPackageManager();

		nameTextView = (TextView) mContext.findViewById(R.id.text_appicon);
		iconImageView = (ImageView) mContext
				.findViewById(R.id.image_appicon);
		
		nameTextView.setSingleLine();
		nameTextView.setEllipsize(TruncateAt.END);
		mContext.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		addView(mContext);
		setBackground(false);

		setFocusable(true);
		setFocusableInTouchMode(true);
		if (null != m_info) {
			setText();
			iconImageView.setImageDrawable(m_info.activityInfo.loadIcon(mPackageManager));
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
		} else {
			
		}
		setOnFocusChangeListener(focusChangeListener);
	}

	private void setBackground(boolean on_focus) {
		if (m_index != -1) {
			int res_id = -1;
			if (on_focus) {
				res_id = CommonUtil.getDrawableResourceId("app" + m_index + "_focus");
			} else {
				res_id = CommonUtil.getDrawableResourceId("app" + m_index);
			}
			if (res_id != -1) {
				setBackgroundResource(res_id);
			}
		}
	}

	private void setText() {
		int k = CommonUtil.findApp(m_info.activityInfo.packageName);
		if (k != -1) {
			nameTextView.setText(DynamicSetting.APP_ARRAY[1][k]);
		} else {
			nameTextView.setText(m_info.loadLabel(mPackageManager));
		}
	}
}
