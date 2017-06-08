package mali.core.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.common.dao.model.PO;
import com.common.web.PageResultModel;

public class BeanUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(BeanUtil.class);

	public static <T extends Serializable> PageResultModel<T> copyPageModel(
			PageResultModel<?> src, Class<T> targetClass) {
		if (src == null) {
			throw new IllegalArgumentException("copyPageModel src is null.");
		}
		if (targetClass == null) {
			throw new IllegalArgumentException(
					"copyPageModel targetClass is null.");
		}
		PageResultModel<T> resultPModel = new PageResultModel<T>();
		resultPModel.setPageNo(src.getPageNo());
		resultPModel.setPageSize(src.getPageSize());
		resultPModel.setTotal(src.getTotal());
		if (src.getRows() != null)
			resultPModel.setRows(copyList(src.getRows(), targetClass));
		return resultPModel;
	}

	public static <T extends Object> List<T> copyList(List<?> src,
			Class<T> targetClass) {
		if (src == null) {
			throw new IllegalArgumentException("copyList src is null.");
		}
		if (targetClass == null) {
			throw new IllegalArgumentException("copyList targetClass is null.");
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

	static public <T> T po2dto(PO po, Class<T> dtoClass) {
		try {
			T t = dtoClass.newInstance();
			getPropertyUtils().copyProperties(t, po);
			return t;
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				throw new RuntimeException(e.getCause());
			}
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	static public <T extends PO> T dto2po(Serializable serializable,
			Class<T> poClass) {
		try {
			T t = poClass.newInstance();
			getPropertyUtils().copyProperties(t, serializable);
			return t;
		} catch (Exception e) {
			if (e instanceof InvocationTargetException) {
				throw new RuntimeException(e.getCause());
			}
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
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

	public static Map<String, String> describe(Object bean)
			throws IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		if (bean == null) {
			return (new HashMap<String, String>());
		}

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
