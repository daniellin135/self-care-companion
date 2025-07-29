package com.example.self_care_companion.ui.habit_trends;

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

import com.example.self_care_companion.MainActivity;
import com.example.self_care_companion.R;
import com.example.self_care_companion.ui.trends.ChartContext;
import com.example.self_care_companion.ui.trends.HabitChartStrategy;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HabitTrendsFragment extends Fragment {

    private LineChart lineChart;
    private AutoCompleteTextView dateRangeDropdown;
    private AutoCompleteTextView habitSelectorDropdown;
    private int selectedDays = 1;
    private String selectedHabit = "Water Intake"; // Default habit
    private final ChartContext chartContext = new ChartContext(); // Context for chart strategies

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        chartContext.setStrategy(new HabitChartStrategy(habitLabel));
        chartContext.showChart(lineChart, pastDays);
    }
}
