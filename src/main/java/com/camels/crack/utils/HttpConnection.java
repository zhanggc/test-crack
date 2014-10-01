package com.camels.crack.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

public class HttpConnection {
	static String BOUNDARY = java.util.UUID.randomUUID().toString();
	static String PREFIX = "--", LINEND = "\r\n";
	static String MULTIPART_FROM_DATA = "multipart/form-data";
	static String CHARSET = "UTF-8";

	public static void upLoadImage(String url, Map<String, String> params,
			File file) throws IOException {
		HttpURLConnection conn = getConnection(url);
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +
				 ";boundary=" + BOUNDARY);
		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());

		// 首先组拼文本类型的参数
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=\"" + CHARSET + "\""
					+ LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND).append(entry.getValue()).append(LINEND);
		}
		outStream.write(sb.toString().getBytes());

		// 发送文件数据
		sb = new StringBuilder();
		sb.append("--").append(BOUNDARY).append("\r\n");
		sb.append("Content-Disposition: form-data; name=\"fvo.file\"; filename=\""
				+ file.getName() + "\"\r\n");
		sb.append("Content-Type: application/octet-stream\r\n\r\n");

		outStream.write(sb.toString().getBytes());
		InputStream is = new FileInputStream(file);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		is.close();
		outStream.write(LINEND.getBytes());
		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();

		outStream.write(end_data);
		outStream.flush();
		// 得到响应码
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		if (res == 200) {
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
			Logger.getAnonymousLogger().info(sb2.toString());
		}
		outStream.close();
		conn.disconnect();
	}

	public static HttpURLConnection getConnection(String url)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
//		conn.setReadTimeout(8 * 1000); // 缓存的最长时间
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("GET");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		return conn;
	}

	public static void login(String url, Map<String, String> params)
			throws IOException {
		String values = "";
		HttpURLConnection conn = getConnection(url);
		InputStream in = conn.getInputStream();
		int res = conn.getResponseCode();
		if (res == 200) {
			int ch;
			StringBuilder sb2 = new StringBuilder();
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
			Logger.getAnonymousLogger().info(sb2.toString());
		}

	}
}
