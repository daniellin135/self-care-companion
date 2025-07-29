package com.example.self_care_companion.ui.trends;

import com.github.mikephil.charting.charts.Chart;

/**
 * Strategy interface for displaying different types of charts.
 */
public interface ChartStrategy {
    void displayChart(Chart<?> chart, int pastDays);
}
