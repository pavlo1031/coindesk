package cathay.coindeskApi.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * 只保留留日期
	 * @param date
	 * @return
	 */
	public static Date truncateTime(Date val) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(val);
		return truncateTime(cal);
	}

	public static Date truncateTime(Calendar val) {
		Date date = val.getTime();
		long time = date.getTime();
		time -= val.get(Calendar.MILLISECOND);
		time -= (val.get(Calendar.SECOND) * 1000L);
		time -= (val.get(Calendar.MINUTE) * 60000L);
		time -= (val.get(Calendar.HOUR_OF_DAY) * 3600000L);
		return new Date(time);
	}

	public static LocalDate transToLocalDate(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate;
	}

	public static Date transToDate(LocalDateTime dateTime) {
		Date out = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		return out;
	}

	public static String dateFormat(Date date, CharSequence format) {
		if (date == null)
			throw new NullPointerException("The argument 'data' cannot be null");
		if (format == null)
			throw new NullPointerException("The argument 'format' cannot be null");
		
        SimpleDateFormat df = new SimpleDateFormat(format.toString());
        return df.format(date);
	}

	public static String dateFormat(Date date) {
		return dateFormat(date, "yyyy-MM-dd");
	}

	public static String dateTimeFormat(Date date) {
		return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date toDate(CharSequence date, CharSequence format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format.toString());
		return sdf.parse(date.toString());
	}

	public static Date toDate(CharSequence date, CharSequence format, boolean throwsExceptionOnfail) {
		if (date == null)
			throw new NullPointerException("The argument 'data' cannot be null");
		if (format == null)
			throw new NullPointerException("The argument 'format' cannot be null");
		
		SimpleDateFormat sdf = new SimpleDateFormat(format.toString());
		try {
			return sdf.parse(date.toString());
		}
		catch (ParseException e) {
			if (throwsExceptionOnfail)
				throw new RuntimeException(e);
		}
		return null;
	}
	
	public static Date toDate(CharSequence date) throws ParseException {
		return toDate(date, "yyyy-MM-dd");
	}
	
    /**
     * 將日期字串date轉為Date型別
     * 
     * @param date 日期字串
     * @param throwsExceptionOnfail 當throwsExceptionOnfail為true, 如果字串date不符預設格式時(yyyy-MM-dd) 會拋出exception, 否則傳回null
     */
	public static Date toDate(CharSequence date, boolean throwsExceptionOnfail) {
		return toDate(date, "yyyy-MM-dd", throwsExceptionOnfail);
	}

    /**
     * 將日期字串date轉為Date型別
     * 字串date不符預設格式時(yyyy-MM-dd), 只會回傳null (不會拋出exception)
     * 
     * @param date 日期字串
     */
	public static Date toDateQuitely(CharSequence date) {
		return toDate(date, "yyyy-MM-dd", false);
	}
	
	/**
     * 將日期字串date轉為Date型別
     * 字串不符格式format時, 只會回傳null (不會拋出exception)
     * 
     * @param date 日期字串
     * @param format 日期格式
     */
	public static Date toDateQuitely(CharSequence date, CharSequence format) {
		return toDate(date, format.toString(), false);
	}
	
    /**
     * 將日期字串date轉為Date型別
     * 字串不符預設格式時(yyyy-MM-dd HH:mm:ss), 只會回傳null (不會拋出exception)
     * 
     * @param date 日期字串
     */
	public static Date toDateTimeQuitely(CharSequence date) {
		return toDateTime(date, false);
	}
	
    /**
     * 將日期字串依預設格式(yyyy-MM-dd HH:mm:ss) 轉為Date型別
     * 
     * @param date 日期字串
     * @param throwsExceptionOnfail 當throwsExceptionOnfail為true, 如果字串date不符預設格式(yyyy-MM-dd HH:mm:ss) 會拋出exception, 否則傳回null
     */
	public static Date toDateTime(CharSequence date, boolean throwsExceptionOnfail) {
		return toDate(date, "yyyy-MM-dd HH:mm:ss", throwsExceptionOnfail);
	}
	
	public static Date toDateTime(CharSequence date) throws ParseException {
		return toDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static Date getDateAddMinute(int minutes) {
		Calendar currentTimeNow = Calendar.getInstance();
		currentTimeNow.add(Calendar.MINUTE, minutes);
		Date tenMinsFromNow = currentTimeNow.getTime();
		return tenMinsFromNow;
	}

	public static Date getDateAddDay(int days) {
		Calendar currentTimeNow = Calendar.getInstance();
		currentTimeNow.add(Calendar.DATE, days);
		Date tenMinsFromNow = currentTimeNow.getTime();
		return tenMinsFromNow;
	}

	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		Date toDate = cal.getTime();
		return toDate;
	}

	public static Date dateAddSec(Date date, int sec) {
		return dateAdd(date, sec, Calendar.SECOND);
	}

	public static Date dateAddMinute(Date date, int minutes) {
		return dateAdd(date, minutes, Calendar.MINUTE);
	}

	public static Date dateAddDay(Date date, int days) {
		return dateAdd(date, days, Calendar.DATE);
	}

	public static Date dateAdd(Date date, int value, int unitField) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(unitField, value);
		Date rtn = cal.getTime();
		return rtn;
	}

	public static String secondFormat(Integer time) {
		String timeName = "";
		if (time != null) {
			int min = (int) Math.floor(time / 60);
			if (min >= 1) {
				int hour = (int) Math.floor(min / 60);
				if (hour >= 1) {
					min = Math.floorMod(min, 60);
					timeName = hour + "時" + min + "分";
				} else {
					timeName = min + "分";
				}
			}
			int sec = Math.floorMod(time, 60);
			if (sec != 0) {
				timeName += sec + "秒";
			}
		}
		return timeName;
	}

	/**
	 *
	 * @param lockTime 鎖定時間
	 * @param expiredTime 過期時間
	 */
	public static boolean isExpired(Date lockTime, Date expiredTime) {
		if (lockTime == null || expiredTime == null) {
			return true;
		}
		if (lockTime != null && expiredTime.before(lockTime)) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param unlockTime 解鎖時間
	 * @param min
	 * @return
	 */
	public static boolean isLock(Date lockTime) {
		if (lockTime == null) {
			return false;
		}
		return new Date().before(lockTime);
	}

	/**
	 *
	 * @param lockTime 鎖定時間
	 * @param expiredTime 過期時間
	 */
	public static boolean isExpiredMin(Date lockTime, int min) {
		Date expiredTime = dateAddMinute(lockTime, min);
		if (lockTime == null || expiredTime == null) {
			return true;
		}
		if (lockTime != null && expiredTime.before(lockTime)) {
			return true;
		}
		return false;
	}

	public static boolean areDatesEqual(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
				cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
	}

	public static Date getOneMonthAgo(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1); // 减去一个月
		return calendar.getTime();
	}
	
	public static Date getStartOfYear(Date date) {
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		String startOfYearStr = yearFormatter.format(date) + "-01-01";
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(startOfYearStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date getEndOfYear(Date date) {
		SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
		String endOfYearStr = yearFormatter.format(date) + "-12-31";
		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(endOfYearStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public static long getDaysBetween(Date date1, Date date2) {
        LocalDate startDate = toLocalDate(date1);
        LocalDate endDate = toLocalDate(date2);
		if(startDate.equals(endDate)){
			return 1;
		}
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * 判斷是否 startDate <= date <= endDate 23:59:59
     */
    public static boolean isDateBetween(Date date, LocalDate startDate, LocalDate endDate) {
    	LocalDate dateTime = toLocalDate(date);
    	return (dateTime.equals(startDate) || dateTime.isAfter(startDate)) &&
        		(dateTime.equals(endDate)  || dateTime.isBefore(endDate));
    }
    
    /**
     * 判斷是否 startDate <= date <= endDate 23:59:59
     */
    public static boolean isDateBetween(Date date, Date startDate, Date endDate) {
    	Date startDate_ = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate());
    	Date endDate_ = new Date(endDate.getYear(), endDate.getMonth(), endDate.getDate());
    	return (date.equals(startDate_) || date.after(startDate_)) &&
        		(date.equals(endDate_)   || date.before(endDate_));
    }

    /**
     * 將日期字串date轉為LocalDate型別
     * 若日期字串不符合格式, 會拋出exception
     * 
     * @param date 日期字串
     * @param format
     */
    public static LocalDate toLocalDate(CharSequence s, String format) {
    	return toLocalDate(s, format, true);
    }
    
    /**
     * 將日期字串date轉為LocalDate型別
     * 
     * @param date 日期字串
     * @param format
     * @param throwsExceptionOnfail 當throwsExceptionOnfail為true, 如果字串date不符預設格式format時, 會拋出exception, 否則傳回null
     */
    public static LocalDate toLocalDate(CharSequence date, CharSequence format, boolean throwsExceptionOnfail) {
    	if (date == null)
			throw new NullPointerException("The argument 'data' cannot be null");
		if (format == null)
			throw new NullPointerException("The argument 'format' cannot be null");
		
    	Date date_ = null;
    	try {
			date_ = new SimpleDateFormat(format.toString()).parse(date.toString());
		} catch (ParseException e) {
			if (throwsExceptionOnfail)
				throw new RuntimeException(e);
			else
				return null;
		}
    	return toLocalDate(date_);
    }
    
    /**
     * 將Date轉為LocalDateTime型別
     * @param date
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 將Date轉為LocalDateTime型別
     * @param date
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.of(date.getYear()+1900, date.getMonth()+1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
    }
    
    /**
     * 將Date轉為LocalDateTime型別, 並指定時,分,秒
     * @param date
     * @param hours 時
     * @param minutes 分
     * @param seconds 秒
     */
    public static LocalDateTime toLocalDateTime(Date date, int hours, int minutes, int seconds) {
        return LocalDateTime.of(date.getYear()+1900, date.getMonth()+1, date.getDate(), hours, minutes, seconds);
    }
    
    /**
     * 將LocalDate轉為LocalDateTime型別
     */
    public static LocalDateTime toLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
    }
    
    /**
     * 將LocalDate轉為LocalDateTime型別, 並指定時,分,秒
     * @param date
     * @param hours 時
     * @param minutes 分
     * @param seconds 秒
     */
    public static LocalDateTime toLocalDateTime(LocalDate date, int hours, int minutes, int seconds) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hours, minutes, seconds);
    }
}
