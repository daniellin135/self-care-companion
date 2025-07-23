package com.example.self_care_companion.ui.habit_trends;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.self_care_companion.MainActivity;
import com.example.self_care_companion.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Locale;
import android.graphics.Typeface;

public class HabitTrendsFragment extends Fragment {

    private LineChart lineChart;
    private AutoCompleteTextView dateRangeDropdown;
    private AutoCompleteTextView habitSelectorDropdown;
    private int selectedDays = 1;
    private String selectedHabit = "Water Intake"; // Default habit

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habittrends, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        dateRangeDropdown = view.findViewById(R.id.dateRangeDropdown);
        habitSelectorDropdown = view.findViewById(R.id.habitSelectorDropdown);

        Button btnViewMoods = view.findViewById(R.id.btnViewMoods);

        // Set click listener to navigate
        btnViewMoods.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_habitTrendsFragment_to_moodTrendsFragment);
        });

        setupDateRangeDropdown();
        setupHabitDropdown();
        loadHabitLineChart(selectedHabit, selectedDays);

        return view;
    }

    private void setupDateRangeDropdown() {
        String[] dateRanges = {"Past 1 Day", "Past 3 Days", "Past Week", "Past Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, dateRanges);
        dateRangeDropdown.setAdapter(adapter);

        // Set default to "Past 1 Day"
        selectedDays = 1;
        dateRangeDropdown.setText("Past 1 Day", false);

        dateRangeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: selectedDays = 1; break;
                case 1: selectedDays = 3; break;
                case 2: selectedDays = 7; break;
                case 3: selectedDays = 30; break;
            }
            loadHabitLineChart(selectedHabit, selectedDays);
        });
    }


    private void setupHabitDropdown() {
        Set<String> habitSet = MainActivity.databaseHelper.getUniqueHabitNames();
        List<String> habits = new ArrayList<>(habitSet);

        ArrayAdapter<String> habitAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, habits);
        habitSelectorDropdown.setAdapter(habitAdapter);

        // Set default to "Water Intake" if it exists, else fallback to first habit
        if (habits.contains("Water Intake")) {
            selectedHabit = "Water Intake";
        } else if (!habits.isEmpty()) {
            selectedHabit = habits.get(0);
        }
        habitSelectorDropdown.setText(selectedHabit, false);

        habitSelectorDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedHabit = habits.get(position);
            loadHabitLineChart(selectedHabit, selectedDays);
        });
    }


    private void loadHabitLineChart(String habitLabel, int pastDays) {
        // Fetch data
        Map<String, Double> habitData = MainActivity.databaseHelper.getHabitValues(habitLabel, pastDays);
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        int index = 0;
        double goalValue = MainActivity.databaseHelper.getHabitGoal(habitLabel); // Dynamic goal

        for (Map.Entry<String, Double> entry : habitData.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            dates.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, habitLabel);
        dataSet.setColor(Color.parseColor("#3E7BFA"));
        dataSet.setCircleColor(Color.parseColor("#3E7BFA"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#AEC6FF"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.clear();
        lineChart.setData(lineData);

        // Chart style
        styleLineChart();

        // Adjust Y-Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.removeAllLimitLines();

        float dataMax = lineData.getYMax();
        float maxY = Math.max(dataMax, (float) goalValue);
        leftAxis.setAxisMaximum(maxY + (maxY * 0.1f));

        // Goal line
        String goalText = String.format(Locale.getDefault(), "Goal: %.1f", goalValue);
        LimitLine goalLine = new LimitLine((float) goalValue, goalText);
        goalLine.setLineColor(Color.parseColor("#FF4C4C"));
        goalLine.setLineWidth(2f);
        goalLine.enableDashedLine(10f, 10f, 0f);
        goalLine.setTextColor(Color.parseColor("#FF4C4C"));
        goalLine.setTextSize(12f);
        goalLine.setTypeface(Typeface.DEFAULT_BOLD);
        goalLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        leftAxis.addLimitLine(goalLine);

        // X-axis as dates
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-30);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int pos = Math.round(value);
                if (pos >= 0 && pos < dates.size()) {
                    String date = dates.get(pos);
                    // Assume format "YYYY-MM-DD"
                    return date.substring(5); // "07-22"
                }
                return "";
            }
        });


        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void styleLineChart() {
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextColor(Color.DKGRAY);

        lineChart.getAxisRight().setEnabled(false);
    }
}
