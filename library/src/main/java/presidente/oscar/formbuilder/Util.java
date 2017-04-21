package presidente.oscar.formbuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

/**
 * Created by oscarr on 4/7/17.
 */

public class Util {
    private static final boolean DEBUG = true;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");

    public static void logError(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void log(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMAT.format(date);
    }

    public static Date parseDate(String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }

    public static Date parseTime(String time) throws ParseException {
        return TIME_FORMAT.parse(time);
    }
}
