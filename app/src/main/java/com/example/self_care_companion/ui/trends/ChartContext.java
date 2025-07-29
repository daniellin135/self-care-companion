package com.example.self_care_companion.ui.trends;

import com.github.mikephil.charting.charts.Chart;

/**
 * Context class for chart strategies.
 */
public class ChartContext {
    private ChartStrategy strategy;

    public void setStrategy(ChartStrategy strategy) {
        this.strategy = strategy;
    }

    public void showChart(Chart<?> chart, int pastDays) {
        if (strategy != null) {
            strategy.displayChart(chart, pastDays);
        }
    }
}
