package cathay.coindeskApi.commons.util;

import static java.util.Objects.requireNonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * 常用日期操作
 * 
 * @author drew lee
 */
public class DateUtils {

	private static ThreadLocal<StringBuilder> threadLocalBuffer = ThreadLocal.withInitial(StringBuilder::new);
	
	public static class Formats {
		
		public static final String DateFormatString = "yyyy-MM-dd";
		public static final java.text.DateFormat DateFormat = new SimpleDateFormat(DateFormatString);
		
		public static final String DateTimeFormatString = "yyyy-MM-dd HH:mm:ss";
		public static final java.text.DateFormat DateTimeFormat = new SimpleDateFormat(DateTimeFormatString);
		
		public static DateFormat of(CharSequence pattern) throws IllegalArgumentException {
			requireNonNull(pattern, "The argument 'pattern' cannot be null");
			return new SimpleDateFormat(pattern.toString());
		}
	}
	
	/**
	 * 將此刻時間更新至日期 (日期使用自然格式, 年/月不需位移)
	 * 
	 * @param date 待更新的日期
	 * @param year 更新年份; 如果year < 0 為增量模式, 參數date.year = date.year - abs(year)
	 * @param month 更新月份; 如果month < 0 為增量模式, 參數date.month = date.month - abs(month)
	 * @param dateOfMonth 更新該月之日期; 如果dateOfMonth < 0 為增量模式, 參數date.date = date.date - abs(dayOfMonth)
	 * @return 更新參數data後回傳
	 */
	public static Date updateDate(Date date, Integer year, Integer month, Integer dayOfMonth) {
		if (year != null) {
			if (year >= 0) date.setYear(year-1900);
			else date.setYear(date.getYear() + year);
		}		
		if (month != null) {
			if (month >= 0) date.setMonth(month-1);
			else date.setMonth(date.getMonth() + month);
		}		
		if (dayOfMonth != null) {
			if (dayOfMonth >= 0) date.setDate(dayOfMonth);
			else date.setDate(date.getDate() + dayOfMonth);
		}
		return date;
	}
	
	/**
	 * 將此刻時間更新至時間
	 * 
	 * @param date 待更新的日期
	 * @param hours 更新年份; 如果hours < 0 為增量模式, 參數date.hours = date.hours - abs(hours)
	 * @param minutes 更新分鐘; 如果minutes < 0 為增量模式, 參數date.minutes = date.minutes - abs(minutes)
	 * @param seconds 更新秒數; 如果seconds < 0 為增量模式, 參數date.seconds = date.seconds - abs(seconds)
	 * @return 更新參數data後回傳
	 */
	public static Date updateTime(Date date, Integer hours, Integer minutes, Integer seconds) {
		if (hours != null) {
			if (hours >= 0) date.setHours(hours);
			else date.setHours(date.getHours() + hours);
		}
		if (minutes != null) {
			if (minutes >= 0) date.setMinutes(minutes);
			else date.setMinutes(date.getMinutes() + minutes);
		}
		if (seconds != null) {
			if (seconds >= 0) date.setSeconds(seconds);
			else date.setSeconds(date.getSeconds() + seconds);
		}
		return date;
	}

	/**
	 * 將此刻時間更新至此刻時間 (日期使用自然格式, 年/月不需位移)
	 * 
	 * @param date 待更新的日期
	 * @param year 更新年份; 如果year < 0 為增量模式, 參數date.year = date.year - abs(year)
	 * @param month 更新月份; 如果month < 0 為增量模式, 參數date.month = date.month - abs(month)
	 * @param dateOfMonth 更新該月之日期; 如果dateOfMonth < 0 為增量模式, 參數date.date = date.date - abs(dayOfMonth)
	 * @param hours 更新年份; 如果hours < 0 為增量模式, 參數date.hours = date.hours - abs(hours)
	 * @param minutes 更新分鐘; 如果minutes < 0 為增量模式, 參數date.minutes = date.minutes - abs(minutes)
	 * @param seconds 更新秒數; 如果seconds < 0 為增量模式, 參數date.seconds = date.seconds - abs(seconds)
	 * @return 更新參數data後回傳
	 */
	public static Date updateDateTime(Date date, Integer year, Integer month, Integer dayOfMonth, Integer hours, Integer minutes, Integer seconds) {
		// 年月日
		if (year != null) {
			if (year >= 0) date.setYear(year-1900);
			else date.setYear(date.getYear() + year);
		}		
		if (month != null) {
			if (month >= 0) date.setMonth(month-1);
			else date.setMonth(date.getMonth() + month);
		}		
		if (dayOfMonth != null) {
			if (dayOfMonth >= 0) date.setDate(dayOfMonth);
			else date.setDate(date.getDate() + dayOfMonth);
		}
		
		// 時分秒
		if (hours != null) {
			if (hours >= 0) date.setHours(hours);
			else date.setHours(date.getHours() + hours);
		}
		if (minutes != null) {
			if (minutes >= 0) date.setMinutes(minutes);
			else date.setMinutes(date.getMinutes() + minutes);
		}
		if (seconds != null) {
			if (seconds >= 0) date.setSeconds(seconds);
			else date.setSeconds(date.getSeconds() + seconds);
		}
		return date;
	}
	
	/**
	 * 將此刻時間更新至此刻時間
	 * 
	 * @param date 待更新的日期
	 * @return 更新參數data後回傳
	 */
	public static Date updateDateTime(Date date) {
		Date now = new Date();
		date.setYear(now.getYear());
		date.setMonth(now.getMonth());
		date.setDate(now.getDate());
		date.setHours(now.getHours());
		date.setMinutes(now.getMinutes());
		date.setSeconds(now.getSeconds());
		return date;
	}
	
	///////////////////////////////////////////////////////////////////////////////////

	public static String formatDate(Date date) {
		return formatDate(date, Formats.DateFormatString);
	}

	public static String formatDateTime(Date date) {
		return formatDate(date, Formats.DateTimeFormatString);
	}
	
	/**
	 * @throws IllegalArgumentException pattern字串不合java日期格式時
	 */
	public static String formatDate(Date date, CharSequence pattern) {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(pattern, "The argument 'pattern' cannot be null");
		
		// 使用是使用通用格式, 就使用內建好的 (而不另外new)
		if (Formats.DateFormatString.equals(pattern)) {
			return formatDate(date, Formats.DateFormat); 
		} else if (Formats.DateTimeFormatString.equals(pattern)) {
			return formatDate(date, Formats.DateTimeFormat);
		}
		return formatDate(date, Formats.of(pattern));
	}
	
	public static String formatDate(Date date, DateFormat format) {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(format, "The argument 'format' cannot be null");
		try {
	        return format.format(date);
		}
		// format不合java規範時
		catch (IllegalArgumentException e) {
			StringBuilder errMessage = threadLocalBuffer.get();
			try {
				errMessage.append("date無法格式化為字串, 請確認格式是否正確 format = ")
						  .append("\"").append(format).append("\"");
				throw new IllegalArgumentException(errMessage.toString(), e);
			} finally {
				errMessage.setLength(0);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////

	// java 8 date & time 轉換為 java.util.Date
	public static Date toDate(LocalDate date) {
		return java.sql.Date.valueOf(date);
	}
	
	public static Date toDate(LocalDateTime dateTime) {
		return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	// 僅日期
	public static Date toDate(CharSequence date) throws ParseException {
		return toDate(date, Formats.DateFormat, true);
	}

	public static Date toDate(CharSequence date, CharSequence pattern) throws IllegalArgumentException {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(pattern, "The argument 'pattern' cannot be null");	
		
		// 如果是使用通用格式, 就使用內建好的 (而不另外new)
		if (Formats.DateFormatString.equals(pattern)) {
			return toDate(date, Formats.DateFormat, true); 
		} else if (Formats.DateTimeFormatString.equals(pattern)) {
			return toDate(date, Formats.DateTimeFormat, true);
		}
		return toDate(date, Formats.of(pattern), true);
	}
	
	// 日期時間
	public static Date toDateTime(CharSequence dateTime) throws ParseException {
		return toDate(dateTime, Formats.DateTimeFormat, true);
	}

	public static Date toDateTime(CharSequence dateTime, boolean throwOnError) {
		return toDate(dateTime, Formats.DateTimeFormat, throwOnError);
	}

	/**
     * 將日期字串date轉為Date型別
     * 
	 * @param date 待轉換的日期字串
	 * @param pattern 允許自行指定格式
	 * @param throwOnError
	 * 
	 * @throws IllegalArgumentException 參數字串date不符合format時(或format不合java規範的格式時)
	 */
	public static Date toDate(CharSequence date, CharSequence pattern, boolean throwOnError) throws IllegalArgumentException {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(pattern, "The argument 'pattern' cannot be null");
		
		// 如果是使用通用格式, 就使用內建好的 (而不另外new)
		if (Formats.DateFormatString.equals(pattern)) {
			return toDate(date, Formats.DateFormat, throwOnError); 
		} else if (Formats.DateTimeFormatString.equals(pattern)) {
			return toDate(date, Formats.DateTimeFormat, throwOnError);
		}
		return toDate(date, Formats.of(pattern), throwOnError);
	}
	
	public static Date toDate(CharSequence date, DateFormat format, boolean throwOnError) throws IllegalArgumentException {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(format, "The argument 'format' cannot be null");
		try {
			return format.parse(date.toString());
		}
		catch (ParseException e) {
			if (!throwOnError)
				return null;
		
			StringBuilder errMessage = threadLocalBuffer.get();
			try {
				errMessage.append("日期字串無法轉為Date, 請確認格式是否正確 format = ")
						  .append("\"").append(format).append("\"");
				throw new IllegalArgumentException(errMessage.toString(), e);
			} finally {
				errMessage.setLength(0);
			}
		}
	}
	
    /**
     * 將日期字串date轉為Date型別
     *  (字串date不符預設格式時(yyyy-MM-dd), 不會拋出exception, 只會回傳null)
     * 
     * @param date 日期字串
     */
	public static Date toDateQuitely(CharSequence date) {
		return toDate(date, Formats.DateFormat, false);
	}

	public static Date toDateQuitely(CharSequence date, CharSequence pattern) {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(pattern, "The argument 'pattern' cannot be null");
		
		// 如果是使用通用格式, 就使用內建好的 (而不另外new)
		if (Formats.DateFormatString.equals(pattern)) {
			return toDate(date, Formats.DateFormat, false); 
		} else if (Formats.DateTimeFormatString.equals(pattern)) {
			return toDate(date, Formats.DateTimeFormat, false);
		}
		return toDate(date, pattern, false);
	}
	
    /**
     * 將日期字串date轉為Date型別
     *  (字串date不符預設格式時(yyyy-MM-dd HH:mm:ss), 不會拋出exception, 只會回傳null)
     * 
     * @param date 日期字串
     */
	public static Date toDateTimeQuitely(CharSequence dateTime) {
		return toDate(dateTime, Formats.DateTimeFormat, false);
	}
	
    /**
     * 將日期字串date轉為Date型別
     *  (字串date不符格式format時, 不會拋出exception, 只會回傳null)
     * 
     * @param date 日期字串
     */
	public static Date toDateTimeQuitely(CharSequence dateTime, CharSequence pattern) {
		requireNonNull(dateTime, "The argument 'date' cannot be null");
		requireNonNull(pattern, "'pattern' must not be null");
    	
		// 如果是使用通用格式, 就使用內建好的 (而不另外new)
		if (Formats.DateTimeFormatString.equals(pattern))
			return toDate(dateTime, Formats.DateTimeFormat, false);
		return toDate(dateTime, pattern, false);
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	// TODO: 會建立2個 new calendar
	public static boolean areDatesEqual(Date date1, Date date2) {
		requireNonNull(date1, "'date1' the 1st date argument must not be null");
    	requireNonNull(date2, "'date2' the 2nd date argument must not be null");
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
				cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 得到兩日期之間的天數
	 * 
	 * FIXME：toLocalDate會建立新的 new LocalDate
	 */
    public static long getDaysBetween(Date startDate, Date endDate) {
    	requireNonNull(startDate, "'startDate' the 1st date argument must not be null");
    	requireNonNull(endDate, "'endDate' the 2nd date argument must not be null");
    	// check date boundary integrity
    	if (startDate.getTime() < endDate.getTime() != true)
    		throw new IllegalArgumentException("起始日期startDate必須早於endDate");
    	
        LocalDate startLocalDate = toLocalDate(startDate);
        LocalDate endLocalDate = toLocalDate(endDate);
		if(startLocalDate.equals(endLocalDate))
			return 1;
        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }
    
    /**
     * 判斷是否 startDate <= date <= endDate 23:59:59
     *  (參數為LocalDate的版本)
     *  
     * FIXME：toLocalDate會建立新的 new LocalDate
     */
    public static boolean isDateBetween(Date date, final LocalDate startDate, final LocalDate endDate) {
    	requireNonNull(startDate, "'startDate' must not be null");
    	requireNonNull(endDate, "'endDate' must not be null");
    	// check date boundary integrity
    	if (startDate.isBefore(endDate) != true)
    		throw new IllegalArgumentException("起始日期startDate必須早於endDate");
    	
    	// check non-null of 'date' argument
    	requireNonNull(date, "The argument 'date' cannot be null");
    	
    	LocalDate dateTime = toLocalDate(date);
    	return (dateTime.equals(startDate) || dateTime.isAfter(startDate)) &&
        		(dateTime.equals(endDate)  || dateTime.isBefore(endDate));
    }
    
    /**
     * 判斷是否 startDate <= date <= endDate 23:59:59
     *  (參數為java.util.Date的版本)
     *  
     *  FIXME：每次會建立新的 new Date
     */
    public static boolean isDateBetween(Date date, final Date startDate, final Date endDate) {
    	requireNonNull(startDate, "'startDate' must not be null");
    	requireNonNull(endDate, "'endDate' must not be null");
    	// check date boundary integrity
    	if (startDate.getTime() < endDate.getTime() != true)
    		throw new IllegalArgumentException("起始日期startDate必須早於endDate");
    	
    	// check non-null of 'date' argument
    	requireNonNull(date, "The argument 'date' cannot be null");
    	
    	Date startDate_ = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate());
    	Date endDate_ = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate());
    	return (date.equals(startDate_) || date.after(startDate_)) &&
        		(date.equals(endDate_)   || date.before(endDate_));
    }

    /**
     * 日期時間是否在指定期限之前
     *  (舊版Date)
     *  
     * FIXME: toLocalDateTime 每次會建立新的 new toLocalDateTime
     */
    public static boolean isDateBefore(Date dateTime, final Date endDate) {
    	return isDateBefore(dateTime, toLocalDateTime(endDate));
    }
    
    /**
     * 支援: 舊版Date和java 8時間日期API
     * 
     * FIXME：toLocalDateTime 會建立新的 new LocalDateTime
     */
    public static boolean isDateBefore(Date dateTime, final ChronoLocalDateTime<?> endDate) {
    	requireNonNull(dateTime, "The argument 'dateTime' cannot be null");
    	requireNonNull(endDate, "'endDate' must not be null");
    	return toLocalDateTime(dateTime).isBefore(endDate);
    }
    
    /**
     * 日期時間是否在指定期限之後
     *  (舊版Date)
     *  
     * FIXME: toLocalDateTime 每次會建立新的 new toLocalDateTime
     */
    public static boolean isDateAfter(Date dateTime, final Date startDate) {
    	return isDateAfter(dateTime, toLocalDateTime(startDate));
    }
    
    /**
     * 支援: 舊版Date和java 8時間日期API
     */
    public static boolean isDateAfter(Date dateTime, final ChronoLocalDateTime<?> startDate) {
    	requireNonNull(dateTime, "The argument 'dateTime' cannot be null");
    	requireNonNull(startDate, "'startDate' must not be null");
    	return toLocalDateTime(dateTime).isAfter(startDate);
    }
    
    /**
     * 將日期字串date轉為LocalDate型別
     * 若日期字串不符合格式, 會拋出exception
     * 
     * @param date 日期字串
     * @param format
     */
    public static LocalDate toLocalDate(CharSequence date, String format) {
    	return toLocalDate(date, format, true);
    }
    
    /**
     * 將日期字串date轉為LocalDate型別
     * 
     * @param date 日期字串
     * @param pattern
     * @param throwOnError 當throwsExceptionOnfail為true, 如果字串date不符預設格式format時, 會拋出exception, 否則傳回null
     */
    public static LocalDate toLocalDate(CharSequence date, CharSequence pattern, boolean throwOnError) {
    	return toLocalDate(date, Formats.DateTimeFormat, throwOnError);
    }
    
    public static LocalDate toLocalDate(CharSequence date, DateFormat format, boolean throwOnError) {
		requireNonNull(date, "The argument 'date' cannot be null");
		requireNonNull(format, "'format' must not be null");
		
    	Date date_ = null;
    	try {
			date_ = format.parse(date.toString());
			return toLocalDate(date_);
		}
    	catch (ParseException e) {
    		if (!throwOnError)
    			return null;
    	
    		StringBuilder errMessage = threadLocalBuffer.get();
			try {
				errMessage.append("日期字串無法轉為Date, 請確認格式是否正確 format = ")
						  .append("\"").append(format).append("\"");
				throw new IllegalArgumentException(errMessage.toString(), e);
			} finally {
				errMessage.setLength(0);
			}
		}
    }

    //////////////////////// 不同日期型別之間互轉換 ///////////////////////////
    
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    
    public static LocalDate toLocalDateTime(CharSequence dateTime, CharSequence pattern, boolean throwOnError) {
    	return toLocalDateTime(dateTime, Formats.DateTimeFormat, throwOnError);
    }
    
    public static LocalDate toLocalDateTime(CharSequence dateTime, DateFormat format, boolean throwOnError) {
		requireNonNull(dateTime, "The argument 'date' cannot be null");
		requireNonNull(format, "'format' must not be null");
		
    	Date date_ = null;
    	try {
			date_ = format.parse(dateTime.toString());
			return toLocalDate(date_);
		}
    	catch (ParseException e) {
			if (!throwOnError)
    			return null;
    	
    		StringBuilder errMessage = threadLocalBuffer.get();
			try {
				errMessage.append("日期字串無法轉為Date, 請確認格式是否正確 format = ")
						  .append("\"").append(format).append("\"");
				throw new IllegalArgumentException(errMessage.toString(), e);
			} finally {
				errMessage.setLength(0);
			}
		}
    }
    
    /**
     * 將Date轉為java 8 LocalDateTime型別
     * 
     * @param date
     */
    public static LocalDateTime toLocalDateTime(Date dateTime) {
        return LocalDateTime.of(dateTime.getYear()+1900, dateTime.getMonth()+1, dateTime.getDate(), dateTime.getHours(), dateTime.getMinutes(), dateTime.getSeconds());
    }
    
    /**
     * 將Date轉為LocalDateTime型別, 並指定時,分,秒
     * 
     * @param date
     * @param hours 時
     * @param minutes 分
     * @param seconds 秒
     */
    public static LocalDateTime toLocalDateTime(Date dateTime, int hours, int minutes, int seconds) {
        return LocalDateTime.of(dateTime.getYear()+1900, dateTime.getMonth()+1, dateTime.getDate(), hours, minutes, seconds);
    }
    
    /**
     * 將LocalDate轉為LocalDateTime型別
     */
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
    }
    
    /**
     * 將LocalDate轉換為LocalDateTime型別, 並指定時,分,秒
     * 
     * @param date
     * @param hours 時
     * @param minutes 分
     * @param seconds 秒
     */
    public static LocalDateTime toLocalDateTime(LocalDate date, int hours, int minutes, int seconds) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hours, minutes, seconds);
    }
}
