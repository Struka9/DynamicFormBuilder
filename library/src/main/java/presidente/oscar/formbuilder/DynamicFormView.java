package presidente.oscar.formbuilder;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscarr on 4/7/17.
 */

public class DynamicFormView extends LinearLayout {
    private static final String TAG = DynamicFormView.class.getSimpleName();

    private LinearLayout mContentLayout;
    private ViewInflater mViewInflater;
    private TextView mTitleTv;

    public DynamicFormView(Context context) {
        this(context, null);
    }

    public DynamicFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the root view and add it to this
        layoutInflater.inflate(R.layout.layout_dynamic_form, this, true);

        // Get the reference to the content view and submission button
        mTitleTv = (TextView) this.findViewById(R.id.title);
        mContentLayout = (LinearLayout) this.findViewById(R.id.content);
        mViewInflater = ViewInflater.getInstance(context);
    }

    public void createView(JSONArray jsonArray) {
        createView(jsonArray, true);
    }

    public void createView(JSONArray jsonArray, boolean allowInput) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                View inflatedView = mViewInflater.inflateFromJson(item, this);

                if (inflatedView != null) {
                    inflatedView.setEnabled(allowInput);
                    mContentLayout.addView(inflatedView);
                }

            } catch (JSONException e) {
                Util.logError(TAG, e.getMessage());
            }
        }
    }

    public void setTitle(String text) {
        mTitleTv.setText(text);
    }

    /**
     * Builds the JSON structure to include the values entered in the form
     *
     * @return
     */
    public JSONArray buildJson() throws JSONException {

        JSONArray viewsArray = new JSONArray();
        for (int i = 0; i < mContentLayout.getChildCount(); i++) {
            try {
                View v = mContentLayout.getChildAt(i);

                JSONObject viewConfig = (JSONObject) v.getTag();

                // We know that for this view to exist 'type' and 'name' props had to be present
                String type = viewConfig.getString(Constants.VIEW_TYPE);

                Object value = null;
                if (type.compareTo(Constants.TYPE_TEXT_INPUT) == 0 ||
                        type.compareTo(Constants.TYPE_TEXT_AREA) == 0) {
                    String textAsString = ((TextInputLayout) v).getEditText().getText().toString();

                    // Get the config
                    if (viewConfig.has(Constants.VIEW_CONFIG)) {
                        JSONObject config = viewConfig.getJSONObject(Constants.VIEW_CONFIG);

                        if (config.has(Constants.VIEW_CONFIG_TEXTINPUT_TYPE) &&
                                config.getString(Constants.VIEW_CONFIG_TEXTINPUT_TYPE).compareTo(Constants.INPUT_TYPE_NUMBER) == 0) {

                            viewConfig.put(Constants.JSON_VALUE, Double.valueOf(textAsString));
                        } else {
                            viewConfig.put(Constants.JSON_VALUE, textAsString);
                        }
                    }

                } else if (type.compareTo(Constants.TYPE_RADIO_GROUP) == 0) {
                    // If it is a radiogroup we only care for the selected radio button
                    // The root layout is a LinearLayout
                    RadioGroup radioGroup = (RadioGroup) ((LinearLayout)v).getChildAt(1);
                    RadioButton selectedRadio = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

                    if (selectedRadio != null) {
                        // The value will be the name of the selected option
                        value = selectedRadio.getTag();

                        if (value != null) {
                            viewConfig.put(Constants.JSON_VALUE, value);
                        }
                    }

                } else if (type.compareTo(Constants.TYPE_CHECK_BOX) == 0) {

                    LinearLayout asLinearLayout = (LinearLayout)v;

                    JSONArray optionsArray = new JSONArray();

                    // We start at '1' since '0' is occupied by the title view
                    for (int j = 1; j < asLinearLayout.getChildCount(); j++) {
                        CheckBox checkBox = (CheckBox) asLinearLayout.getChildAt(j);
                        String checkboxName = (String) checkBox.getTag();

                        JSONObject checkboxAsJson = new JSONObject();
                        checkboxAsJson.put("value", checkboxName);

                        checkboxAsJson.put("selected", checkBox.isSelected());

                        optionsArray.put(checkboxAsJson);
                    }

                    viewConfig.put("options", optionsArray);
                }

                viewsArray.put(viewConfig);
            } catch (JSONException e) {
                Util.logError(TAG, e.getMessage());
            }
        }

        return viewsArray;
    }
}
