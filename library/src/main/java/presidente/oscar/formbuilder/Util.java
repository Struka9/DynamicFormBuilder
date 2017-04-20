package presidente.oscar.formbuilder;

import android.util.Log;

/**
 * Created by oscarr on 4/7/17.
 */

public class Util {
    private static final boolean DEBUG = true;

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
}
