package mali.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 
 * @author andyfang
 */
public class PropertiesUtil extends PropertyPlaceholderConfigurer implements
		Map<String, String> {

	private static final Logger logger = LoggerFactory
			.getLogger(PropertiesUtil.class);

	private static Map<String, String> ctxPropertiesMap;

	public PropertiesUtil() {
	}

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		if (ctxPropertiesMap != null) {
			logger.warn("The property map will be override!");
		}
		ctxPropertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			String value = props.getProperty(keyStr);
			ctxPropertiesMap.put(keyStr, value);
		}
	}

	public static String getString(String name) {
		if (ctxPropertiesMap == null) {
			ctxPropertiesMap = new HashMap<String, String>();
		}
		return ctxPropertiesMap.get(name);
	}

	public static String getString(String name, String defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		return v;
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Boolean.parseBoolean(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int getIntValue(String name, int defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long getLongValue(String name, long defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Long.parseLong(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static short getShortValue(String name, short defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Short.parseShort(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double getDoubleValue(String name, double defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getFloatValue(String name, float defaultValue) {
		String v = getString(name);
		if (v == null || v.trim().equals("")) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(v);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	@Override
	public int size() {
		return ctxPropertiesMap.size();
	}

	@Override
	public boolean isEmpty() {
		return ctxPropertiesMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return ctxPropertiesMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String put(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String get(Object key) {
		return ctxPropertiesMap.get(key);
	}
}
