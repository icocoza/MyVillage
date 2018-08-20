package com.ccz.myvillage.common;

import java.util.Date;

public class TimeUtils {

    public static String calcLastTime(Date date) {
        return calcLastTime(date.getTime());
    }

    public static String calcLastTime(long time) {
        long diff = System.currentTimeMillis() - time;
        if(diff < 60000)
            return "1분 이내";
        else if(diff < 3600000)
            return diff / 60000 + "분";
        else if(diff < 86400000)
            return diff / 3600000 + "시간";
        else if(diff < 2592000000l)
            return diff / 86400000 + "일";
        else if(diff < 31104000000l)
            return diff / 2592000000l + "개월";
        return diff / 31104000000l + "년";
    }

}
