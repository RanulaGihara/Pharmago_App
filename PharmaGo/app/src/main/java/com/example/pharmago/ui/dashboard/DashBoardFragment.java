package com.example.pharmago.ui.dashboard;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.pharmago.R;
import com.example.pharmago.controller.BaseController;
import com.example.pharmago.model.CustomNameValuePair;
import com.example.pharmago.model.User;
import com.example.pharmago.util.BitmapUtility;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fr.ganfra.materialspinner.MaterialSpinner;

public class DashBoardFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static DashBoardFragment getInstance() {
        return new DashBoardFragment();
    }

    private BarChart chart;
    private Handler handler;
    private SweetAlertDialog dialog;
    private MaterialSpinner spinnerType;
    private ArrayAdapter typeArrayAdapter;
    private int type;
    private TextView textDate,predictedValueText;
    private MaterialButton buttonSearch;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        chart = root.findViewById(R.id.chart1);
        spinnerType = root.findViewById(R.id.spinnerType);
        buttonSearch = root.findViewById(R.id.buttonSearch);
        textDate = root.findViewById(R.id.textDate);
        predictedValueText= root.findViewById(R.id.predictedValueText);
        ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();
        HashMap<String, Object> map1 = new HashMap<String, Object>() {
            @NonNull
            @Override
            public String toString() {
                return (String) super.get("typeName");
            }
        };
        map1.put("typeName", "Paracetamol");
        map1.put("typeId", 1);

        hashMaps.add(map1);
        HashMap<String, Object> map2 = new HashMap<String, Object>() {
            @NonNull
            @Override
            public String toString() {
                return (String) super.get("typeName");
            }
        };
        map2.put("typeName", "Vitamin C");
        map2.put("typeId", 2);

        hashMaps.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>() {
            @NonNull
            @Override
            public String toString() {
                return (String) super.get("typeName");
            }
        };
        map3.put("typeName", "Amoxicillin");
        map3.put("typeId", 3);

        hashMaps.add(map3);

        typeArrayAdapter = new ArrayAdapter<HashMap<String, Object>>(getActivity(), R.layout.simple_dropdown_item, hashMaps);
        typeArrayAdapter.setDropDownViewResource(R.layout.simple_dropdown_item);
        spinnerType.setAdapter(typeArrayAdapter);
        spinnerType.setPaddingSafe(0, 0, 0, 0);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    if (position == -1) {
                        type = 0;

                        return;
                    }

                    type = (Integer) hashMaps.get(position).get("typeId");

                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //  getMedicinePredictions();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    Toast.makeText(getActivity(), "Please select a type", Toast.LENGTH_SHORT).show();
                } else if (diffMonth <= 0) {
                    Toast.makeText(getActivity(), "Please select a valid day", Toast.LENGTH_SHORT).show();
                } else {
                    getMedicinePredictions(type);
                }

            }
        });

        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat formatY = new SimpleDateFormat("yyyy");
        SimpleDateFormat formatM = new SimpleDateFormat("MM");
        SimpleDateFormat formatD = new SimpleDateFormat("dd");


        final String yearString = formatY.format(currentDate);
        final String monthString = formatM.format(currentDate);

        textDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), DashBoardFragment.this,
                            Integer.parseInt(yearString), Integer.parseInt(monthString) - 1, 1);

                    datePickerDialog.show();


                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
            }

        });
        return root;

    }


    private void setData(ArrayList<HashMap<String, Object>> hashMaps) {

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (HashMap<String, Object> hashMap : hashMaps) {
            int i = (Integer) hashMap.get("index") + 1;
            double d = (Double) hashMap.get("value");
            float f = (float) d;
            values.add(new BarEntry(i, f));
        }


        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "demand");

            set1.setDrawIcons(false);


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(1.0f);

            chart.setData(data);
            chart.notifyDataSetChanged();
        }


    }

    private ArrayList<HashMap<String, Object>> hashMaps;

    private void getMedicinePredictions(final int type) {
        handler = new Handler();
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Processing Data...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    ArrayList<CustomNameValuePair> valuePairs = new ArrayList<>();
                    valuePairs.add(new CustomNameValuePair("medicineType", type + ""));
                    valuePairs.add(new CustomNameValuePair("monthCount", "" + diffMonth));
                    String response = BaseController.postToServerGzip(BaseController.baseURL + "prediction", valuePairs);

                    //    JSONObject object =    BaseController.getJSONObjectFromURL(BaseController.baseURL+"user");

                    Log.e("RESP", response);

                    try {
                        final JSONObject object = new JSONObject(response);

                        if (object.getBoolean("result")) {


                            JSONArray jsonArray = object.getJSONArray("data");

                            hashMaps = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("index", jsonObject.get("index"));
                                hashMap.put("value", jsonObject.get("value"));
                                hashMaps.add(hashMap);
                            }


                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        int value = 0;
                                        JSONArray jsonArray = object.getJSONArray("data");

                                        JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length()-1);

                                        Double val = jsonObject.getDouble("value");

                                        value = val.intValue();


                                        predictedValueText.setText("Predicted Value Is "+value);
                                        setData(hashMaps);
                                    } catch (Exception e) {
                                        Log.e("ERROR x", e.toString());
                                    }
                                    dialog.dismiss();
                                }
                            });


                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {


                                    dialog.dismiss();

                                    dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                    dialog.setTitleText("ERROR");

                                    try {

                                        dialog.setContentText(object.getString("message"));

                                    } catch (Exception e) {
                                        dialog.setContentText("Invalid response from the server!");
                                    }

                                    dialog.show();

                                }
                            });


                        }


                    } catch (Exception e) {
                        Log.e("ERROR E1 ", e.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();

                                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("ERROR");
                                dialog.setContentText("Something went wrong");
                                dialog.show();


                            }


                        });

                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("ERROR");
                            dialog.setContentText("Something went wrong");
                            dialog.show();
                        }
                    });

                }

            }
        }).

                start();
    }

    int diffMonth = 0;

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.e("DAY", " " + year + " " + month + " " + dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = format.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
            String date_s = format.format(date);

            Date date2 = format.parse("2021-05-01");


            textDate.setText(date_s);

            DateTime start = new DateTime(date2.getTime());
            DateTime end = new DateTime(date.getTime());
            diffMonth = Months.monthsBetween(start, end).getMonths();


        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }


}