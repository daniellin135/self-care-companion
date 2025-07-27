package com.example.self_care_companion.ui.mood_trends;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import com.github.mikephil.charting.formatter.PercentFormatter;

import com.example.self_care_companion.ui.habit_trends.HabitTrendsFragment;

public class MoodTrendsFragment extends Fragment {

    private PieChart pieChart;
    private AutoCompleteTextView dateRangeDropdown;
    private int selectedDays = 1; // Default to past 1 day

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moodtrends, container, false);

        pieChart = view.findViewById(R.id.pieChart);
        dateRangeDropdown = view.findViewById(R.id.dateRangeDropdown);

        Button btnViewHabits = view.findViewById(R.id.btnViewHabits);

        // Set click listener to navigate
        btnViewHabits.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_moodTrendsFragment_to_habitTrendsFragment);
        });

        setupDropdown();
        loadPieChartData(selectedDays);

        return view;
    }

    private static final int[] CUSTOM_PASTEL_COLORS = new int[]{
            Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(120, 120, 200)
    };

    private void setupDropdown() {
        String[] dateRanges = {"Past 1 Day", "Past 3 Days", "Past Week", "Past Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, dateRanges);
        dateRangeDropdown.setAdapter(adapter);

        selectedDays = 1;
        dateRangeDropdown.setText("Past 1 Day", false);

        dateRangeDropdown.setOnItemClickListener((parent, view, position, id) -> {
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
        });
    }

    private void loadPieChartData(int pastDays) {
        Map<String, Integer> moodCounts = MainActivity.databaseHelper.getMoodCountsFiltered(pastDays);
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Mood Distribution");
        dataSet.setColors(CUSTOM_PASTEL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.clear();
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}