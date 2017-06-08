package mali.core.util;

import java.net.URLEncoder;

import mali.core.entity.Constant;

import org.apache.commons.lang.StringUtils;

/**
 * 视图层工具类
 * 
 * @author andyfang
 */
public class ViewUtil {

	public static String getServiceContext() {
		return System.getProperty("service_context");
	}

	/**
	 * 先截取,再转义
	 * 
	 * @param str 要转义的字符
	 * @param length 截取字符长度
	 */
	public static String trimAndEscape(String str, int length) {
		return trimAndEscape(str, length, true);
	}

	public static String trimAndEscape(String str, int length, boolean isAddSuffix) {
		String tmp = truncate(str, length, isAddSuffix);
		if (tmp == null) {
			return tmp;
		} else {
			return htmlSpecialReplace(tmp, false);
		}
	}

	public static String truncate(String str, int limit) {
		return truncate(str, limit, true);
	}

	/**
	 * 按字节截断文本。
	 * 
	 * @param str 需要处理的字符串
	 * @param limit 最大字节长度
	 * @param addShenglu true：截断后添加自定义尾部文本，默认为"..." ,false:如果是刚好等于最大长度则截取到最大长度;否则否则截取到前一个字符
	 * @return 截断后的文本。最后一个字符
	 */
	public static String truncate(String str, int limit, boolean addShenglu) {
		limit--; // 省略号占两个字节,需要先减一减免显示时不全
		try {
			// a.nothing
			if (str == null) {
				return "";
			}
			// b.within the change(more possible)
			else if (str.getBytes(Constant.GBK).length <= limit) {
				return str;
			}
			// c.over the limit
			else {
				int count = 0;
				int i = 0;
				for (; i < str.length(); i++) {
					String tmp = str.substring(i, i + 1);
					count += tmp.getBytes(Constant.GBK).length;
					if (count >= limit) { // 循环必将在此退出
						break;
					}
				}
				if (addShenglu)
					return str.substring(0, i) + SHENGLU;
				else if (count == limit)
					return str.substring(0, (i + 1)); // NOTE:不加省略号并且是刚好等于限制时截取到最长(**新鲜土豆中播客昵称需求**)
				else
					return str.substring(0, i); // 截取限制长的前一个字符
			}
		} catch (Exception e) {
			return "";
		}
	}

	public static String ecapseHtml(String content) {
		return htmlSpecialReplace(content, false);
	}

	public static String htmlSpecialReplace(String content, boolean isWrapBr) {
		if (StringUtils.isBlank(content))
			return "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < content.length(); i++) {
			char ch = content.charAt(i);
			switch (ch) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				default:
					sb.append(ch);
					break;
			}
		}
		if (isWrapBr) {
			return nl2br(sb.toString());
		} else {
			return sb.toString();
		}
	}

	public static String nl2br(String content) {
		String tmp = content.replace("\n\r", "<br/>");
		tmp = tmp.replace("\n", "<br/>");
		return tmp;
	}

	/**
	 * 该方法用于转义网站的url特殊关键字。 对于URL而言，字符'-','/'都不能出现，对于网页内容而言'<','>','&','"'都不能出现。
	 */
	public static String escapeParameter(String p) {
		if (p == null || p.length() == 0) {
			return p;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < p.length(); i++) {
			char ch = p.charAt(i);
			switch (ch) {
				case '&':
					sb.append("&amp;");
					break;
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '-':
				case '/':
					break;
				default:
					sb.append(ch);
					break;
			}
		}

		return sb.toString();
	}

	public static String encodeUrl(String url) {
		return encodeUrl(url, Constant.CHARSET_NAME_UTF8);
	}

	public static String encodeUrl(String url, String encoding) {
		try {
			url = URLEncoder.encode(url, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return url;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < url.length(); i++) {
			char ch = url.charAt(i);
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

	public static String encodeToUtf(String content) {
		return encodeParameter(content, Constant.CHARSET_NAME_UTF8);
	}

	/**
	 * 将指定的内容进行URL编码，适用于网页,主要针对url中的关键字内容进行处理
	 */
	public static String encodeParameter(String content, String encoding) {
		String v = escapeKeyword(content);
		try {
			v = URLEncoder.encode(v, encoding);
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		}

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

	protected static String getSuffix(String url) {
		int index = url.lastIndexOf(".");
		if (index > -1 && (index + 1) < url.length()) {
			return url.substring(index + 1);
		} else {
			return null;
		}

	}

	private static String escapeKeyword(String p) {
		if (p == null || p.length() == 0) {
			return p;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < p.length(); i++) {
			char ch = p.charAt(i);
			switch (ch) {
				case '-':
				case '/':
					break;
				default:
					sb.append(ch);
					break;
			}
		}

		return sb.toString();
	}

	private static final String SHENGLU = "..."; // 超过长度时后缀处理
}
