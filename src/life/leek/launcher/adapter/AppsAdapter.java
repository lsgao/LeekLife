package life.leek.launcher.adapter;

import java.util.List;

import life.leek.launcher.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppsAdapter extends BaseAdapter {
	private List<ResolveInfo> apps;
	private Activity activity;

	private final int CELL_WIDTH = 200;
	private final int CELL_PADDING = 10;
	public AppsAdapter(Activity activity, List<ResolveInfo> apps){
    	this.activity = activity;
    	this.apps = apps;
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int i) {
        return apps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @SuppressLint({ "ViewHolder", "InflateParams" }) @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
    	ResolveInfo info = apps.get(i);
    	PackageManager pManager = activity.getPackageManager();
    	View convertView = LayoutInflater.from(activity).inflate(R.layout.view_appicon, null);
    	ImageView image = (ImageView) convertView.findViewById(R.id.image_appicon);
        TextView text = (TextView) convertView.findViewById(R.id.text_appicon);
    	text.setText(info.loadLabel(pManager));
    	text.setSingleLine();
    	text.setEllipsize(TruncateAt.END);
    	image.setImageDrawable(info.activityInfo.loadIcon(pManager));
    	AbsListView.LayoutParams params = null;
    	if (i % 8 == 0) {
    		params = new AbsListView.LayoutParams(CELL_WIDTH, CELL_WIDTH * 2 + CELL_PADDING);
    	} else if (i % 8 == 4) {
    		params = new AbsListView.LayoutParams(CELL_WIDTH, CELL_WIDTH * 2 + CELL_PADDING);
    	} else {
    		params = new AbsListView.LayoutParams(CELL_WIDTH, CELL_WIDTH);
    	}
    	convertView.setLayoutParams(params);
    	if (i % 8 == 4) {
    		convertView.setVisibility(View.GONE);
    	}

    	return convertView;
    }
}

