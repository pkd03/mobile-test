package com.example.quan_ly_thu_chi_v3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LoaiThu extends AppCompatActivity {
    EditText ma_loai, ten_loai;
    Button btn_them, btn_sua, btn_xoa, btn_back;
    ListView lv_loaithu;
    ArrayList<String> listLoaiThu;
    ArrayAdapter<String> adapter;
    SQLiteDatabase db_loaithu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loai_thu);

        initializeViews();
        setupDatabases();
        setupListeners();
        refreshList();
    }
    //
    private void initializeViews(){
        ma_loai = findViewById(R.id.ma_loai);
        ten_loai = findViewById(R.id.ten_loai);
        btn_them = findViewById(R.id.btn_them);
        btn_sua = findViewById(R.id.btn_sua);
        btn_xoa = findViewById(R.id.btn_xoa);
        btn_back = findViewById(R.id.btn_back);


        // Setup ListView
        lv_loaithu = findViewById(R.id.lv_LoaiChi);
        listLoaiThu = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLoaiThu);
        lv_loaithu.setAdapter(adapter);
        }

    private void setupDatabases() {
        db_loaithu = openOrCreateDatabase("LoaiThu.db", MODE_PRIVATE, null);

        try {
            String sql = "CREATE TABLE tbLoaiThu (ma_loai TEXT PRIMARY KEY, ten_loai TEXT)";
            db_loaithu.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table already exists");
        }
    }
    private void setupListeners() {
        // Thêm
        btn_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maLoai = ma_loai.getText().toString();
                String tenLoai = ten_loai.getText().toString();

                ContentValues values = new ContentValues();
                values.put("ma_loai", maLoai);
                values.put("ten_loai", tenLoai);

                String msg = db_loaithu.insert("tbLoaiThu", null, values) > 0 ?
                        "Thêm thành công" : "Thêm thất bại";
                refreshList();
                Toast.makeText(LoaiThu.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Xóa
        btn_xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maLoai = ma_loai.getText().toString();
                String sql = "DELETE FROM tbLoaiThu WHERE ma_loai = ?";
                db_loaithu.execSQL(sql, new String[]{maLoai});
                Toast.makeText(LoaiThu.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });

        // Sửa
        btn_sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maLoai = ma_loai.getText().toString();
                String tenLoai = ten_loai.getText().toString();

                String sql = "UPDATE tbLoaiThu SET ten_loai = ? WHERE ma_loai = ?";
                db_loaithu.execSQL(sql, new String[]{tenLoai, maLoai});
                Toast.makeText(LoaiThu.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                refreshList();
            }
        });

        //quay về màn hình thêm khoản thu
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(LoaiThu.this, Thu.class);
                startActivity(it);
            }
        });
    }

    //làm mới danh sách
    private void refreshList() {
        listLoaiThu.clear();
        Cursor cursor = db_loaithu.query("tbLoaiThu", null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String data = cursor.getString(0) + " : " + cursor.getString(1);
            listLoaiThu.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}