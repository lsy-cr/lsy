package mathtest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String formatDate(Date date) {
        return DEFAULT_FORMAT.format(date);
    }

    public static String formatNow() {
        return formatDate(new Date());
    }
}
