package presidente.oscar.formbuilder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
                v = inflateTextArea(jsonObject, parent);

                ((TextInputLayout)v).getEditText().setMaxLines(1);

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

    private View inflateTextArea(JSONObject jsonObject, final ViewGroup parent) throws JSONException {
        TextInputLayout textInputLayout = (TextInputLayout) mLayoutInflater.inflate(R.layout.layout_textarea, parent, false);

        final EditText textInput = textInputLayout.getEditText();

        String title = null;
        if (jsonObject.has(Constants.VIEW_PROPS)) {
            JSONObject props = jsonObject.getJSONObject(Constants.VIEW_PROPS);
            if (props.has(Constants.VIEW_PROPS_TITLE)) {
                title = props.getString(Constants.VIEW_PROPS_TITLE);
                textInput.setHint(title);
            }
        }

        if (jsonObject.has(Constants.VIEW_CONFIG)) {
            JSONObject config = jsonObject.getJSONObject(Constants.VIEW_CONFIG);

            if (config.has(Constants.VIEW_CONFIG_TEXTINPUT_TYPE)) {
                String type = config.getString(Constants.VIEW_CONFIG_TEXTINPUT_TYPE);

                if (type.compareTo(Constants.INPUT_TYPE_TEXT) == 0) {
                    textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (type.compareTo(Constants.INPUT_TYPE_NUMBER) == 0) {
                    textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (type.compareTo(Constants.INPUT_TYPE_PHONE) == 0) {
                    textInput.setInputType(InputType.TYPE_CLASS_PHONE);
                } else if (type.compareTo(Constants.INPUT_TYPE_DATETIME) == 0) {
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
                } else if (type.compareTo(Constants.INPUT_TYPE_DATE) == 0) {
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

        return textInputLayout;
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

                RadioButton option = (RadioButton) mLayoutInflater.inflate(R.layout.layout_radioubutton, radioGroup, false);
                if (jsonOption.has(Constants.VIEW_RADIO_OPTIONS_NAME))
                    option.setText(jsonOption.getString(Constants.VIEW_RADIO_OPTIONS_NAME));

                radioGroup.addView(option);
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
