package com.tencent.wxcloudrun.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期处理
 */
@Slf4j
public class DateUtils {

    /**
     * yyyy-MM-dd HH:mm:ss校验格式
     */
    public final static String ELDATETIME = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-9]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";

    public static final DateTimeFormatter yyMMddHHmmss = DateTimeFormatter.ofPattern("yyMMddHHmmss");


    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 线程安全的时间格式化类
     */
    public final static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * 线程安全的时间格式化类
     */
    public final static DateTimeFormatter date_df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }


    /**
     * 根据pattern格式化时间
     *
     * @param date    时间
     * @param pattern 时间格式
     * @return
     */
    public static String format(Date date, String pattern) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        String dateStr = "";
        if (pattern.equals(DateUtils.DATE_TIME_PATTERN)) {
            dateStr = df.format(localDateTime);
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            dateStr = df.format(localDateTime);
        }
        return dateStr;
    }

    /**
     * 时间比较
     *
     * @param dateBefore
     * @param dateAfter
     * @return 如果时间一样,返回值是0;
     *         如果dateBefore在dateAfter之后，返回值是大于0；
     *         如果dateBefore在dateAfter之前，返回值是小于0；
     * @throws Exception
     */
    public static int dateCompareTo(Date dateBefore, Date dateAfter) throws Exception {
        if (ObjectUtils.isEmpty(dateBefore)) {
            throw new Exception("比较中的dateBefore不能为空");
        }
        if (ObjectUtils.isEmpty(dateAfter)) {
            throw new Exception("比较中的dateAfter不能为空");
        }
        return dateBefore.compareTo(dateAfter);
    }


    /**
     * 计算距离现在多久，非精确
     *
     * @param date
     * @return
     */
    public static String getTimeBefore(Date date) {
        Date now = new Date();
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        String r = "";
        if (day > 0) {
            r += day + "天";
        } else if (hour > 0) {
            r += hour + "小时";
        } else if (min > 0) {
            r += min + "分";
        } else if (s > 0) {
            r += s + "秒";
        }
        r += "前";
        return r;
    }


    /**
     * 计算距离现在多久，精确
     *
     * @param date
     * @return
     */
    public static String getTimeBeforeAccurate(Date date) {
        Date now = new Date();
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        String r = "";
        if (day > 0) {
            r += day + "天";
        }
        if (hour > 0) {
            r += hour + "小时";
        }
        if (min > 0) {
            r += min + "分";
        }
        if (s > 0) {
            r += s + "秒";
        }
        r += "前";
        return r;
    }


    /**
     * 计算距离现在多久，精确
     * @param currentDate
     * @param datefore
     * @return
     */
    public static String getTimeBeforeAccurate(Date currentDate,Date datefore) {
        String r = "";
        try {
            long l = currentDate.getTime() - datefore.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            long hour = (l / (60 * 60 * 1000) - day * 24);
            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

            if (day > 0) {
                r += day + "天";
            }
            if (hour > 0) {
                r += hour + "小时";
            }
            if (min > 0) {
                r += min + "分";
            }
            if (s >= 0) {
                r += s + "秒";
            }
            r += "前";

        } catch (Exception e) {
            e.printStackTrace();
            r = "0";
        }
        return r;
    }


    public static int xhTime(Date startDate) {
        long a = new Date().getTime();
        long b = startDate.getTime();
        int c = (int) ((a - b) / 1000);
        return c;
    }


    /**
     * 根据pattern格式，解析时间
     *
     * @param dateStr 时间
     * @param pattern 格式
     * @return
     */
    public static Date parseDateTime(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        boolean flag = validDateTime(dateStr);
        if (flag) {
            LocalDateTime localDateTime = null;
            if (pattern.equals(DateUtils.DATE_TIME_PATTERN)) {
                localDateTime = LocalDateTime.parse(dateStr, df);
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
            }
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = localDateTime.atZone(zoneId);
            Date date = Date.from(zdt.toInstant());
            return date;
        }
        return null;
    }



    /**
     * 根据pattern格式，解析时间
     *
     * @param dateStr 时间
     * @param pattern 格式
     * @return
     */
    public static Date parseDate(String dateStr, String pattern) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        boolean flag = validDateTime(dateStr);
        if (flag) {
            LocalDate localDate = null;
            if (pattern.equals(DateUtils.DATE_TIME_PATTERN)) {
                localDate = LocalDate.parse(dateStr, df);
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
                localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            }
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant();
            Date date = Date.from(instant);
            return date;
        }
        return null;
    }

    public static LocalDateTime parse(String str) {
        try {
            return LocalDateTime.parse(str, yyMMddHHmmss);
        } catch (Exception e) {
            log.error("日期格式错误：[{}]", str);
            return null;
        }
    }



    public static LocalDate parseDate(String str) {
        try {
            return LocalDate.parse(str, date_df);
        } catch (Exception e) {
            log.error("日期格式错误：[{}]", str);
            return null;
        }
    }



    /**
     * 校验日期时间格式的方法(只能校验格式, yyyy-MM-dd 或是yyyy-MM-dd HH:mm:ss)
     * 如果校验完之后，加df.setLenient(true);将
     *
     * @param dateTimeStr
     * @return
     */
    public static boolean validDateTime(String dateTimeStr) {
        dateTimeStr = StringUtils.trim(dateTimeStr);
        if (StringUtils.isBlank(dateTimeStr)) {
            return false;
        }
        Pattern p = Pattern.compile(ELDATETIME);
        Matcher m = p.matcher(dateTimeStr);
        boolean flag = m.matches();
        return flag;
    }


    /**
     * 从一个时间计算，或者是这个时间之后的日期时间，或是之前的日期时间
     * @param date 计算日期时间的基础时间
     * @param timeUnit
     *        时间单位，如果是小时， Calendar.HOUR
     *        时间单位，如果是分钟， Calendar.MINUTE
     *        时间单位，如果是秒，   Calendar.SECOND
     *        时间单位，如果是日，  Calendar.DATE
     *        时间单位，如果是月， Calendar.MONTH
     *        时间单位，如果是年， Calendar.YEAR
     * @param amount 传入date时间的差值，支持整数
     * @return
     */
    public static Date calculateDate(Date date, int timeUnit, int amount) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(timeUnit, amount);
        return calendar.getTime();
    }


    /**
     * 根据秒级别的时间戳转换成时间
     * @param timestamp
     * @return
     */
    public static Date timestampTransitionDate(Long timestamp) {
        // 将秒级时间戳转换为 Instant 对象
        Instant instant = Instant.ofEpochSecond(timestamp);
        // 将 Instant 对象转换为 LocalDateTime 对象
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateTime.format(dateTimeFormatter);
        Date date = parseDateTime(formattedDate, DateUtils.DATE_TIME_PATTERN);
        return date;
    }


    public static void main(String[] args) throws Exception {
       /* Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1 );
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String todayStr = dateFormat.format(calendar.getTime());
        System.out.println(todayStr);*/
        String beginTime = "2018-07-28 14:42:32";
        String endTime = "2018-07-29 12:26:32";
        Date date1 = DateUtils.parseDate(beginTime, DateUtils.DATE_TIME_PATTERN);
        Date date2 = DateUtils.parseDate(endTime, DateUtils.DATE_TIME_PATTERN);
        int compare = DateUtils.dateCompareTo(date1, date2);
        System.out.println(compare);
    }


}
