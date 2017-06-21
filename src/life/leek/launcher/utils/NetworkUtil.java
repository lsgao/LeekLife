package life.leek.launcher.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.InflaterInputStream;

import life.leek.launcher.setting.GlobalVariable;
import life.leek.launcher.setting.Setting;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 测试网络连通性
 * 
 */
public class NetworkUtil {
	public final static String TAG = "NetworkUtil";

	private static NetworkUtil instance = null;

	public static synchronized NetworkUtil getInstance() {
		if (instance == null) {
			instance = new NetworkUtil();
		}
		return instance;
	}

	/**
	 * 测试本地能否ping ip
	 * 
	 * @param ip
	 * @return
	 */
	public boolean isReachIp(String ip) {
		boolean isReach = false;
		try {
			InetAddress address = InetAddress.getByName(ip);// ping this IP

			if (address instanceof java.net.Inet4Address) {
				Log.i(TAG, ip + " is ipv4 address");
			} else if (address instanceof java.net.Inet6Address) {
				Log.i(TAG, ip + " is ipv6 address");
			} else {
				Log.i(TAG, ip + " is unrecongized");
			}
			if (address.isReachable(5000)) {
				isReach = true;
				Log.i(TAG, "SUCCESS - ping " + ip
						+ " with no interface specified");
			} else {
				isReach = false;
				Log.i(TAG, "FAILURE - ping " + ip
						+ " with no interface specified");
			}
		} catch (Exception e) {
			Log.e(TAG, "error occurs:" + e.getMessage());
		}
		return isReach;
	}

	/**
	 * 测试本地所有的网卡地址都能ping通 ip
	 * 
	 * @param ip
	 * @return
	 */
	public boolean isReachNetworkInterfaces(String ip) {
		boolean isReach = false;
		try {
			InetAddress address = InetAddress.getByName(ip);// ping this IP

			if (address instanceof java.net.Inet4Address) {
				Log.i(TAG, ip + " is ipv4 address");
			} else if (address instanceof java.net.Inet6Address) {
				Log.i(TAG, ip + " is ipv6 address");
			} else {
				Log.i(TAG, ip + " is unrecongized");
			}
			if (address.isReachable(5000)) {
				isReach = true;
				Log.i(TAG, "SUCCESS - ping " + ip
						+ " with no interface specified");
			} else {
				isReach = false;
				Log.i(TAG, "FAILURE - ping " + ip
						+ " with no interface specified");
			}
			if (isReach) {
				Log.i(TAG, "-------Trying different interfaces--------");
				Enumeration<NetworkInterface> netInterfaces = NetworkInterface
						.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					NetworkInterface ni = netInterfaces.nextElement();
					Log.i(TAG,
							"Checking interface, DisplayName:"
									+ ni.getDisplayName() + ", Name:"
									+ ni.getName());
					if (address.isReachable(ni, 0, 5000)) {
						isReach = true;
						Log.i(TAG, "SUCCESS - ping " + ip);
					} else {
						isReach = false;
						Log.i(TAG, "FAILURE - ping " + ip);
					}
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						Log.i(TAG, "IP: " + ips.nextElement().getHostAddress());
					}
					Log.i(TAG,
							"-----------------check now NetworkInterface is done--------------------------");
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "error occurs:" + e.getMessage());
		}
		return isReach;
	}

	/**
	 * 获取能与远程主机指定端口建立连接的本机ip地址
	 * 
	 * @param remoteAddr
	 * @param port
	 * @return
	 */
	public String getReachableIP(InetAddress remoteAddr, int port) {
		String retIP = null;
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> localAddrs = ni.getInetAddresses();
				while (localAddrs.hasMoreElements()) {
					InetAddress localAddr = localAddrs.nextElement();
					if (isReachable(localAddr, remoteAddr, port, 5000)) {
						retIP = localAddr.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG,
					"Error occurred while listing all the local network addresses:"
							+ e.getMessage());
		}
		if (retIP == null) {
			Log.i(TAG, "NULL reachable local IP is found!");
		} else {
			Log.i(TAG, "Reachable local IP is found, it is " + retIP);
		}
		return retIP;
	}

	/**
	 * 获取能与远程主机指定端口建立连接的本机ip地址
	 * 
	 * @param remoteIp
	 * @param port
	 * @return
	 */
	public String getReachableIP(String remoteIp, int port) {

		String retIP = null;
		InetAddress remoteAddr = null;
		Enumeration<NetworkInterface> netInterfaces;
		try {
			remoteAddr = InetAddress.getByName(remoteIp);
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> localAddrs = ni.getInetAddresses();
				while (localAddrs.hasMoreElements()) {
					InetAddress localAddr = localAddrs.nextElement();
					if (isReachable(localAddr, remoteAddr, port, 5000)) {
						retIP = localAddr.getHostAddress();
						break;
					}
				}
			}
		} catch (UnknownHostException e) {
			Log.e(TAG,
					"Error occurred while listing all the local network addresses:"
							+ e.getMessage());
		} catch (SocketException e) {
			Log.e(TAG,
					"Error occurred while listing all the local network addresses:"
							+ e.getMessage());
		}
		if (retIP == null) {
			Log.i(TAG, "NULL reachable local IP is found!");
		} else {
			Log.i(TAG, "Reachable local IP is found, it is " + retIP);
		}
		return retIP;
	}

	/**
	 * 测试localInetAddr能否与远程的主机指定端口建立连接相连
	 * 
	 * @param localInetAddr
	 * @param remoteInetAddr
	 * @param port
	 * @param timeout
	 * @return
	 */
	public boolean isReachable(InetAddress localInetAddr,
			InetAddress remoteInetAddr, int port, int timeout) {
		boolean isReachable = false;
		Socket socket = null;
		try {
			socket = new Socket();
			// 端口号设置为 0 表示在本地挑选一个可用端口进行连接
			SocketAddress localSocketAddr = new InetSocketAddress(
					localInetAddr, 0);
			socket.bind(localSocketAddr);
			InetSocketAddress endpointSocketAddr = new InetSocketAddress(
					remoteInetAddr, port);
			socket.connect(endpointSocketAddr, timeout);
			Log.i(TAG, "SUCCESS - connection established! Local: "
					+ localInetAddr.getHostAddress() + " remote: "
					+ remoteInetAddr.getHostAddress() + " port" + port);
			isReachable = true;
		} catch (IOException e) {
			Log.e(TAG,
					"FAILRE - CAN not connect! Local: "
							+ localInetAddr.getHostAddress() + " remote: "
							+ remoteInetAddr.getHostAddress() + " port" + port);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Error occurred while closing socket:"
									+ e.getMessage());
				}
			}
		}
		return isReachable;
	}

	/**
	 * 测试localIp能否与远程的主机指定端口建立连接相连
	 * 
	 * @param localIp
	 * @param remoteIp
	 * @param port
	 * @param timeout
	 * @return
	 */
	public boolean isReachable(String localIp, String remoteIp, int port,
			int timeout) {
		boolean isReachable = false;
		Socket socket = null;
		InetAddress localInetAddr = null;
		InetAddress remoteInetAddr = null;
		try {
			localInetAddr = InetAddress.getByName(localIp);
			remoteInetAddr = InetAddress.getByName(remoteIp);
			socket = new Socket();
			// 端口号设置为 0 表示在本地挑选一个可用端口进行连接
			SocketAddress localSocketAddr = new InetSocketAddress(
					localInetAddr, 0);
			socket.bind(localSocketAddr);
			InetSocketAddress endpointSocketAddr = new InetSocketAddress(
					remoteInetAddr, port);
			socket.connect(endpointSocketAddr, timeout);
			Log.i(TAG, "SUCCESS - connection established! Local: "
					+ localInetAddr.getHostAddress() + " remote: "
					+ remoteInetAddr.getHostAddress() + " port" + port);
			isReachable = true;
		} catch (IOException e) {
			Log.e(TAG,
					"FAILRE - CAN not connect! Local: "
							+ localInetAddr.getHostAddress() + " remote: "
							+ remoteInetAddr.getHostAddress() + " port" + port);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					Log.e(TAG,
							"Error occurred while closing socket:"
									+ e.getMessage());
				}
			}
		}
		return isReachable;
	}

	public static boolean isReachable(String _url) {
		if (!_url.startsWith(Setting.KEY_HTTP)) {
			_url = Setting.KEY_HTTP + _url;
		}
		try {
			URL url = new URL(_url);
			URLConnection conn = url.openConnection();
			// conn.connect();

			conn.setConnectTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);
			conn.setReadTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);

			int status = ((HttpURLConnection) conn).getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return false;
	}

	private static InputStream getInputStreamFromUrl(String _url) {
		if (!_url.startsWith(Setting.KEY_HTTP)) {
			_url = Setting.KEY_HTTP + _url;
		}
		try {
			URL url = new URL(_url);
			URLConnection conn = url.openConnection();
			// conn.connect();

			conn.setConnectTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);
			conn.setReadTimeout(Setting.TIME_HTTP_CONNECTION_TIMEOUT);
			// at com.android.okhttp.internal.http.HttpURLConnectionImpl.getResponseCode(HttpURLConnectionImpl.java:503)
			// "OkHttp ConnectionPool" daemon prio=5 tid=10 TIMED_WAIT
			// "AsyncTask #2" prio=5 tid=20 WAIT
			int status = ((HttpURLConnection) conn).getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				return conn.getInputStream();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return null;
	}

	public static String getZipedContentsFromUrl(String _url) {
		String resultData = "";
		InputStream is = null;
		try {
			is = getInputStreamFromUrl(_url);
			if (is != null) {

				ByteArrayBuffer bab = new ByteArrayBuffer(1024);
				byte[] bs = new byte[1024];
				int len = 0;
				while ((len = is.read(bs)) > 0) {
					bab.append(bs, 0, len);
				}

				// get buffers
				len = bab.length();
				byte[] input = new byte[len + 1];
				System.arraycopy(bab.buffer(), 0, input, 0, len);
				input[len] = 0;

				// inflate zipped data
				ByteArrayInputStream bin = new ByteArrayInputStream(input);
				InputStreamReader infis = new InputStreamReader(new InflaterInputStream(bin), Setting.ENCODING);

				StringBuilder sb = new StringBuilder();

				char[] bs1 = new char[1024];
				while ((len = infis.read(bs1, 0, 1024)) > 0) {
					sb.append(new String(bs1, 0, len));
				}

				infis.close();
				bin.close();

				resultData = sb.toString();
			}

		} catch (IOException e) {
			Log.e(TAG,e.getMessage(), e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		return resultData;
	}

	public static String getContentsFromUrlEx(String _url) {
		String resultData = "";
		InputStream is = null;
		try {
			is = getInputStreamFromUrl(_url);
			if (is != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, Setting.ENCODING));
				StringBuilder sb = new StringBuilder();

				char[] bs = new char[1024];
				int len = 0;
				while ((len = reader.read(bs)) > 0) {
					sb.append(new String(bs, 0, len));
				}
				resultData = sb.toString();
			}

		} catch (IOException e) {
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		return resultData;
	}

	/**
	 * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	 * 
	 * @param url
	 *            Service net address
	 * @param params
	 *            text content
	 * @param files
	 *            pictures
	 * @return String result of Service response
	 * @throws IOException
	 */
	public static String post(String url, Map<String, String> params,
			Map<String, File> files, String filename) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		final String PREFIX = "--";
		final String LINEND = "\r\n";
		final String MULTIPART_FROM_DATA = "multipart/form-data";
		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setConnectTimeout(10 * 1000);
		conn.setReadTimeout(60 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", Setting.ENCODING);

		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +"; boundary=" + BOUNDARY);

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + Setting.ENCODING + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		// 发送文件数据
		if (files != null)
			for (Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\"" + filename + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset=" + Setting.ENCODING + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
			}
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		String result = sb2.toString();
		return result;
	}

	public static void getWifiInfo(Context context) {
		WifiInfo wifiInfo = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
		if (wifiInfo.getBSSID() != null) {
			// wifi名称
			String ssid = wifiInfo.getSSID();
			// wifi信号强度
			int signalLevel = WifiManager.calculateSignalLevel(
					wifiInfo.getRssi(), 5);
			GlobalVariable.g_wifi_level = signalLevel;
			// wifi速度
			int speed = wifiInfo.getLinkSpeed();
			// wifi速度单位
			String units = WifiInfo.LINK_SPEED_UNITS;
			Log.i(TAG, "wifi名称: " + ssid + ", wifi信号强度: " + signalLevel + ", wifi速度: " + speed + ", wifi速度单位" + units);
		}
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiNetworkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}