package pl.softwaremill.smokedetector;


import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleLogLineAnalyser {

    public static int DATE_TIME_STRING_LENGTH = 12;
    public static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    // 2012-06-22 15:53:45,215
    public static boolean isItStartOfNewCommand(String line) {
        try {
            extractDateFromFirstSqlLine(line);
            return true;
        } catch (Exception e) { }

        return false;
    }

    public static Date extractDateFromFirstSqlLine(String line) {
        String potentialDateFragment = StringUtils.substring(line, 0, DATE_TIME_STRING_LENGTH);
        try {
            return formatter.parse(potentialDateFragment);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
