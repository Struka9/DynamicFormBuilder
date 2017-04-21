package presidente.oscar.formbuilder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

        // Get the reference to the content view and submission button
        mContentLayout = (LinearLayout) root.findViewById(R.id.content);
        mViewInflater = ViewInflater.getInstance(context);
    }

    public void createView(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                View inflatedView = mViewInflater.inflateFromJson(item, this);

                if (inflatedView != null)
                    this.addView(inflatedView);
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

                ViewInflater.ViewConfig viewConfig = (ViewInflater.ViewConfig) v.getTag();

                jsonItem.put(Constants.VIEW_TYPE,
                        viewConfig.type);

                Object value = null;
                if (viewConfig.type.compareTo(Constants.TYPE_TEXT_INPUT) == 0 ||
                        viewConfig.type.compareTo(Constants.TYPE_TEXT_AREA) == 0) {
                    value = ((EditText) v).getText().toString();
                }

                if (value != null) {
                    jsonItem.put(Constants.JSON_VALUE, value);
                }

                rootObject.put(viewConfig.name, jsonItem);
            } catch (JSONException e) {
                Util.logError(TAG, e.getMessage());
            }
        }

        return rootObject;
    }
}
