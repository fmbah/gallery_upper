package com.xs.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 提供日期的加减转换等功能 包含多数常用的日期格式
 * 
 * @author tongyi
 * @date Jul 29, 2013
 * 
 */
public class CalendarUtil {

	private static final Logger	logger		= LoggerFactory.getLogger(CalendarUtil.class);

	/** new a Calendar instance */
	static GregorianCalendar cldr		= new GregorianCalendar();

	/** the milli second of a day */
	public static final long	DAYMILLI	= 24 * 60 * 60 * 1000;

	/** the milli seconds of an hour */
	public static final long	HOURMILLI	= 60 * 60 * 1000;

	/** the milli seconds of a minute */
	public static final long	MINUTEMILLI	= 60 * 1000;

	/** the milli seconds of a second */
	public static final long	SECONDMILLI	= 1000;

	/** added time */
	public static final String TIMETO		= " 23:59:59";

	/** added time */
	public static final String TIMEFROM	= " 00:00:00";

	public static final Date EMPTY_DATE	= CalendarUtil.toDate("1970-01-01 08:00:00", CalendarUtil.TIME_PATTERN);

	/**
	 * set the default time zone
	 */
	static {
		cldr.setTimeZone(java.util.TimeZone.getTimeZone("GMT+9:00"));
	}

	/** flag before */
	public static final transient int	BEFORE					= 1;

	/** flag after */
	public static final transient int	AFTER					= 2;

	/** flag equal */
	public static final transient int	EQUAL					= 3;

	/** date format dd/MMM/yyyy:HH:mm:ss +0900 */
	public static final String TIME_PATTERN_LONG		= "dd/MMM/yyyy:HH:mm:ss +0900";

	/** date format dd/MM/yyyy:HH:mm:ss +0900 */
	public static final String TIME_PATTERN_LONG2		= "dd/MM/yyyy:HH:mm:ss +0900";

	/** date format yyyy-MM-dd HH:mm:ss */
	public static final String TIME_PATTERN			= "yyyy-MM-dd HH:mm:ss";

	/** date format YYYY-MM-DD HH24:MI:SS */
	public static final String DB_TIME_PATTERN			= "YYYY-MM-DD HH24:MI:SS";

	/** date format dd/MM/yy HH:mm:ss */
	public static final String TIME_PATTERN_SHORT		= "dd/MM/yy HH:mm:ss";

	/** date format dd/MM/yy HH24:mm */
	public static final String TIME_PATTERN_SHORT_1	= "yyyy/MM/dd HH:mm";

	/** date format yyyyMMddHHmmss */
	public static final String TIME_PATTERN_SESSION	= "yyyyMMddHHmmss";

	/** date format yyyyMMdd */
	public static final String DATE_FMT_0				= "yyyyMMdd";

	/** date format yyyy/MM/dd */
	public static final String DATE_FMT_1				= "yyyy/MM/dd";

	/** date format yyyy/MM/dd hh:mm:ss */
	public static final String DATE_FMT_2				= "yyyy/MM/dd hh:mm:ss";

	/** date format yyyy-MM-dd */
	public static final String DATE_FMT_3				= "yyyy-MM-dd";

	/**
	 * change string to date 将String类型的日期转成Date类型
	 * 
	 * @param sDate the date string
	 * @param sFmt the date format
	 * 
	 * @return Date object
	 */
	public static Date toDate(String sDate, String sFmt) {
		if (StringUtils.isBlank(sDate) || StringUtils.isBlank(sFmt)) {
			return null;
		}

		SimpleDateFormat sdfFrom = null;
		Date dt = null;
		try {
			sdfFrom = new SimpleDateFormat(sFmt);
			dt = sdfFrom.parse(sDate);
		} catch (Exception ex) {
			logger.error("CalendarUtil toDate(String, String) error." + ex.getMessage());
			return null;
		} finally {
			sdfFrom = null;
		}

		return dt;
	}

	/**
	 * change date to string 将日期类型的参数转成String类型
	 * 
	 * @param dt a date
	 * 
	 * @return the format string
	 */
	public static String toString(Date dt) {
		return toString(dt, DATE_FMT_0);
	}

	/**
	 * change date object to string 将String类型的日期转成Date类型
	 * 
	 * @param dt date object
	 * @param sFmt the date format
	 * 
	 * @return the formatted string
	 */
	public static String toString(Date dt, String sFmt) {
		if (null == dt || StringUtils.isBlank(sFmt)) {
			return null;
		}

		SimpleDateFormat sdfFrom = null;
		String sRet = null;
		try {
			sdfFrom = new SimpleDateFormat(sFmt);
			sRet = sdfFrom.format(dt).toString();
		} catch (Exception ex) {
			logger.error("CalendarUtil toString(Date, String) error." + ex.getMessage());
			return null;
		} finally {
			sdfFrom = null;
		}

		return sRet;
	}

	/**
	 * 获取Date所属月的最后一天日期
	 * 
	 * @param date
	 * @return Date 默认null
	 */
	public static Date getMonthLastDate(Date date) {
		if (null == date) {
			return null;
		}

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY, 23);
		ca.set(Calendar.MINUTE, 59);
		ca.set(Calendar.SECOND, 59);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		ca.add(Calendar.MONTH, 1);
		ca.add(Calendar.DAY_OF_MONTH, -1);

		Date lastDate = ca.getTime();
		return lastDate;
	}

	/**
	 * 获取Date所属月的最后一天日期
	 * 
	 * @param date
	 * @param pattern
	 * @return String 默认null
	 */
	public static String getMonthLastDate(Date date, String pattern) {
		Date lastDate = getMonthLastDate(date);
		if (null == lastDate) {
			return null;
		}

		if (StringUtils.isBlank(pattern)) {
			pattern = TIME_PATTERN;
		}

		return toString(lastDate, pattern);
	}

	/**
	 * 获取Date所属月的第一天日期
	 * 
	 * @param date
	 * @return Date 默认null
	 */
	public static Date getMonthFirstDate(Date date) {
		if (null == date) {
			return null;
		}

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.DAY_OF_MONTH, 1);

		Date firstDate = ca.getTime();
		return firstDate;
	}

	/**
	 * 获取Date所属月的第一天日期
	 * 
	 * @param date
	 * @param pattern
	 * @return String 默认null
	 */
	public static String getMonthFirstDate(Date date, String pattern) {
		Date firstDate = getMonthFirstDate(date);
		if (null == firstDate) {
			return null;
		}

		if (StringUtils.isBlank(pattern)) {
			pattern = TIME_PATTERN;
		}

		return toString(firstDate, pattern);
	}

	/**
	 * 获取昨天的日期
	 * 
	 * @param pattern
	 * @return String 默认null
	 */
	public static String getYesterdayDate(String pattern) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);

		if (StringUtils.isBlank(pattern)) {
			pattern = TIME_PATTERN;
		}

		return toString(cal.getTime(), pattern);
	}

	/**
	 * 获取指定日期的上一天
	 * 
	 * @param pattern
	 * @return Date
	 */
	public static Date getYesterdayDate(Date pattern) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pattern);
		calendar.add(Calendar.DATE,-1);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
		return calendar.getTime();
	}

	public static String getYesterdayDate(Date pattern, Integer days) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(pattern);
		calendar.add(Calendar.DATE,days);//把日期往前减少一天，若想把日期向后推一天则将负数改为正数
		return formatter.format(calendar.getTime());
	}

	/**
	 * 获取指定日期的下一天
	 * 
	 * @param pattern
	 * @return Date
	 */
	public static Date getTomorrowDate(Date pattern) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(pattern);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return new Date(calendar.getTimeInMillis());
	}

	/**
	 * 获取两个日期之间的日期
	 * @param start 开始日期
	 * @param end 结束日期
	 * @return 日期集合
	 */
	public static List<String> getBetweenDates(Date start, Date end) {
		List<String> result = new ArrayList<String>();
		Calendar tempStart = Calendar.getInstance();
		tempStart.setTime(getYesterdayDate(start));
		tempStart.add(Calendar.DAY_OF_YEAR, 1);
		
		Calendar tempEnd = Calendar.getInstance();
		tempEnd.setTime(getTomorrowDate(end));
		while (tempStart.before(tempEnd)) {
			String date1 = toString(tempStart.getTime(), DATE_FMT_3);
			result.add(date1);
			tempStart.add(Calendar.DAY_OF_YEAR, 1);
		}
		return result;
	}

	/**
	 * 获取两个日期之间的月份
	 * @param start 开始日期
	 * @param end 结束日期
	 * @return 日期集合
	 */
	public static List<String> getMonthBetween(Date start, Date end){
		List<String> result = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

		Calendar min = Calendar.getInstance();
		Calendar max = Calendar.getInstance();

		min.setTime(start);
		min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

		max.setTime(end);
		max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

		Calendar curr = min;
		while (curr.before(max)) {
			result.add(sdf.format(curr.getTime()));
			curr.add(Calendar.MONTH, 1);
		}

		return result;
	}

	/**
	 * 计算两个日期间隔的天数
	 * 
	 * @param firstDate 小者
	 * @param lastDate 大者
	 * @return int 默认-1
	 */
	public static int getIntervalDays(Date firstDate, Date lastDate) {
		if (null == firstDate || null == lastDate) {
			return -1;
		}

		long intervalMilli = lastDate.getTime() - firstDate.getTime();
		return (int) (intervalMilli / (24 * 60 * 60 * 1000));
	}

	/**
	 * 计算两个日期间隔的小时数
	 * 
	 * @param firstDate 小者
	 * @param lastDate 大者
	 * @return int 默认-1
	 */
	public static int getTimeIntervalHours(Date firstDate, Date lastDate) {
		if (null == firstDate || null == lastDate) {
			return -1;
		}

		long intervalMilli = lastDate.getTime() - firstDate.getTime();
		return (int) (intervalMilli / (60 * 60 * 1000));
	}

	/**
	 * 计算两个日期间隔的分钟数
	 * 
	 * @param firstDate 小者
	 * @param lastDate 大者
	 * @return int 默认-1
	 */
	public static int getTimeIntervalMins(Date firstDate, Date lastDate) {
		if (null == firstDate || null == lastDate) {
			return -1;
		}

		long intervalMilli = lastDate.getTime() - firstDate.getTime();
		return (int) (intervalMilli / (60 * 1000));
	}

	/**
	 * format the date in given pattern 格式化日期
	 * 
	 * @param d date
	 * @param pattern time pattern
	 * @return the formatted string
	 */
	public static String formatDate(Date d, String pattern) {
		if (null == d || StringUtils.isBlank(pattern)) {
			return null;
		}

		SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateInstance();

		formatter.applyPattern(pattern);
		return formatter.format(d);
	}

	/**
	 * 比较两个日期的先后顺序
	 * 
	 * @param src
	 * @param desc
	 * @return
	 */
	public static int compareDate(Date src, Date desc) {
		if ((src == null) && (desc == null)) {
			return EQUAL;
		} else if (desc == null) {
			return BEFORE;
		} else if (src == null) {
			return AFTER;
		} else {
			long timeSrc = src.getTime();
			long timeDesc = desc.getTime();

			if (timeSrc == timeDesc) {
				return EQUAL;
			} else {
				return (timeDesc > timeSrc) ? AFTER : BEFORE;
			}
		}
	}

	/**
	 * 比较两个日期的先后顺序
	 * 
	 * @param first date1
	 * @param second date2
	 * 
	 * @return EQUAL - if equal BEFORE - if before than date2 AFTER - if over than date2
	 */
	public static int compareTwoDate(Date first, Date second) {
		if ((first == null) && (second == null)) {
			return EQUAL;
		} else if (first == null) {
			return BEFORE;
		} else if (second == null) {
			return AFTER;
		} else if (first.before(second)) {
			return BEFORE;
		} else if (first.after(second)) {
			return AFTER;
		} else {
			return EQUAL;
		}
	}

	/**
	 * 比较日期是否介于两者之间
	 * 
	 * @param date the specified date
	 * @param begin date1
	 * @param end date2
	 * 
	 * @return true - between date1 and date2 false - not between date1 and date2
	 */
	public static boolean isDateBetween(Date date, Date begin, Date end) {
		int c1 = compareTwoDate(begin, date);
		int c2 = compareTwoDate(date, end);

		return (((c1 == BEFORE) && (c2 == BEFORE)) || (c1 == EQUAL) || (c2 == EQUAL));
	}

	/**
	 * 比较日期是否介于当前日期的前后数天内
	 * 
	 * @param myDate
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean isDateBetween(Date myDate, int begin, int end) {
		return isDateBetween(myDate, getCurrentDateTime(), begin, end);
	}

	/**
	 * 比较日期是否介于指定日期的前后数天内
	 * 
	 * @param utilDate
	 * @param dateBaseLine
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean isDateBetween(Date utilDate, Date dateBaseLine, int begin, int end) {
		String pattern = TIME_PATTERN;

		String my = toString(utilDate, pattern);
		Date myDate = toDate(my, pattern);

		String baseLine = toString(dateBaseLine, pattern);

		// Date baseLineDate = parseString2Date(baseLine, pattern);
		String from = addDays(baseLine, begin);
		Date fromDate = toDate(from, pattern);

		String to = addDays(baseLine, end);
		Date toDate = toDate(to, pattern);

		return isDateBetween(myDate, fromDate, toDate);
	}

	/**
	 * 增加天数
	 * 
	 * @param date
	 * @param day
	 * @return Date
	 */
	public static Date addDate(Date date, int day) {
		if (null == date) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);
		return calendar.getTime();
	}

	/**
	 * 增加天数
	 * 
	 * @param date
	 * @param day
	 * @param pattern
	 * @return
	 */
	public static String addDays(Date date, int day, String pattern) {
		return addDays(toString(date, pattern), day, pattern);
	}

	/**
	 * 增加天数
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static String addDays(Date date, int day) {
		return addDays(toString(date, TIME_PATTERN), day);
	}

	/**
	 * 增加天数
	 * 
	 * @param date
	 * @param day
	 * @return
	 */
	public static String addDaysYMD(String date, int day) {
		return addDays(date, day, DATE_FMT_3);
	}

	/**
	 * 增加天数
	 *
	 * @param date
	 * @param day
	 * @return
	 */
	public static String addDays(String date, int day) {
		return addDays(date, day, TIME_PATTERN);
	}

	/**
	 * get the time of the specified date after given days
	 * 
	 * @param date the specified date
	 * @param day day distance
	 * 
	 * @return the format string of time
	 */
	public static String addDays(String date, int day, String pattern) {
		if (date == null) {
			return "";
		}

		if (date.equals("")) {
			return "";
		}

		if (day == 0) {
			return date;
		}

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			Calendar calendar = dateFormat.getCalendar();

			calendar.setTime(dateFormat.parse(date));
			calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);
			return dateFormat.format(calendar.getTime());
		} catch (Exception ex) {
			logger.error("CalendarUtil addDays(String, int, String) error." + ex.getMessage());
			return "";
		}
	}

	/**
	 * change timestamp to formatted string
	 * 
	 * @param t Timestamp
	 * @param sFmt date format
	 * 
	 * @return formatted string
	 */
	public static String formatTimestamp(Timestamp t, String sFmt) {
		if (t == null || StringUtils.isBlank(sFmt)) {
			return "";
		}

		t.setNanos(0);

		DateFormat ft = new SimpleDateFormat(sFmt);
		String str = "";

		try {
			str = ft.format(t);
		} catch (NullPointerException ex) {
			logger.error("CalendarUtil formatTimestamp error." + ex.getMessage());
		}

		return str;
	}

	/**
	 * return current date
	 * 
	 * @return current date
	 */
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * return current calendar instance
	 * 
	 * @return Calendar
	 */
	public static Calendar getCurrentCalendar() {
		return Calendar.getInstance();
	}

	/**
	 * return current time
	 * 
	 * @return current time
	 */
	public static Timestamp getCurrentDateTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 获取年份
	 * 
	 * @param date Date
	 * @return int
	 */
	public static final int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取年份
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getYear(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取月份
	 * 
	 * @param date Date
	 * @return int
	 */
	public static final int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取月份
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getMonth(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取日期
	 * 
	 * @param date Date
	 * @return int
	 */
	public static final int getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 获取日期
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 获取小时
	 * 
	 * @param date Date
	 * @return int
	 */
	public static final int getHour(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取小时
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getHour(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取分钟
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getMinute(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 获取分钟
	 * 
	 * @param millis long
	 * @return int
	 */
	public static final int getMinute(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.get(Calendar.MINUTE);
	}

	/**
	 * 把日期后的时间归0 变成(yyyy-MM-dd 00:00:00:000)
	 * 
	 * @param date Date
	 * @return Date
	 */
	public static final Date zerolizedTime(Date fullDate) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(fullDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/*

    获取当前时间之前或之后几小时 hour

   */

	public static String getTimeByHour(int hour) {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hour);

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

	}

    /*

    获取当前时间之前或之后几分钟 minute

    */

	public static String getTimeByMinute(int minute) {

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MINUTE, minute);

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

	}

	/**
	 * 得到本周周一日期
	 * @return
	 */
	public static String getWeekMonday(){
		SimpleDateFormat formater=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		Date first=cal.getTime();
		return formater.format(first);
	}

	public static void main(String[] args) {
		Date date = new Date();
		System.out.println(CalendarUtil.getYesterdayDate(date,-7));
	}
}