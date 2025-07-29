package com.example.self_care_companion.ui.trends;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.example.self_care_companion.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Strategy for displaying mood data as a PieChart.
 */
public class MoodChartStrategy implements ChartStrategy {

    private static final int[] CUSTOM_PASTEL_COLORS = new int[]{
            Color.rgb(64, 89, 128),
            Color.rgb(149, 165, 124),
            Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134),
            Color.rgb(179, 48, 80),
            Color.rgb(120, 120, 200)
    };

    @Override
    public void displayChart(Chart<?> chart, int pastDays) {
        if (!(chart instanceof PieChart)) return;

        PieChart pieChart = (PieChart) chart;
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
