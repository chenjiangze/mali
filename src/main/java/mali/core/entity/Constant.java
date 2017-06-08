package mali.core.entity;

import java.nio.charset.Charset;

import mali.core.util.PropertiesUtil;

/**
 * 
 * @author andyfang
 */
public interface Constant {

	/** 服务域名 */
	public static final String USER_AGENT_NAME = "USER_AGENT";
	public static final String USER_AGENT = PropertiesUtil.getString(USER_AGENT_NAME, "");

	/** 编码常量 */
	String CHARSET_NAME_GBK = "GBK";
	Charset GBK = Charset.forName(CHARSET_NAME_GBK);
	String CHARSET_NAME_UTF8 = "UTF-8";
	Charset UTF8 = Charset.forName(CHARSET_NAME_UTF8);
	String CHARSET_NAME_ISO = "ISO-8859-1";
	Charset ISO = Charset.forName(CHARSET_NAME_ISO);

	/** 分页查询默每页数量 */
	public static final int DEFAULT_PAGE_SIZE = 20;
	/** 分页查询默认页 */
	public static final int DEFAULT_PAGE_NO = 1;
	/** 升序查询 */
	public static String SQL_SORT_ASC = "ASC";
	/** 降序查询 */
	public static String SQL_SORT_DESC = "DESC";

	/** 升序查询 */
	public static String USER_NAME = "userName";
	/** 降序查询 */
	public static String U_ID = "uid";
}
