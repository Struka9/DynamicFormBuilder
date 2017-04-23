package presidente.oscar.formbuilder;

import java.util.HashMap;

/**
 * Created by oscarr on 4/7/17.
 */

public class Constants {
    public static final String TYPE_TEXT_INPUT = "input";
    public static final String TYPE_TEXT_AREA = "textarea";
    public static final String TYPE_RADIO_GROUP = "multipleChoices";
    public static final String TYPE_CHECK_BOX = "checkboxes";
    public static final String TYPE_MATRIX = "matrix";

    public final static HashMap<String, Integer> SUPPORTED_WIDGETS;
    public static final String VIEW_NAME = "name";
    public static final String VIEW_RADIO_OPTIONS = "options";
    public static final String VIEW_RADIO_OPTIONS_TITLE = "title";
    public static final String VIEW_RADIO_OPTIONS_VALUE = "value";
    public static final String VIEW_CONFIG_TEXTINPUT_TYPE = "type";

    static {
        SUPPORTED_WIDGETS = new HashMap<>();
        SUPPORTED_WIDGETS.put(TYPE_TEXT_AREA, R.layout.layout_textarea);
        SUPPORTED_WIDGETS.put(TYPE_RADIO_GROUP, R.layout.layout_radiougroup);
        SUPPORTED_WIDGETS.put(TYPE_CHECK_BOX, R.layout.layout_checkbox);
//        SUPPORTED_WIDGETS.put(TYPE_MATRIX,)
    }

    public static final String VIEW_TYPE = "type";

    public static final String VIEW_CONFIG = "config";

    public static final String VIEW_PROPS = "props";
    public static final String VIEW_PROPS_TITLE = "title";
    // The keys to use in the JSON sent back to the server
    public static final String JSON_VALUE = "value";

    public static final String INPUT_TYPE_TEXT = "text";
    public static final String INPUT_TYPE_NUMBER = "number";
    public static final String INPUT_TYPE_PHONE = "phone";
    public static final String INPUT_TYPE_DATE = "date";
    public static final String INPUT_TYPE_DATETIME = "datetime";
}
