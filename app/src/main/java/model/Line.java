package model;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class Line {

    private String label;
    private int color;
    private int entriesNum;
    private float width;

    private LineDataSet lineDataSet;
    private LineData lineData;

    ArrayList<Entry> entries = new ArrayList<>();

    public Line(String label, int color, int entriesNum, float width, ArrayList<ILineDataSet> dataSets) {
        entries = new ArrayList<>();
        this.label = label;
        this.color = color;
        this.entriesNum = entriesNum;
        this.width = width;

        for (int i = 0; i < entriesNum; i++) {
            entries.add(new Entry(i, 0f));
        }

        lineDataSet = new LineDataSet(entries, label);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet.setLineWidth(width);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(color);

        lineData = new LineData(dataSets);
    }

    public String getLabel() {
        return label;
    }
    public LineDataSet getLineDataSet() {
        return lineDataSet;
    }
    public int getColor() {
        return color;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public LineData getLineData() {
        return lineData;
    }
}
