package presidente.oscar.formbuilder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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
            }

            if (v != null) {
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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

    public class ViewConfig {
        public String type;
        public String name;

        public ViewConfig(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
