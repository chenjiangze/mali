package mali.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import mali.core.entity.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class RequestUtil {

	private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	private static final String GET = "GET";
	private static final String USER_AGENT = "User-agent";

	public static String getRequest(String u, Charset encoding) {
		if (u == null) {
			throw new RuntimeException("无效的路径");
		}
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;
		ByteArrayOutputStream bops = null;
		try {
			URL url = new URL(u);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDoOutput(true);
			if (!(Constant.USER_AGENT == null || Constant.USER_AGENT.trim().equals("")))
				httpURLConnection.setRequestProperty(USER_AGENT, Constant.USER_AGENT);
			httpURLConnection.setRequestMethod(GET);
			httpURLConnection.setReadTimeout(20000);
			httpURLConnection.connect();

			inputStream = httpURLConnection.getInputStream();

			bops = new ByteArrayOutputStream();
			int count = 0;
			byte[] b = new byte[1024];
			while ((count = inputStream.read(b)) != -1) {
				bops.write(b, 0, count);
			}

			httpURLConnection.disconnect();

			return new String(bops.toByteArray(), 0, bops.size(), encoding);
		} catch (Exception e) {
			logger.error("获取给定的资源失败:url ->" + u, e);
			throw new RuntimeException("获取给定的资源失败:" + e.getMessage() + ", url -> " + u, e);
		} finally {
			if (bops != null) {
				try {
					bops.flush();
					bops.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public static boolean isDebug = false;

	public static String getIp(HttpServletRequest request) {
		if (isDebug) {
			printHeaders(request);
		}

		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值
		if (ip != null && ip.length() > 15) {
			int index = ip.indexOf(",");
			if (index > -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}

	public static void printHeaders(HttpServletRequest request) {
		Enumeration<String> headerEnum = request.getHeaderNames();
		System.out.println("--------------header start--------------");
		while (headerEnum.hasMoreElements()) {
			String header = headerEnum.nextElement();
			System.out.println(header + ":" + request.getHeader(header));
		}
		System.out.println("--------------header end--------------");
	}
}
