package mali.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mali.core.entity.Constant;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class StringUtil {

	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	public static final ObjectMapper JSON_MAPPER_NOTNULL = new ObjectMapper();

	private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*\"?([^\\s;\"]*)");
	private static String userAgent = Constant.USER_AGENT;

	static {
		JSON_MAPPER_NOTNULL.setSerializationInclusion(Include.NON_NULL);
		// SimpleModule module = new SimpleModule();
		// module.addSerializer(CodeDescEnum.class, new CodeEnumSerializer());
		// JSON_MAPPER_NOTNULL.registerModule(module);
		JSON_MAPPER_NOTNULL.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		JSON_MAPPER_NOTNULL.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		JSON_MAPPER_NOTNULL.setLocale(Locale.getDefault());
	}

	public static String md5Str(InputStream ins) throws NoSuchAlgorithmException, IOException {
		return bytesToHex(md5(ins));
	}

	public static String md5Str(byte[] data) throws NoSuchAlgorithmException {
		return bytesToHex(md5(data));
	}

	public static byte[] md5(InputStream ins) throws NoSuchAlgorithmException, IOException {
		java.security.MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] buf = new byte[1024 * 16];
		while (true) {
			int readLen = ins.read(buf);
			if (readLen == -1) {
				break;
			}
			md5.update(buf, 0, readLen);

		}
		return md5.digest();
	}

	public static byte[] md5(byte[] data) throws NoSuchAlgorithmException {
		java.security.MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	public static String bytesToHex(byte bytes[]) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(convertDigit(bytes[i] >> 4));
			sb.append(convertDigit(bytes[i] & 0x0f));
		}
		return (sb.toString());

	}

	private static char convertDigit(int value) {
		value &= 0x0f;
		if (value >= 10) {
			return ((char) (value - 10 + 'a'));
		} else {
			return ((char) (value + '0'));
		}
	}

	public static Document getJsoupDocument(String url) throws IOException {
		Connection con = Jsoup.connect(url).userAgent(userAgent);
		con.request().timeout(10000);
		Document doc = con.get();
		return doc;
	}

	public static Document getJsoupDocument(String url, IDocumentPrepared dp) throws IOException {
		Connection con = Jsoup.connect(url).userAgent(userAgent);
		con.request().timeout(10000);
		Document doc = con.get();
		if (dp != null) {
			Charset charset = doc.outputSettings().charset();
			String modifyStr = dp.prepare(con.response().body());
			doc = Jsoup.parse(modifyStr, doc.baseUri());
			doc.outputSettings().charset(charset);
		}
		return doc;
	}

	public static Document getJsoupDocumentWithProxy(String targetUrl, String ip, int port) throws IOException {
		return getJsoupDocumentWithProxy(targetUrl, ip, port, null);
	}

	public static Document getJsoupDocumentWithProxy(String targetUrl, String ip, int port, IDocumentPrepared dp) throws IOException {
		HttpURLConnection uc = null;
		InputStream inputStream = null;
		try {
			InetSocketAddress addr = new InetSocketAddress(ip, port);
			URL url = new URL(targetUrl);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			uc = (HttpURLConnection) url.openConnection(proxy);
			uc.setDoOutput(true);
			uc.setRequestProperty("User-agent", userAgent);
			uc.setRequestMethod("GET");
			uc.setReadTimeout(5000);
			uc.connect();
			// int code = uc.getResponseCode();
			// if (code == 302 || code == 301 || code == 303) {
			// String newUrl = uc.getHeaderField("location");
			// if (newUrl == null || newUrl.length() == 0) {
			// throw new IOException(
			// "Redirecting failed: header(location) can't be found!");
			// }
			// return getJsoupDocumentWithProxy(newUrl, ip, port);
			// }

			String charset = getCharsetFromContentType(uc.getContentType());

			inputStream = uc.getInputStream();

			ByteArrayOutputStream bops = new ByteArrayOutputStream();
			int count = 0;
			byte[] b = new byte[1024];
			while ((count = inputStream.read(b)) != -1) {
				bops.write(b, 0, count);
			}

			Document doc = parseDoc(bops.toByteArray(), 0, bops.size(), charset, targetUrl, dp);

			bops.close();
			// Document doc = Jsoup.parse(new String(bops.toByteArray(), 0,
			// bops.size(), charset), targetUrl);

			return doc;
		} catch (Exception e) {
			logger.error("Error occurs in method: getJsoupDocumentWithProxy, " + e.getMessage(), e);
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
			if (uc != null) {
				try {
					uc.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 纠正规则，将空格转换为%20,而不是+
	 * 
	 * @param content
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeUrl(String content, String encoding) throws UnsupportedEncodingException {
		String v = URLEncoder.encode(content, encoding);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < v.length(); i++) {
			char ch = v.charAt(i);
			switch (ch) {
				case '+':
					sb.append("%20");
					break;
				default:
					sb.append(ch);
					break;
			}
		}
		return sb.toString();
	}

	private static Document parseDoc(byte[] byteData, int offset, int size, String charset, String baseUrl, IDocumentPrepared dp) throws UnsupportedEncodingException {
		String docData = null;
		Document doc = null;
		if (charset == null) {
			docData = new String(byteData, 0, size, Constant.CHARSET_NAME_UTF8);
			doc = Jsoup.parse(docData, baseUrl);
			Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
			if (meta != null) { // if not found, will keep utf-8 as best attempt
				String foundCharset = meta.hasAttr("http-equiv") ? getCharsetFromContentType(meta.attr("content")) : meta.attr("charset");
				if (foundCharset != null && foundCharset.length() != 0 && !foundCharset.equals(Constant.CHARSET_NAME_UTF8)) { // need
					// to
					// re-decode
					charset = foundCharset;
					// byteData.rewind();
					docData = new String(byteData, 0, size, charset);
					doc = null;
				}
			}
		} else {
			docData = new String(byteData, 0, size, charset);
		}

		if (doc == null) {
			doc = Jsoup.parse(docData, baseUrl);
			doc.outputSettings().charset(charset);
		}

		if (dp != null) {
			Charset charset1 = doc.outputSettings().charset();
			String modifyStr = dp.prepare(docData);
			doc = Jsoup.parse(modifyStr, doc.baseUri());
			doc.outputSettings().charset(charset1);
		}

		return doc;
	}

	public static long getLongValue(String s, long defaultValue) {
		if (s == null) {
			return defaultValue;
		}

		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getFloatValue(String s, float defaultValue) {
		if (s == null) {
			return defaultValue;
		}

		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static short getShortValue(String s, short defaultValue) {
		if (s == null) {
			return defaultValue;
		}

		try {
			return Short.parseShort(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double getDoubleValue(String s, double defaultValue) {
		if (s == null) {
			return defaultValue;
		}

		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int getIntValue(String s, int defaultValue) {
		if (s == null || s.length() == 0) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static boolean getBooleanValue(String s, boolean defaultValue) {
		if (s == null) {
			return defaultValue;
		}

		try {
			return Boolean.parseBoolean(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int getPageCount(int total, int pageSize) {
		if (total % pageSize == 0) {
			return total / pageSize;
		} else {
			return (total / pageSize) + 1;
		}
	}

	static String getCharsetFromContentType(String contentType) {
		if (contentType == null)
			return null;

		Matcher m = charsetPattern.matcher(contentType);
		if (m.find()) {
			return m.group(1).trim().toUpperCase();
		}
		return null;
	}

	public static boolean isEmpty(Object o) {
		if (o instanceof String) {
			return o == null || o.toString().equals("");
		} else {
			return o == null;
		}
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	private static final String integer_type = "(-?[1-9]\\d*)|0";

	public static boolean isInteger(String str) {
		return isNotEmpty(str) && str.matches(integer_type);
	}

	public static boolean isNotInteger(String str) {
		return !isInteger(str);
	}

	/**
	 * 构建全站跟踪字符串。
	 * 
	 * @param app 应用类型
	 * @param channel 渠道号
	 * @param resource 渠道范围内的资源
	 */
	public static String getTraceString(String app, String channel, String resource) {
		return app + "_" + channel + "_" + resource;
	}

	/**
	 * cover parts of the src with '*'
	 * 
	 * @param src
	 * @param starNum number of the '*'
	 * @param start the start position(include),start with 0.
	 * @param end the end position(include).
	 * @return
	 * 
	 *         for example: hideString("15821442258",6,2,9) the result is 158******58 add by forgkan
	 */
	public static String hideString(String src, int starNum, int start, int end) {
		if (isEmpty(src)) {
			return src;
		}

		if (starNum < 0 || start < 0 || start >= src.length() || end < start || end > src.length()) {
			throw new IllegalArgumentException("Invalid paramter.");
		}

		StringBuilder stars = new StringBuilder();
		for (int i = 0; i < starNum; i++) {
			stars.append("*");
		}

		return src.substring(0, start + 1) + stars.toString() + src.substring(end);
	}

	/**
	 * object字符串转json
	 * 
	 * @param obj
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String trans2Json(Object obj) throws IOException {
		return JSON_MAPPER.writeValueAsString(obj);
	}

	/**
	 * Json字符串转object
	 * 
	 * @param content
	 * @param type
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T extends Object> T trans2Object(String content, Class<T> type) throws IOException {
		return JSON_MAPPER.readValue(content, type);
	}

	/**
	 * 
	 * @param obj
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String trans2JsonWithoutNull(Object obj) throws IOException {
		return JSON_MAPPER_NOTNULL.writeValueAsString(obj);
	}

	/**
	 * 转json字符串，转换异常返回
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj) {
		try {
			return JSON_MAPPER_NOTNULL.writeValueAsString(obj);
		} catch (Exception e) {
			throw new IllegalArgumentException("illegal json String.");
		}
	}

}
