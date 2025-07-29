package com.example.self_care_companion.ui.mood_trends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.self_care_companion.R;
import com.example.self_care_companion.ui.trends.ChartContext;
import com.example.self_care_companion.ui.trends.MoodChartStrategy;
import com.github.mikephil.charting.charts.PieChart;

public class MoodTrendsFragment extends Fragment {

    private PieChart pieChart;
    private AutoCompleteTextView dateRangeDropdown;
    private int selectedDays = 1; // Default to past 1 day
    private final ChartContext chartContext = new ChartContext(); // Context for chart strategies

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        chartContext.setStrategy(new MoodChartStrategy());
        setupDropdown();
        loadPieChartData(selectedDays);

        return view;
    }

    private void setupDropdown() {
        String[] dateRanges = {"Past 1 Day", "Past 3 Days", "Past Week", "Past Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_list_item_1, dateRanges);
        dateRangeDropdown.setAdapter(adapter);

        selectedDays = 1;
        dateRangeDropdown.setText("Past 1 Day", false);

        dateRangeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: selectedDays = 1; break;
                case 1: selectedDays = 3; break;
                case 2: selectedDays = 7; break;
                case 3: selectedDays = 30; break;
            }
            loadPieChartData(selectedDays);
        });
    }

    private void loadPieChartData(int pastDays) {
        chartContext.showChart(pieChart, pastDays);
    }
}
