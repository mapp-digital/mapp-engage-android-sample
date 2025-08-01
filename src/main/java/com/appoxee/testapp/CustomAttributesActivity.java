package com.appoxee.testapp;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.Appoxee;
import com.appoxee.GetCustomAttributesCallback;
import com.appoxee.testapp.databinding.ActivityCustomAttributesBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomAttributesActivity extends AppCompatActivity {

    private enum DataType {
        STRING,
        DATE,
        NUMBER,
        BOOLEAN
    }

    String[] types = new String[]{"String", "Number", "Boolean", "Date"};

    private ActivityCustomAttributesBinding binding;

    private Date selectedDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomAttributesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSpinner();

        binding.btnSet.setOnClickListener(v -> {
            Editable keyEditable = binding.tvKey.getText();
            Editable valueEditable = binding.tvValue.getText();

            String key = keyEditable != null ? keyEditable.toString() : null;
            String value = valueEditable != null ? valueEditable.toString() : null;
            String type = types[binding.spinnerTypes.getSelectedItemPosition()];
            DataType dataType = getType(type);
            if (!TextUtils.isEmpty(key)) {
                switch (dataType) {
                    case DATE -> setCustomAttribute(key, selectedDate);
                    case BOOLEAN -> setCustomAttribute(key, binding.switchCompat.isChecked());
                    case NUMBER -> setCustomAttribute(key, parseString(value));
                    case STRING -> setCustomAttribute(key, value);
                }
            }
        });

        binding.btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth);
                selectedDate = cal.getTime();
                updateBtnSelectDate();
            }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        binding.btnGet.setOnClickListener(v -> {
//            String key = binding.tvKeyGet.getText() != null ? binding.tvKeyGet.getText().toString() : null;
//            if (!TextUtils.isEmpty(key)) {
//                String attribute = Appoxee.instance().getAttributeStringValue(key);
//                show(key, attribute);
//            }

            Appoxee.instance().getCustomAttributes(
                    List.of("multi_attr_param1",
                            "multi_attr_param2",
                            "multi_attr_param3"),
                    new GetCustomAttributesCallback() {
                        @Override
                        public void onSuccess(Map<String, String> customAttributes) {
                            StringBuilder sb = new StringBuilder();
                            customAttributes.forEach((key, value) -> {
                                sb.append(key).append(" : ").append(value).append("\n");
                            });
                            show("Custom attributes", sb.toString());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            show("Error getting custom attributes", errorMessage);
                        }
                    });
        });

        binding.btnDelete.setOnClickListener(v -> {
            String key = binding.tvKeyGet.getText() != null ? binding.tvKeyGet.getText().toString() : null;
            if (!TextUtils.isEmpty(key)) {
                Appoxee.instance().removeAttribute(key);
                show("Deleted", "Attribute '" + key + "' was deleted!");
            }
        });

        binding.btnSetMultipleParameters.setOnClickListener(v -> {
//            Appoxee.instance().setAttribute("firstName", "Mark");
//            Appoxee.instance().setAttribute("lastName", "Twain");
//            Appoxee.instance().setAttribute("currency","EUR");
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("multi_attr_param1", "Lorem ipsum dolor sit amet");
            attributes.put("multi_attr_param2", 176);
            attributes.put("multi_attr_param3", false);
            Appoxee.instance().setAttributes(attributes);
        });

    }

    private void updateBtnSelectDate() {
        binding.btnSelectDate.setText(selectedDate != null ? SimpleDateFormat.getDateInstance().format(selectedDate) : "Click to select date");
    }

    private void setupSpinner() {
        List<Map<String, String>> data = new ArrayList<>();

        for (String s : types) {
            Map<String, String> map = new HashMap<>();
            map.put("type", s);
            data.add(map);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner_row,
                types
        );

        adapter.setDropDownViewResource(R.layout.item_spinner_row);

        binding.spinnerTypes.setAdapter(adapter);
        binding.spinnerTypes.setSelection(0);

        binding.spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DataType selectedType = getType(types[position]);
                switch (selectedType) {
                    case DATE -> {
                        binding.tvValue.setVisibility(GONE);
                        binding.switchCompat.setVisibility(GONE);
                        binding.btnSelectDate.setVisibility(VISIBLE);
                    }
                    case NUMBER -> {
                        binding.switchCompat.setVisibility(GONE);
                        binding.btnSelectDate.setVisibility(GONE);
                        binding.tvValue.setVisibility(VISIBLE);
                        binding.tvValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    }
                    case BOOLEAN -> {
                        binding.tvValue.setVisibility(GONE);
                        binding.btnSelectDate.setVisibility(GONE);
                        binding.switchCompat.setVisibility(VISIBLE);
                    }
                    default -> {
                        binding.switchCompat.setVisibility(GONE);
                        binding.btnSelectDate.setVisibility(GONE);
                        binding.tvValue.setVisibility(VISIBLE);
                        binding.tvValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int parseString(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private DataType getType(String type) {
        String mType = type != null ? type.toLowerCase() : "string";
        return switch (mType) {
            case "date" -> DataType.DATE;
            case "number" -> DataType.NUMBER;
            case "boolean" -> DataType.BOOLEAN;
            default -> DataType.STRING;
        };
    }

    private <T> void setCustomAttribute(String key, T value) {
        if (value instanceof String) {
            Appoxee.instance().setAttribute(key, (String) value);
        } else if (value instanceof Date) {
            Appoxee.instance().setAttribute(key, (Date) value);
        } else if (value instanceof Number) {
            Appoxee.instance().setAttribute(key, (Number) value);
        } else if (value instanceof Boolean) {
            Appoxee.instance().setAttribute(key, (Boolean) value);
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error")
                    .setMessage("Type not supported")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    private void show(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }
}