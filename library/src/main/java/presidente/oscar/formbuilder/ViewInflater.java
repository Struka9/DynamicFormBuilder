package presidente.oscar.formbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscarr on 4/7/17.
 */

public class ViewInflater {
    private static final String TAG = ViewInflater.class.getSimpleName();

    private static ViewInflater mInstance;

    public static ViewInflater getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ViewInflater(context);
        }

        return mInstance;
    }

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private ViewInflater(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public View inflateFromJson(JSONObject jsonObject, ViewGroup parent) {
        try {
            String type = null;
            if (jsonObject.has(Constants.VIEW_TYPE)) {
                type = jsonObject.getString(Constants.VIEW_TYPE);
            } else {
                // No type so we just return null
                return null;
            }

            String name = null;
            if (jsonObject.has(Constants.VIEW_NAME)) {
                name = jsonObject.getString(Constants.VIEW_NAME);
            }

            ViewConfig config = new ViewConfig(name, type);

            View v = null;
            if (type.compareTo(Constants.TYPE_TEXT_INPUT) == 0) {
                v = inflateTextInput(jsonObject, parent);
            } else if (type.compareTo(Constants.TYPE_TEXT_AREA) == 0) {
                v = inflateTextArea(jsonObject, parent);
            } else if (type.compareTo(Constants.TYPE_RADIO_GROUP) == 0) {
                v = inflateRadioGroup(jsonObject, parent);
            }

            if (v != null) {
                v.setTag(config);
            }
            return v;

        } catch (JSONException e) {
            Util.logError(TAG, e.getMessage());
            return null;
        }
    }

    private EditText inflateTextArea(JSONObject jsonObject, ViewGroup parent) throws JSONException {
        EditText textInput = (EditText) mLayoutInflater.inflate(R.layout.layout_textarea, parent, false);

        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);
            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                textInput.setHint(props.getString(Constants.VIEW_PROPS_TITLE));
            }
        }

        if (jsonObject.has(Constants.VIEW_CONFIG)) {
            JSONObject config = jsonObject.getJSONObject(Constants.VIEW_CONFIG);
        }

        return textInput;
    }

    private EditText inflateTextInput(JSONObject jsonObject, ViewGroup parent) throws JSONException {
        EditText textInput = (EditText) mLayoutInflater.inflate(R.layout.layout_textinput, parent, false);


        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);
            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                textInput.setHint(props.getString(Constants.VIEW_PROPS_TITLE));
            }
        }

        if (jsonObject.has(Constants.VIEW_CONFIG)) {
            JSONObject config = jsonObject.getJSONObject(Constants.VIEW_CONFIG);
        }

        return textInput;
    }

    private RadioGroup inflateRadioGroup(JSONObject jsonObject, ViewGroup parent) throws JSONException {
        RadioGroup radioGroup = (RadioGroup) mLayoutInflater.inflate(R.layout.layout_radiougroup, parent, false);

        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);

            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                String title = props.getString(Constants.VIEW_PROPS_TITLE);

                // TODO: Add the title to the radio group
            }
        }

        if (jsonObject.has(Constants.VIEW_RADIO_OPTIONS)) {
            JSONArray optionsArray = jsonObject.getJSONArray(Constants.VIEW_RADIO_OPTIONS);

            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject jsonOption = optionsArray.getJSONObject(i);

                RadioButton option = (RadioButton) mLayoutInflater.inflate(R.layout.layout_radioubutton, radioGroup, true);
                if (jsonOption.has(Constants.VIEW_RADIO_OPTIONS_NAME))
                    option.setText(jsonOption.getString(Constants.VIEW_RADIO_OPTIONS_NAME));
            }
        }

        return radioGroup;
    }

    public class ViewConfig {
        public String type;
        public String name;

        public ViewConfig(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
