package mali.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolsUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(ToolsUtil.class);

	public static final Pattern checkIP = Pattern
			.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(\\.[0-9]{1,3})?)");
	public static final Pattern checkDomain = Pattern
			.compile("[a-zA-Z0-9\\._-]+(\\.[a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})");
	public static final Pattern checkUrl = Pattern
			.compile("((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}(\\.[0-9]{1,3})?))(:[0-9]{1,5})*(/?[a-zA-Z0-9\\&%_\\./-~-]*)?");

	/**
	 * 获取请求地址的domain.
	 * 
	 * @param r
	 * @return
	 */
	public static String getDomain(String url) {
		String domain = null;
		if (!(url == null || url.equals(""))) {
			Matcher m = checkUrl.matcher(url);
			if (m.matches()) {
				domain = m.group(3);
				m = checkIP.matcher(domain);
				if (!m.matches()) {
					m = checkDomain.matcher(domain);
					if (m.matches()) {
						domain = m.group(1);
					}
				}
			}
		}
		return domain;
	}

	/**
	 * 判断是否是ajax请求.
	 * 
	 * @param r
	 * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest r) {
		// String type = r.getContentType();
		String requestedWith = r.getHeader("x-requested-with");
		if (requestedWith != null && "XMLHttpRequest".equals(requestedWith)
		// && type != null
		// && type.startsWith("application/x-www-form-urlencoded")
		) {
			return true;
		}
		return false;
	}

	/**
	 * 隐藏手机号.
	 * 
	 * @param src
	 * @return
	 */
	public static String hidePhoneNo(String src) {
		int to = 9;
		if (src == null) {
			return null;
		}
		if (to > src.length()) {
			to = src.length();
		}
		return StringUtil.hideString(src, 6, 2, to);
	}

	/**
	 * 隐藏用户名.
	 * 
	 * @param src
	 * @return
	 */
	public static String hideUserName(String src) {
		if (src == null || src.equals("")) {
			return src;
		}
		return StringUtil.hideString(src, 2, 0, src.length());
	}

	/**
	 * 隐藏银行卡号.
	 * 
	 * @param src
	 * @return
	 */
	public static String hideCardId(String src) {
		if (src == null || src.equals("")) {
			return src;
		}
		return StringUtil.hideString(src, 10, 3, src.length() - 4);
	}

	/**
	 * 构造返回的map,接口使用
	 * 
	 * @param cde
	 * @param msg
	 * @return
	 */
	public static Map<String, Object> createRestMap(Integer cde, String msg) {
		Map<String, Object> restMap = new HashMap<String, Object>();
		restMap.put("c", cde);
		restMap.put("m", msg);
		return restMap;
	}

	/**
	 * 构造返回的map,接口使用
	 * 
	 * @param cde
	 * @param msg
	 * @return
	 */
	public static Map<String, Object> createRestMap(String cde, String msg) {
		Map<String, Object> restMap = new HashMap<String, Object>();
		restMap.put("c", cde);
		restMap.put("m", msg);
		return restMap;
	}

	/**
	 * 校验银行卡卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId
				.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null
				|| nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	// public static <T extends Object> PageModel<T> copyPageModel(PageModel<?>
	// src, Class<T> targetClass) {
	// if (src == null) {
	// throw new IllegalArgumentException("copyPageModel src is null.");
	// }
	//
	// PageModel<T> resultPModel = new PageModel<T>();
	//
	// resultPModel.setPageNo(src.getPageNo());
	// resultPModel.setPageSize(src.getPageSize());
	// resultPModel.setTotal(src.getTotal());
	//
	// resultPModel.setRows(copyList(src.getRows(), targetClass));
	//
	// return resultPModel;
	// }

	/**
	 * 集合拷贝.
	 * 
	 * @param src
	 * @param targetClass
	 * @return
	 */
	public static <T extends Object> List<T> copyList(List<?> src,
			Class<T> targetClass) {

		if (src == null) {
			throw new IllegalArgumentException("copyList src is null.");
		}
		List<T> restList = new ArrayList<T>();

		for (Object o : src) {
			try {
				T t = targetClass.newInstance();
				getPropertyUtils().copyProperties(t, o);
				restList.add(t);
			} catch (Exception e) {
				logger.error("copyList(List<?>, Class<T>) - exception ignored",
						e);
			}
		}

		return restList;
	}

	private static PropertyUtilsBean getPropertyUtils() {
		return new PropertyUtilsBean();
	}

	private static ConvertUtilsBean getConvertUtils() {
		return new ConvertUtilsBean();
	}

	private static String getProperty(Object bean, String name, Class<?> type)
			throws IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		Object value = getPropertyUtils().getNestedProperty(bean, name);
		if (value == null)
			return "";
		return getConvertUtils().convert(
				getConvertUtils().convert(value.toString(), type));

	}

	/**
	 * 提取描述信息.
	 * 
	 * @param bean
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public static Map<String, String> describe(Object bean)
			throws IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		if (bean == null) {
			return (new java.util.HashMap<String, String>());
		}
		// PropertyUtilsBean propertyUtilsBean =
		// BeanUtilsBean.getInstance().getPropertyUtils();

		Map<String, String> description = new HashMap<String, String>();
		if (bean instanceof DynaBean) {
			DynaProperty descriptors[] = ((DynaBean) bean).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				Class<?> clazz = descriptors[i].getType();
				description.put(name, getProperty(bean, name, clazz));
			}
		} else {
			PropertyDescriptor descriptors[] = getPropertyUtils()
					.getPropertyDescriptors(bean);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (descriptors[i].getReadMethod() != null)
					description.put(
							name,
							getProperty(bean, name,
									descriptors[i].getPropertyType()));
			}
		}
		return (description);
	}

}
