package cbj.trailer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.firestore.util.Util;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TabFragment1 extends Fragment {

    private static final int MAX_X_VALUE = 7;
    private static final int MAX_Y_VALUE = 10000;
    private static final int MIN_Y_VALUE = 500;
    private static final String[] DAYS = { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };

    private BarChart chart1;
    private BarChart chart2;
    private int[] data = new int[13];
    public TabFragment1(int [] data) {
        // Required empty public constructor
        this.data = data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        chart1 = view.findViewById(R.id.barchart1);
        chart2 = view.findViewById(R.id.barchart2);

        int [] step_count = new int[7];
        int [] heart_score = new int[7];

        for(int i=0; i<MAX_X_VALUE; i++){
            step_count[i] = data[i];
            heart_score[i] = data[i+MAX_X_VALUE];
        }

        BarData data1 = createChartData(step_count);
        BarData data2 = createChartData2(heart_score);

        configureChartAppearance();
        configureChartAppearance2();

        prepareChartData(data1);
        prepareChartData2(data2);

        return view;
    }

    private BarData createChartData(int[] step_count) {
        String label = "걸음 수";
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            int x = i;
            int y = step_count[i];
            values.add(new BarEntry(x, y));
        }

        BarDataSet set1 = new BarDataSet(values, label);
        set1.setColor(Color.rgb(30,190,230));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        return data;
    }

    private BarData createChartData2(int[] heart_score) {
        String label = "심장 점수";
        ArrayList<BarEntry> values = new ArrayList<>();
        for (int i = 0; i < MAX_X_VALUE; i++) {
            int x = i;
            int y = heart_score[i];
            values.add(new BarEntry(x, y));
        }
        BarDataSet set1 = new BarDataSet(values, label);
        set1.setColor(Color.rgb(20,220,210));

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);

        return data;
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(13);
        chart1.setData(data);
        chart1.invalidate();
        chart1.getLegend().setTextSize(18f);
        chart1.setDrawValueAboveBar(true);

        chart1.getAxisRight().setDrawGridLines(false);
        chart1.getAxisLeft().setDrawGridLines(false);
        chart1.getXAxis().setDrawGridLines(false);
    }

    private void prepareChartData2(BarData data) {
        data.setValueTextSize(14);
        chart2.setData(data);
        chart2.invalidate();
        chart2.getLegend().setTextSize(18f);
        chart2.setDrawValueAboveBar(true);

        chart2.getAxisRight().setDrawGridLines(false);
        chart2.getAxisLeft().setDrawGridLines(false);
        chart2.getXAxis().setDrawGridLines(false);
    }

    private void configureChartAppearance() {
        chart1.getDescription().setEnabled(false);
        chart1.setDrawValueAboveBar(false);

        XAxis xAxis = chart1.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DAYS[(int) value];
            }
        });

        YAxis axisLeft = chart1.getAxisLeft();
        axisLeft.setGranularity(10);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = chart1.getAxisRight();
        axisRight.setGranularity(10);
        axisRight.setAxisMinimum(0);
    }

    private void configureChartAppearance2() {
        chart2.getDescription().setEnabled(false);
        chart2.setDrawValueAboveBar(false);

        XAxis xAxis = chart2.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return DAYS[(int) value];
            }
        });

        YAxis axisLeft = chart2.getAxisLeft();
        axisLeft.setGranularity(10);
        axisLeft.setAxisMinimum(0);

        YAxis axisRight = chart2.getAxisRight();
        axisRight.setGranularity(10);
        axisRight.setAxisMinimum(0);
    }
}


