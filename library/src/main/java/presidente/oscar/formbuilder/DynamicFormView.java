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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscarr on 4/7/17.
 */

public class DynamicFormView extends FrameLayout {
    private static final String TAG = DynamicFormView.class.getSimpleName();

    private LinearLayout mContentLayout;
    private ViewInflater mViewInflater;

    private LinearLayout mRootLayout;

    public DynamicFormView(Context context) {
        this(context, null);
    }

    public DynamicFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the root view and add it to this
        View root = layoutInflater.inflate(R.layout.layout_dynamic_form, this, true);

        mRootLayout = (LinearLayout) findViewById(R.id.content);

        // Get the reference to the content view and submission button
        mContentLayout = (LinearLayout) root.findViewById(R.id.content);
        mViewInflater = ViewInflater.getInstance(context);
    }

    public void createView(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                View inflatedView = mViewInflater.inflateFromJson(item, this);

                if (inflatedView != null) {
                    mRootLayout.addView(inflatedView);
                }
            } catch (JSONException e) {
                Util.logError(TAG, e.getMessage());
            }
        }
    }

    /**
     * Builds the JSON structure to include the values entered in the form
     *
     * @return
     */
    public JSONObject buildJson() {
        JSONObject rootObject = new JSONObject();

        for (int i = 0; i < this.getChildCount(); i++) {
            try {
                View v = this.getChildAt(i);

                JSONObject jsonItem = new JSONObject();

                // This view config includes the name and type of the inflated view
                ViewInflater.ViewConfig viewConfig = (ViewInflater.ViewConfig) v.getTag();

                jsonItem.put(Constants.VIEW_TYPE,
                        viewConfig.type);

                Object value = null;
                if (viewConfig.type.compareTo(Constants.TYPE_TEXT_INPUT) == 0 ||
                        viewConfig.type.compareTo(Constants.TYPE_TEXT_AREA) == 0) {
                    value = ((TextInputLayout) v).getEditText().getText().toString();

                    if (value != null) {
                        jsonItem.put(Constants.JSON_VALUE, value);
                    }

                } else if (viewConfig.type.compareTo(Constants.TYPE_RADIO_GROUP) == 0) {
                    // If it is a radiogroup we only care for the selected radio button
                    // The root layout is a LinearLayout
                    RadioGroup radioGroup = (RadioGroup) ((LinearLayout)v).getChildAt(1);
                    RadioButton selectedRadio = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    // The value will be the name of the selected option
                    value = selectedRadio.getTag();

                    if (value != null) {
                        jsonItem.put(Constants.JSON_VALUE, value);
                    }

                } else if (viewConfig.type.compareTo(Constants.TYPE_CHECK_BOX) == 0) {

                    JSONArray checkedItems = new JSONArray();

                    LinearLayout asLinearLayout = (LinearLayout)v;

                    // We start at '1' since '0' is occupied by the title view
                    for (int j = 1; j < asLinearLayout.getChildCount(); j++) {
                        CheckBox checkBox = (CheckBox) asLinearLayout.getChildAt(j);

                        String checkboxName = (String) checkBox.getTag();

                        if (checkBox.isChecked()) {
                            checkedItems.put(checkboxName);
                        }

                    }

                    jsonItem.put(Constants.JSON_CHECKED, checkedItems);
                }

                rootObject.put(viewConfig.name, jsonItem);
            } catch (JSONException e) {
                Util.logError(TAG, e.getMessage());
            }
        }

        return rootObject;
    }
}
