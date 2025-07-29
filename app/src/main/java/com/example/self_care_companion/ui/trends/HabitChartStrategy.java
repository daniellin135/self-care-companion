package com.example.self_care_companion.ui.trends;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.example.self_care_companion.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Strategy for displaying habit data as a LineChart.
 */
public class HabitChartStrategy implements ChartStrategy {

    private final String selectedHabit;

    public HabitChartStrategy(String habit) {
        this.selectedHabit = habit;
    }

    @Override
    public void displayChart(Chart<?> chart, int pastDays) {
        if (!(chart instanceof LineChart)) return;

        LineChart lineChart = (LineChart) chart;
        Map<String, Double> habitData = MainActivity.databaseHelper.getHabitValues(selectedHabit, pastDays);
        List<Entry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        int index = 0;
        double goalValue = MainActivity.databaseHelper.getHabitGoal(selectedHabit);

        for (Map.Entry<String, Double> entry : habitData.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            dates.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, selectedHabit);
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
        goalLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        leftAxis.addLimitLine(goalLine);

        // X-axis formatting
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
                return (pos >= 0 && pos < dates.size()) ? dates.get(pos).substring(5) : "";
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}
