package life.leek.launcher.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import life.leek.launcher.R;
import life.leek.launcher.setting.Setting;
import life.leek.launcher.utils.CommonUtil;
import life.leek.launcher.utils.NetworkUtil;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 自动下载更新apk服务 
 * Create by: ls_gao 
 * Date: 2017-06-20 time: 12:58
 * 
 */
public class DownloadApkIntentService extends IntentService {

	public DownloadApkIntentService() {
		super("");
	}
	public DownloadApkIntentService(String name) {
		super(name);
	}

	public final static String TAG = "DownloadApkIntentService";

	private static final int DOWNLOAD_ERROR = -1;
	private static final int DOWNLOAD_STARTED = 0;
	private static final int DOWNLOAD_PROCESSING = 1;
	private static final int DOWNLOAD_COMPLETED = 2;

	private String mDownloadUrl;// APK的下载路径
	private String mCurrVersion;// 当前版本号
	private String mNewestVersion;// 最新版本号
//	private NotificationManager mNotificationManager;
//	private Notification mNotification;

	@SuppressWarnings("unused")
	private int mFileSize;
	@SuppressWarnings("unused")
	private int mDownLoadFileSize;
	private String mFileName;
	private String mPackageName;
	private String mMainActivityName;

	private final Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case DOWNLOAD_STARTED:
					break;
				case DOWNLOAD_PROCESSING:
//					int progress = mDownLoadFileSize  * 100 / mFileSize;
//					// progress*100为当前文件下载进度，total为文件大小
//					if ((int) (progress) % 10 == 0) {
//						// 避免频繁刷新View，这里设置每下载10%提醒更新一次进度
//						notifyMsg("温馨提醒", "文件正在下载..", (int) (progress));
//					}
					break;
				case DOWNLOAD_COMPLETED:
//					notifyMsg("温馨提醒", "文件下载已完成", 100);
					showTip(getString(R.string.upgrade_tip));
					String apk_absolute_path = Environment.getExternalStorageDirectory().getPath() + File.separator + mFileName;
					CommonUtil.silentInstall(apk_absolute_path, mPackageName, mMainActivityName);
//					stopSelf();					
					break;
				case DOWNLOAD_ERROR:
//					notifyMsg("温馨提醒", "文件下载失败", 0);
					stopSelf();
					break;
				default:
					break;
				}
			}
			return false;
		}
	});

	@Override
	protected void onHandleIntent(Intent intent) {
//		mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

		if (intent == null) {
//			notifyMsg("温馨提醒", "文件下载失败", 0);
			stopSelf();
			return;
		}

		while (!NetworkUtil.isWifiConnected(this)) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}
		mDownloadUrl = intent.getStringExtra(Setting.KEY_URL);// 获取下载APK的链接
		String newVersonUrl = intent.getStringExtra(Setting.KEY_VERSION_URL);
		mNewestVersion = NetworkUtil.getContentsFromUrlEx(newVersonUrl);
		mCurrVersion = intent.getStringExtra(Setting.KEY_CURRENT_VERSION);
		mPackageName = intent.getStringExtra(Setting.KEY_PACKAGE_NAME);
		mMainActivityName = intent.getStringExtra(Setting.KEY_MAIN_ACTIVITY);
		if (mCurrVersion.compareTo(mNewestVersion) < 0) {
			download(mDownloadUrl);// 下载APK
		}
	}


//	private void notifyMsg(String title, String content, int progress) {
//
//		Builder builder = new Builder(this);// 为了向下兼容，这里采用了v7包下的NotificationCompat来构造
//		builder.setSmallIcon(R.drawable.ic_launcher)
//				.setLargeIcon(
//						BitmapFactory.decodeResource(getResources(),
//								R.drawable.ic_launcher)).setContentTitle(title);
//		if (progress > 0 && progress < 100) {
//			// 下载进行中
//			builder.setProgress(100, progress, false);
//		} else {
//			builder.setProgress(0, 0, false);
//		}
//		builder.setAutoCancel(true);
//		builder.setWhen(System.currentTimeMillis());
//		builder.setContentText(content);
//		if (progress >= 100) {
//			// 下载完成
//			builder.setContentIntent(getInstallIntent());
//		}
//		mNotification = builder.build();
//		mNotificationManager.notify(0, mNotification);
//
//	}
//
//	/**
//	 * 安装apk文件
//	 * 
//	 * @return
//	 */
//	private PendingIntent getInstallIntent() {
//		File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + mFileName);
//		Intent intent = new Intent(Intent.ACTION_VIEW);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setDataAndType(Uri.parse("file://" + file.getAbsolutePath()),
//				"application/vnd.android.package-archive");
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		return pendingIntent;
//	}

	private boolean download(String file_url) {
		boolean download_result = false;
		if (null == file_url) {
			sendMsg(DOWNLOAD_ERROR);
			return false;
		}
		int pos = file_url.lastIndexOf(File.separator);
		if (pos < 0) {
			sendMsg(DOWNLOAD_ERROR);
			return false;
		}

		mFileName = file_url.substring(pos + 1);
		String targetUrl = file_url;
		try {
			// 构造URL
			URL url = new URL(targetUrl);
			Log.i(TAG, targetUrl);
			// 打开连接
			URLConnection conn = url.openConnection();
			conn.setConnectTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);
			conn.setRequestProperty("Accept-Encoding", "identity");
			// 获得文件的长度
			mFileSize = conn.getContentLength();
			InputStream is = conn.getInputStream();
			String targetFile = Environment.getExternalStorageDirectory().getPath() + File.separator + mFileName;
			File outFile = new File(targetFile);
			OutputStream outstream = new FileOutputStream(outFile);
			mDownLoadFileSize = 0;
			if (outstream != null && is != null) {
				byte[] buffer = new byte[1024];
				int size = is.read(buffer, 0, 1024);
				mDownLoadFileSize = 0;
				sendMsg(DOWNLOAD_STARTED);
				while (size > 0) {
					outstream.write(buffer, 0, size);
					size = is.read(buffer);
					mDownLoadFileSize += size;

					//sendMsg(DOWNLOAD_PROCESSING);// 更新进度条
				}
				sendMsg(DOWNLOAD_COMPLETED);// 通知下载完成
			}
			outstream.close();
			is.close();
			download_result = true;
		} catch (Exception e) {
			sendMsg(DOWNLOAD_ERROR);
			Log.e(TAG, e.getMessage(), e);
		}
		return download_result;
	}

	private void sendMsg(int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	private void showTip(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
