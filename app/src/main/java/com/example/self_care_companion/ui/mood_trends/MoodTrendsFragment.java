package com.example.self_care_companion.ui.mood_trends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.self_care_companion.MainActivity;
import com.example.self_care_companion.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoodTrendsFragment extends Fragment {

    private PieChart pieChart;
    private Spinner dateRangeSpinner;
    private int selectedDays = 1; // Default to past 1 day

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moodtrends, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        dateRangeSpinner = view.findViewById(R.id.dateRangeSpinner);

        setupSpinner();
        loadPieChartData(selectedDays);

        return view;
    }

    private void setupSpinner() {
        String[] dateRanges = {"Past 1 Day", "Past 3 Days", "Past Week", "Past Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, dateRanges);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateRangeSpinner.setAdapter(adapter);

        dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectedDays = 1;
                        break;
                    case 1:
                        selectedDays = 3;
                        break;
                    case 2:
                        selectedDays = 7;
                        break;
                    case 3:
                        selectedDays = 30;
                        break;
                }
                loadPieChartData(selectedDays);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadPieChartData(int pastDays) {
        Map<String, Integer> moodCounts = MainActivity.databaseHelper.getMoodCountsFiltered(pastDays);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Mood Distribution");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
