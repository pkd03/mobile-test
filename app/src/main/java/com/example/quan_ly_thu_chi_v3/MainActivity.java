package com.example.quan_ly_thu_chi_v3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //lấy về tham chiếu của các nút
        Button btn_thu = (Button) findViewById(R.id.btn_thu);
        Button btn_chi = (Button) findViewById(R.id.btn_chi);
        Button btn_thongke = (Button) findViewById(R.id.btn_thongke);

        btn_thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(MainActivity.this, Thu.class);
                startActivity(it);

            }
        });

        btn_chi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(MainActivity.this, Chi.class);
                startActivity(it);

            }
        });

        btn_thongke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(MainActivity.this, ThongKe.class);
                startActivity(it);

            }
        });

    }
}