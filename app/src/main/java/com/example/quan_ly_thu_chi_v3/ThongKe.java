package com.example.quan_ly_thu_chi_v3;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.ArrayList;
import java.text.NumberFormat;
import java.util.Locale;

public class ThongKe extends AppCompatActivity {
    private PieChart pieChart;
    private SQLiteDatabase db_thu, db_chi;
    private TextView tong_thu, tong_chi, so_du;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_ke);

        pieChart = findViewById(R.id.pieChart);
        tong_thu = findViewById(R.id.tong_thu);
        tong_chi = findViewById(R.id.tong_chi);
        so_du = findViewById(R.id.so_du);

        db_thu = openOrCreateDatabase("Thu.db", MODE_PRIVATE, null);
        db_chi = openOrCreateDatabase("Chi.db", MODE_PRIVATE, null);

        setupChart();
        updateChart();
    }

    private void setupChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Thu - Chi");
    }

    private String formatCurrency(float amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }

    private void updateChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        float totalThu = 0;
        Cursor cursorThu = db_thu.rawQuery("SELECT SUM(tien_thu) FROM tbThu", null);
        if (cursorThu.moveToFirst()) {
            totalThu = cursorThu.getFloat(0);
        }
        cursorThu.close();

        float totalChi = 0;
        Cursor cursorChi = db_chi.rawQuery("SELECT SUM(tien_chi) FROM tbChi", null);
        if (cursorChi.moveToFirst()) {
            totalChi = cursorChi.getFloat(0);
        }
        cursorChi.close();

        float con_lai = totalThu - totalChi;


        tong_thu.setText("Tổng thu: " + formatCurrency(totalThu));
        tong_chi.setText("Tổng chi: " + formatCurrency(totalChi));
        so_du.setText("Số dư: " + formatCurrency(con_lai));

        if (con_lai < 0) {
            so_du.setTextColor(Color.rgb(239, 83, 80)); // Red for negative
        } else {
            so_du.setTextColor(Color.rgb(67, 160, 71)); // Green for positive
        }

        if (totalThu > 0) entries.add(new PieEntry(totalThu, "Thu"));
        if (totalChi > 0) entries.add(new PieEntry(totalChi, "Chi"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.rgb(67, 160, 71), Color.rgb(239, 83, 80));
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(data);
        pieChart.invalidate();
    }
}