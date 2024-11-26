package com.dataour.bifrost.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期处理
 *
 * @Author JASON
 * @version V1.0
 * @ClassName: DateUtils
 * @Date 2024-01-10 00:53
 */
@Slf4j
public class DateUtils {
    public static final SimpleDateFormat dateFormat3 = getDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat dateFormat = getDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateFormat2 = getDateFormat("yyyyMMdd");
    private static final SimpleDateFormat defaultFormat = getDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat defaultFormat2 = getDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final SimpleDateFormat fullTimeFormat = getDateFormat("yyyyMMddHHmmssSSS");
    private static final SimpleDateFormat dateTimeFormat = getDateFormat("yyyyMMddHHmmss");

    /**
     * 计算日期之间的天数差
     */
    public static int calDaysDiff(Date fromDate, Date toDate) {
        // 创建两个Calendar对象
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(fromDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(toDate);
        return calendar2.get(Calendar.DAY_OF_YEAR) - calendar1.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatDate(Date Date) {
        return defaultFormat.format(Date);
    }

    public static String formatFullDate(Date Date) {
        return defaultFormat2.format(Date);
    }

    public static String formatDate3(Date Date) {
        if (Date == null) {
            return "";
        }
        return dateFormat3.format(Date);
    }

    public static Date parseDate(String Date) {
        try {
            return defaultFormat.parse(Date);
        } catch (ParseException e) {
            log.error("", e);
        }
        return null;
    }

    public static Date parseFullDate(String Date) {
        try {
            return defaultFormat2.parse(Date);
        } catch (ParseException e) {
            log.error("", e);
        }
        return null;
    }

    public static String formatDay(Date Date) {
        return dateFormat.format(Date);
    }

    public static Date parseDay(String Date) {
        try {
            return dateFormat.parse(Date);
        } catch (ParseException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * @return
     */
    public static Date getZeroClockTime() {
        return getZeroClockTime(null);
    }

    /**
     * @return
     */
    public static Date getZeroClockTime(Date Date) {
        Calendar calendar = Calendar.getInstance();
        if (Date != null) {
            calendar.setTime(Date);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getDayLastTime(Date Date) {
        Calendar calendar = Calendar.getInstance();
        if (Date != null) {
            calendar.setTime(Date);
        }
        // 将时、分、秒设置为23:59:59
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
//        calendar.set(Calendar.MILLISECOND, 900);
        return calendar.getTime();
    }

    public static SimpleDateFormat getDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 自动生成上一周的时间范围
     * [fromTime,endTime]
     *
     * @return
     */
    public static TimeRange getLastWeekTimeRange() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        return getTimeRange(currentDate.getYear(), currentDate.get(WeekFields.ISO.weekOfWeekBasedYear()) - 1);
    }

    /**
     * [fromTime,endTime]
     *
     * @return
     */
    public static TimeRange getTimeRange(Integer year, Integer weekOfYear) {
        // 构建指定年份和周数的LocalDateTime对象
        LocalDateTime startOfWeek = LocalDateTime.of(year, 1, 1, 0, 0, 0).with(TemporalAdjusters.firstDayOfYear()).plusWeeks(weekOfYear - 1).with(DayOfWeek.MONDAY);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        // 将LocalDateTime转换为Date类型
        Date startDate = Date.from(startOfWeek.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atZone(ZoneId.systemDefault()).toInstant());
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(startDate);
        timeRange.setEndTime(endDate);
        return timeRange;
    }

    public static TimeRange getLastDayTimeRange(Date Date) {
        Calendar calendar = Calendar.getInstance();
        if (Date != null) {
            calendar.setTime(Date);
        }
        calendar.add(Calendar.DATE, -1);
        TimeRange timeRange = new TimeRange();
        timeRange.setStartTime(getZeroClockTime(calendar.getTime()));
        timeRange.setEndTime(getDayLastTime(calendar.getTime()));
        return timeRange;
    }

    /**
     * 获取前一天的最后一秒时间
     *
     * @return
     */
    public static Date getPreviousDayLastTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return getDayLastTime(calendar.getTime());
    }

    @Data
    public static class TimeRange {
        private Date startTime;
        private Date endTime;

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = parseDate(startTime);
        }

        public void setEndTime(String endTime) {
            this.endTime = parseDate(endTime);
        }

    }
}
