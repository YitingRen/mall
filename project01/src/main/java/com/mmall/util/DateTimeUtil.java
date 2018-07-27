package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by geely
 */
public class DateTimeUtil {
    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";


    public static Date strToDate(String dataTimeStr){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(dataTimeStr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dataTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if(date==null){
            return StringUtils.EMPTY;
        }
        String dateString = dateToStr(date);
        return dateString;
    }


}
