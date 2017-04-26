package presidente.oscar.formbuilder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
            // name and type are required
            if (!jsonObject.has(Constants.VIEW_TYPE)) {
                return null;
            }

            String type = jsonObject.getString(Constants.VIEW_TYPE);

            View v = null;
            if (type.compareTo(Constants.TYPE_TEXT_INPUT) == 0) {
                v = inflateTextInput(jsonObject, parent);
                ((TextInputLayout)v).getEditText().setMaxLines(1);
            } else if (type.compareTo(Constants.TYPE_TEXT_AREA) == 0) {
                v = inflateTextInput(jsonObject, parent);
            } else if (type.compareTo(Constants.TYPE_RADIO_GROUP) == 0) {
                v = inflateRadioGroup(jsonObject, parent);
            } else if (type.compareTo(Constants.TYPE_CHECK_BOX) == 0) {
                v = inflateCheckbox(jsonObject, parent);
            }

            if (v != null) {
                v.setTag(jsonObject);
            }

            return v;
        } catch (JSONException e) {
            Util.logError(TAG, e.getMessage());
            return null;
        }
    }

    private View inflateCheckbox(JSONObject jsonObject, ViewGroup parent) throws JSONException {
        LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.layout_checkbox_group, parent, false);

        TextView titleTv = (TextView) linearLayout.getChildAt(0);

        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);

            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                String title = props.getString(Constants.VIEW_PROPS_TITLE);
                titleTv.setText(title);
            }
        }

        // In case the definition contains checked items
        JSONArray checkedItems = jsonObject.getJSONArray(Constants.JSON_VALUE);

        if (jsonObject.has(Constants.VIEW_RADIO_OPTIONS)) {
            JSONArray optionsArray = jsonObject.getJSONArray(Constants.VIEW_RADIO_OPTIONS);

            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject jsonOption = optionsArray.getJSONObject(i);

                CheckBox option = (CheckBox) mLayoutInflater.inflate(R.layout.layout_checkbox, linearLayout, false);

                option.setId(i);

                String optionName = null;
                if (jsonOption.has(Constants.VIEW_RADIO_OPTIONS_VALUE)) {
                    optionName = jsonOption.getString(Constants.VIEW_RADIO_OPTIONS_VALUE);
                    // TODO: We can omit the tag and use the only the text
                    option.setTag(optionName);
                    option.setText(optionName);
                }

                // Iterate through the checked items and see if we should check this option

                if (checkedItems != null) {
                    for (int j = 0; j < checkedItems.length(); j++) {
                        String checkedItem = checkedItems.getString(j);
                        if (checkedItem.compareTo(optionName) == 0) {
                            option.setChecked(true);
                        }
                    }
                }

                linearLayout.addView(option);
            }
        }


        return linearLayout;
    }

    private View inflateTextInput(JSONObject jsonObject, final ViewGroup parent) throws JSONException {

        TextInputLayout textInputLayout = (TextInputLayout) mLayoutInflater.inflate(R.layout.layout_textarea, parent, false);
        final TextInputEditText textInput = (TextInputEditText) textInputLayout.getEditText();

        // Is it a input view or textarea?
        String type = jsonObject.getString(Constants.VIEW_TYPE);

        String title = null;

        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);

            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                title = props.getString(Constants.VIEW_PROPS_TITLE);
                textInputLayout.setHint(title);
            }
        }

        // Check the edit text doesn't have an aswer already
        String value = jsonObject.getString(Constants.JSON_VALUE);
        textInput.setText(value);

        // If it's text are we don't set date or date time listeners
        if (type.compareTo(Constants.TYPE_TEXT_INPUT) == 0) {

            if (jsonObject.has(Constants.VIEW_CONFIG)) {
                JSONObject config = jsonObject.getJSONObject(Constants.VIEW_CONFIG);

                if (config.has(Constants.VIEW_CONFIG_TEXTINPUT_TYPE)) {
                    String inputType = config.getString(Constants.VIEW_CONFIG_TEXTINPUT_TYPE);

                    if (inputType.compareTo(Constants.INPUT_TYPE_TEXT) == 0) {
                        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    } else if (inputType.compareTo(Constants.INPUT_TYPE_NUMBER) == 0) {
                        textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else if (inputType.compareTo(Constants.INPUT_TYPE_PHONE) == 0) {
                        textInput.setInputType(InputType.TYPE_CLASS_PHONE);
                    } else if (inputType.compareTo(Constants.INPUT_TYPE_DATETIME) == 0) {
                        textInput.setFocusable(false);
                        textInput.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String timeSet = textInput.getText().toString();

                                Calendar calendar = Calendar.getInstance();

                                if (!timeSet.isEmpty()) {
                                    try {
                                        Date date = Util.parseTime(timeSet);
                                        calendar.setTime(date);
                                    } catch (ParseException e) {
                                        Util.logError(TAG, e.getMessage());
                                    }
                                }

                                TimePickerDialog dialog = new TimePickerDialog(parent.getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                                Calendar c = Calendar.getInstance();
                                                c.set(Calendar.HOUR_OF_DAY, hour);
                                                c.set(Calendar.MINUTE, minute);

                                                textInput.setText(Util.formatTime(c.getTime()));
                                            }
                                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                                dialog.show();
                            }
                        });
                    } else if (inputType.compareTo(Constants.INPUT_TYPE_DATE) == 0) {
                        textInput.setFocusable(false);
                        textInput.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String dateSet = textInput.getText().toString();

                                Calendar calendar = Calendar.getInstance();

                                if (!dateSet.isEmpty()) {
                                    try {
                                        Date date = Util.parseDate(dateSet);
                                        calendar.setTime(date);
                                    } catch (ParseException e) {
                                        Util.log(TAG, e.getMessage());
                                    }
                                }

                                DatePickerDialog dialog = new DatePickerDialog(parent.getContext(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.YEAR, year);
                                        c.set(Calendar.MONTH, month);
                                        c.set(Calendar.DAY_OF_MONTH, day);

                                        textInput.setText(Util.formatDate(c.getTime()));
                                    }
                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                                dialog.show();
                            }
                        });
                    }
                }
            }
        } else {
            textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        return textInputLayout;
    }

    private View inflateRadioGroup(JSONObject jsonObject, ViewGroup parent) throws JSONException {
        LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.layout_radiougroup, parent, false);

        TextView titleTv = (TextView) linearLayout.getChildAt(0);

        // We know the index of the radio group
        RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(1);

        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);

            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                String title = props.getString(Constants.VIEW_PROPS_TITLE);
                titleTv.setText(title);
            }
        }

        String checkedItem = jsonObject.getString(Constants.JSON_VALUE);

        if (jsonObject.has(Constants.VIEW_RADIO_OPTIONS)) {
            JSONArray optionsArray = jsonObject.getJSONArray(Constants.VIEW_RADIO_OPTIONS);

            for (int i = 0; i < optionsArray.length(); i++) {
                JSONObject jsonOption = optionsArray.getJSONObject(i);

                RadioButton option = (RadioButton) mLayoutInflater.inflate(R.layout.layout_radioubutton, radioGroup, false);

                option.setId(i);

                String optionId = null;
                if (jsonOption.has(Constants.VIEW_RADIO_OPTIONS_VALUE)) {
                    optionId = jsonOption.getString(Constants.VIEW_RADIO_OPTIONS_VALUE);
                    option.setTag(optionId);
                    option.setText(optionId);
                }

                radioGroup.addView(option);

                if (optionId != null && checkedItem != null && optionId.compareTo(checkedItem) == 0) {
                    option.setChecked(true);
                }
            }
        }

        return linearLayout;
    }
}
