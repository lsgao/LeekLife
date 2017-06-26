package life.leek.launcher.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import life.leek.launcher.R;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.setting.GlobalVariable;

public class DownloadApkFileManager {
	public final static String TAG = "DownloadApkManager";
	private static final int DOWNLOAD_STARTED = 0;
	private static final int DOWNLOAD_PROCESSING = 1;
	private static final int DOWNLOAD_COMPLETED = 2;
	private final Activity activity;
	private String targetFile;
	private String targetUrl;
	private String targetPackageName;
	private boolean isDownloadSucceed = false;
	private final String PACKAGE = "package:";

	public ProgressDialog pBar;
	private ApkFileDownloadingListener listener;

	public interface ApkFileDownloadingListener {
		public void onNetworkFailed();
		public void onInstallFailed();
	}

	OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};

	private final Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case DOWNLOAD_STARTED:
					pBar.setMax(fileSize);
					break;
				case DOWNLOAD_PROCESSING:
					pBar.setProgress(downLoadFileSize);
					int result = downLoadFileSize * 100 / fileSize;
					pBar.setMessage(result + "%");
					break;
				case DOWNLOAD_COMPLETED:
					if (null != activity) {
						Toast.makeText(
								activity,
								activity.getString(R.string.download_complete_msg),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								GlobalVariable.g_currentRunningActivity,
								GlobalVariable.g_currentRunningActivity.getString(R.string.download_complete_msg),
								Toast.LENGTH_LONG).show();
					}
					pBar.dismiss();
					break;
				case -1:
					if (null != activity) {
						Toast.makeText(
								activity,
								activity.getString(R.string.download_error_msg),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								GlobalVariable.g_currentRunningActivity,
								GlobalVariable.g_currentRunningActivity.getString(R.string.download_error_msg),
								Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
			return false;
		}
	});

	private int fileSize;

	private int downLoadFileSize;

	public DownloadApkFileManager(String url, String packagename, Activity activity, ApkFileDownloadingListener listener) {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		this.targetFile = Environment.getExternalStorageDirectory().getPath() + File.separator + filename;
		this.targetUrl = url;
		this.targetPackageName = packagename;
		this.activity = activity;
		this.listener = listener;
	}

	public DownloadApkFileManager(String url, String packagename, ApkFileDownloadingListener listener) {
		String filename = url.substring(url.lastIndexOf("/") + 1);
		this.targetFile = Environment.getExternalStorageDirectory().getPath() + File.separator + filename;
		this.targetUrl = url;
		this.targetPackageName = packagename;
		this.activity = null;
		this.listener = listener;
	}

	public void downloadApkFile() {
		this.prepareToDownload();
	}

	private void prepareToDownload() {
		if (null != activity) {
			pBar = new ProgressDialog(this.activity);
			pBar.setOnKeyListener(keylistener);
			pBar.setMessage(this.activity.getString(R.string.download_please_waiting));
		} else {
			pBar = new ProgressDialog(GlobalVariable.g_currentRunningActivity);
			pBar.setMessage(GlobalVariable.g_currentRunningActivity.getString(R.string.download_please_waiting));
		}
		pBar.setTitle(R.string.downloading_msg);

		pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		beginDownloading();
	}

	private void beginDownloading() {
		pBar.show();
		new Thread() {
			@Override
			public void run() {
				try {
					isDownloadSucceed = download();
				} catch (Exception e) {
					listener.onNetworkFailed();
					Log.e(TAG, e.getMessage(), e);
				}
				if (isDownloadSucceed) {
					install();
				} else {
					pBar.cancel();
					Looper.prepare();
					if (null != activity) {
						Toast.makeText(
								activity,
								activity.getString(R.string.download_error_msg),
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								GlobalVariable.g_currentRunningActivity,
								GlobalVariable.g_currentRunningActivity.getString(R.string.download_error_msg),
								Toast.LENGTH_SHORT).show();
					}
					Looper.loop();
				}
			}

		}.start();
	}

	private boolean download() {
		isDownloadSucceed = false;
		try {
			// 构造URL
			URL url = new URL(targetUrl);
			Log.i(TAG, targetUrl);
			// 打开连接
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);
			conn.setRequestProperty("Accept-Encoding", "identity");
			//获得文件的长度
			fileSize = conn.getContentLength();
			InputStream is = conn.getInputStream();
			File outFile = new File(targetFile);
			OutputStream outstream = new FileOutputStream(outFile);
			if (outstream != null && is != null) {
				byte[] buffer = new byte[1024];
				int size = is.read(buffer, 0, 1024);
				downLoadFileSize = 0;
				sendMsg(DOWNLOAD_STARTED);
				while (size > 0) {
					outstream.write(buffer, 0, size);
					size = is.read(buffer);
					downLoadFileSize += size;

					sendMsg(DOWNLOAD_PROCESSING);// 更新进度条
				}
				sendMsg(DOWNLOAD_COMPLETED);// 通知下载完成
			}
			outstream.close();
			is.close();
			isDownloadSucceed = true;
		} catch (Exception e) {
			listener.onNetworkFailed();
			Log.e(TAG, e.getMessage(), e);
		}
		return isDownloadSucceed;
	}

	private void install() {

		String packageName = PACKAGE + targetPackageName;

		File apkFile = new File(targetFile);
		if (apkFile != null && apkFile.exists()) {
			installPackage(apkFile, packageName);
		} else {
			listener.onInstallFailed();
		}
	}

	private void installPackage(File apkFile, String packageName) {

		try {
			Uri packageURI = Uri.parse(packageName);
			Intent i = new Intent(Intent.ACTION_VIEW, packageURI);
			i.setDataAndType(Uri.fromFile(apkFile),	"application/vnd.android.package-archive");
			if (null != activity) {
				activity.startActivity(i);
			} else {
				GlobalVariable.g_currentRunningActivity.startActivity(i);
			}
			pBar.dismiss();
		} catch (Exception e) {
			listener.onInstallFailed();
			Log.e(TAG, e.getMessage(), e);
		}
	}

//	private void uninstallPackage(String packageName) {
//		Uri packageURI = Uri.parse(packageName);
//		Intent i = new Intent(Intent.ACTION_DELETE, packageURI);
//		if (null != activity) {
//			activity.startActivity(i);
//		} else {
//			GlobalVariable.g_currentRunningActivity.startActivity(i);
//		}
//	}

	private void sendMsg(int flag) {
		Message msg = new Message();
		msg.what = flag;
		handler.sendMessage(msg);
	}

}
