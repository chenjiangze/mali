package mali.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author andyfang
 * 
 */
public class DateUtil {
	/** 日期格式yyyy-MM-dd字符串常量 */
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	/** 日期格式yyyy-MM-dd HH:mm:ss字符串常量 */
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** 日期格式yyyy-MM字符串常量 */
	public static final String MONTH_FORMAT = "yyyy-MM";

	/** 一个月时间大约的long型数字 */
	public static final long MONTH_LONG = 2651224907l;

	/**
	 * @return 当前时间
	 */
	public static long now() {
		return System.currentTimeMillis();
	}

	/**
	 * 得到当前日期的前/后　beforeDays　天的日期数
	 * 
	 * @param beforeDays
	 * @return
	 */
	public static String getDate(int beforeDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, beforeDays);
		String a = dateToString(c.getTime(), DATE_FORMAT);
		return a;
	}

	/**
	 * 得到当前日期的前/后　beforeDays　天的日期数,格式自定
	 * 
	 * @param beforeDays
	 * @param dateFormat
	 * @return
	 */
	public static String getDate(int beforeDays, String dateFormat) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, beforeDays);
		String a = dateToString(c.getTime(), dateFormat);
		return a;
	}

	/**
	 * 将日期类型转换为yyyy-MM-dd字符串
	 * 
	 * @param dateValue
	 * @return String
	 */
	public static String dateToString(Date dateValue) {
		return dateToString(dateValue, DATETIME_FORMAT);
	}

	/**
	 * 将日期类型转换为指定格式的字符串
	 * 
	 * @param dateValue
	 * @param format
	 * @return String
	 */
	public static String dateToString(Date dateValue, String format) {
		if (dateValue == null || format == null) {
			return null;
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(dateValue);
		}
	}

	/**
	 * 将日期yyyy-MM-dd字符串转为日期类型，如果转换失败返回null
	 * 
	 * @param stringValue
	 * @return Date
	 */
	public static Date stringToDate(String stringValue) {
		return stringToDate(stringValue, DATE_FORMAT);
	}

	/**
	 * 将指定日期格式的字符串转为日期类型，如果转换失败返回null
	 * 
	 * @param stringValue
	 * @param format
	 * @return Date
	 */
	public static Date stringToDate(String stringValue, String format) {
		Date dateValue = null;
		if (stringValue != null && format != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateValue = dateFormat.parse(stringValue);
			} catch (ParseException ex) {
				dateValue = null;
			}
		}
		return dateValue;
	}

	/**
	 * 获得当前年
	 * 
	 * @return string
	 */
	public static String getNowYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return String.valueOf(year);
	}

	/**
	 * 获得当前月
	 * 
	 * @return string
	 */
	public static String getNowMonth() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		if (month < 10) {
			return "0" + month;
		} else {
			return String.valueOf(month);
		}
	}

	/**
	 * 获得当前日
	 * 
	 * @return string
	 */
	public static String getNowDay() {
		return dateToString(new Date(), "dd");

	}

	/**
	 * 昨天
	 * 
	 * @return
	 */
	public static String getYestday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DATE, -1);
		return dateToString(date.getTime(), "dd");

	}

	/**
	 * 返回几个月前的Date类型
	 * 
	 * @param monthCount 几个月
	 * @return Date
	 */
	public static Date getDateFront(int monthCount) {
		return new Date(Calendar.getInstance().getTimeInMillis() - MONTH_LONG * monthCount);
	}

	/**
	 * 返回当前小时
	 * 
	 * @return string
	 */
	public static String getNowHour() {
		return dateToString(new Date(), "HH");
	}

	/**
	 * 返回当前分钟
	 * 
	 * @return string
	 */
	public static String getNowMinute() {
		return dateToString(new Date(), "mm");
	}

	/**
	 * 设置时间的日期值
	 * 
	 * @param stringDate
	 * @param num
	 * @return Date
	 */
	public static Date setDate(String stringDate, int num) {
		if (stringDate != null) {
			Date date = stringToDate(stringDate, "yyyy-MM-dd");
			return setDate(date, num);
		} else {
			return null;
		}
	}

	/**
	 * 设置时间的日期值
	 * 
	 * @param date
	 * @param num
	 * @return Date
	 */
	public static Date setDate(String longValue) {
		Date dateValue = null;
		if (longValue != null) {
			try {
				dateValue = new Date(Long.parseLong(longValue));
			} catch (Exception e) {
			}
		}
		return dateValue;
	}

	/**
	 * 设置时间的日期值
	 * 
	 * @param date
	 * @param num
	 * @return Date
	 */
	public static Date setDate(Date date, int num) {
		Date dateValue = null;
		Calendar c = null;
		if (date != null) {
			c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DAY_OF_MONTH, num);
			dateValue = c.getTime();
		}
		return dateValue;
	}

	/**
	 * 取得两个日期的时间间隔,相差的天数
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getDayBetween(Date d1, Date d2) {
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		if (d1.before(d2)) {
			before.setTime(d1);
			after.setTime(d2);
		} else {
			before.setTime(d2);
			after.setTime(d1);
		}
		int days = 0;

		int startDay = before.get(Calendar.DAY_OF_YEAR);
		int endDay = after.get(Calendar.DAY_OF_YEAR);

		int startYear = before.get(Calendar.YEAR);
		int endYear = after.get(Calendar.YEAR);
		before.clear();
		before.set(startYear, 0, 1);

		while (startYear != endYear) {
			before.set(startYear++, Calendar.DECEMBER, 31);
			days += before.get(Calendar.DAY_OF_YEAR);
		}
		return days + endDay - startDay;
	}

	public static Date addDay(Date myDate, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(myDate);
		cal.add(Calendar.DAY_OF_MONTH, amount);
		return cal.getTime();
	}

	/**
	 * 计算两个日期相差的天数 （注意单位是天 忽略时分秒）
	 * 
	 * @param before
	 * @param after
	 * @return
	 * @throws ParseException
	 */
	public static int getIntervalDays(Date before, Date after) {

		if (null == before || null == after) {
			throw new NullPointerException();
		}

		long intervalMilli = (after.getTime() / (24 * 60 * 60 * 1000)) - (before.getTime() / (24 * 60 * 60 * 1000));

		return (int) intervalMilli;
	}

	public static int getIntervalMonths(Date before, Date after) {
		if (null == before || null == after) {
			throw new NullPointerException();
		}

		Calendar b = Calendar.getInstance();
		b.setTime(before);
		Calendar a = Calendar.getInstance();
		a.setTime(after);

		int monthbefore = b.get(Calendar.MONTH);
		int monthafter = a.get(Calendar.MONTH);

		int ys = a.get(Calendar.YEAR) - b.get(Calendar.YEAR);

		return monthafter + ys * 12 - monthbefore;
	}

	/** for test only */
	public static void main(String[] args) {
		// Date myDate = DateUtil.stringToDate("2009-10-21 16:58:26",
		// "yyyy-MM-dd hh:mm:ss");
		// Date myDate = DateUtil.stringToDate("12365698", "yyyy-MM-dd");
		//
		// System.out.println("myDate:"+myDate);
		//
		// Date currentDate = new Date();
		// Date endDate = DateUtil.addDay(currentDate, 15);
		//
		// System.out.println(endDate.after(currentDate));
		// System.out.println("大前天："+DateUtil.getDate(-3));
		// System.out.println("明天"+DateUtil.getDate(1));
		// System.out.println("明天"+DateUtil.getDate(1).replaceAll("-",""));
		// System.out.println("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Maxthon; Poco 0.31; .NET CLR 1.1.4322; InfoPath.1; Alexa Toolbar)".length());
		// System.out.println(DateUtil.addDay(myDate, 5));
		// System.out.println(DateUtil.getNowYear()+DateUtil.getNowMonth()+DateUtil.getNowDay());
		// System.out.println(getIntervalDays(currentDate,endDate));

		System.out.println(getIntervalDays(stringToDate("2009-10-21", "yyyy-MM-dd"), stringToDate("2009-10-24", "yyyy-MM-dd")));
		System.out.println(getIntervalDays(stringToDate("2009-9-20", "yyyy-MM-dd"), stringToDate("2009-10-21", "yyyy-MM-dd")));
		System.out.println(getIntervalDays(stringToDate("2009-1-21", "yyyy-MM-dd"), stringToDate("2009-2-19", "yyyy-MM-dd")));
		System.out.println(getIntervalDays(stringToDate("2011-1-21", "yyyy-MM-dd"), stringToDate("2012-1-20", "yyyy-MM-dd")));
	}
}
