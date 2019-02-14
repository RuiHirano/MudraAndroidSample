package model;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {

    private LineChart lineChart;
    private Map<String, Line> linesMap;
    private ArrayList<ILineDataSet> dataSets;
    private Context context;

    public Graph(View view) {
        context = view.getContext();
        this.lineChart = (LineChart) view;
        linesMap = new HashMap<>();
        dataSets = new ArrayList<>();
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setTouchEnabled(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getXAxis().setDrawLabels(false);

        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisRight().setAxisMaximum(1.25f);
        lineChart.getAxisRight().setAxisMinimum(-1.25f);

        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);

        lineChart.getLegend().setEnabled(false);

    }

    public void addLine(String label, int color, int entriesNum, float width) {
        Line line = new Line(label, color, entriesNum, width, dataSets);
        linesMap.put(line.getLabel(), line);
        dataSets.add(line.getLineDataSet());
    }

    public void setYaxis(float leftMax, float leftMin, float rightMax, float rightMin) {
        YAxis yAxisl = lineChart.getAxisLeft();
        yAxisl.setAxisMinimum(leftMin);
        yAxisl.setAxisMaximum(leftMax);

        YAxis yAxisr = lineChart.getAxisRight();
        yAxisr.setAxisMinimum(rightMin);
        yAxisr.setAxisMaximum(rightMax);
    }

    public void drawLine(final String label, final float[] values) {

        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {

                // Update entries in model.Line's entries arraylist
                Line line = linesMap.get(label);
                ArrayList<Entry> entries = line.getEntries();

                // Shift previous values left
                for (int i = 0; i<entries.size()-values.length; i++) {
                    entries.get(i).setX(i + values.length); // update i as i+n entry X
                    entries.get(i).setY(entries.get(i+values.length).getY()); // update i as i+n entry Y
                }

                // Update last (incoming) entries
                for (int i=entries.size()-values.length, j=0; i<entries.size(); i++, j++) {
                    entries.get(i).setX(i);
                    entries.get(i).setY(values[j]);
                }

                // Update chart
                LineData lineData = line.getLineData();
                lineData.removeDataSet(dataSets.get(0));
                lineData.addDataSet(line.getLineDataSet());
                lineChart.setData(lineData);
                lineChart.invalidate();

            }
        });
    }
}
