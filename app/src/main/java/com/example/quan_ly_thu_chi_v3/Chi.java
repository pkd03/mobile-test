package com.example.quan_ly_thu_chi_v3;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class Chi extends AppCompatActivity {
    EditText ten, tien_chi;
    Spinner spn_loaichi;
    Button btn_them, btn_sua, btn_xoa, btn_themloai, btn_back;
    ListView lv_chi;
    ArrayList listChi, listLoaiChi;
    ArrayAdapter adapter, loaiChiAdapter;
    SQLiteDatabase db_chi, db_loaichi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi);

        initializeViews();
        setupDatabases();
        loadLoaiChi();
        setupListeners();
        refreshList();
    }

    private void initializeViews() {
        ten = findViewById(R.id.ten);
        tien_chi = findViewById(R.id.tien_chi);
        spn_loaichi = findViewById(R.id.spn_loaichi);
        btn_themloai = findViewById(R.id.btn_themloai);
        btn_sua = findViewById(R.id.btn_sua);
        btn_xoa = findViewById(R.id.btn_xoa);
        lv_chi = findViewById(R.id.lv_chi);
        btn_them = findViewById(R.id.btn_them);
        btn_back = findViewById(R.id.btn_back);

        listChi = new ArrayList<>();
        listLoaiChi = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listChi);
        loaiChiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listLoaiChi);
        loaiChiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lv_chi.setAdapter(adapter);
        spn_loaichi.setAdapter(loaiChiAdapter);
    }

    private void setupDatabases() {
        db_chi = openOrCreateDatabase("Chi.db", MODE_PRIVATE, null);
        db_loaichi = openOrCreateDatabase("LoaiChi.db", MODE_PRIVATE, null);

        try {
            db_chi.execSQL("CREATE TABLE IF NOT EXISTS tbChi (ten TEXT, tien_chi INTEGER, loai_chi TEXT)");
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo bảng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLoaiChi() {
        try {
            Cursor cursor = db_loaichi.query("tbLoaiChi", null, null, null, null, null, null);
            listLoaiChi.clear();

            if (cursor.moveToFirst()) {
                do {
                    listLoaiChi.add(cursor.getString(1)); // ten_loai
                } while (cursor.moveToNext());
            }
            cursor.close();
            loaiChiAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi load loại chi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        //thêm
        btn_them.setOnClickListener(v -> {
            try {
                if (listLoaiChi.isEmpty()) {
                    Toast.makeText(this, "Vui lòng thêm loại chi trước", Toast.LENGTH_SHORT).show();
                    return;
                }

                String Ten = ten.getText().toString().trim();
                String TienChiStr = tien_chi.getText().toString().trim();

                if (Ten.isEmpty() || TienChiStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int TienChi = Integer.parseInt(TienChiStr);
                String LoaiChi = spn_loaichi.getSelectedItem().toString();

                ContentValues values = new ContentValues();
                values.put("ten", Ten);
                values.put("tien_chi", TienChi);
                values.put("loai_chi", LoaiChi);

                long result = db_chi.insert("tbChi", null, values);
                if (result > 0) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    ten.setText("");
                    tien_chi.setText("");
                    refreshList();
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Tiền chi phải là số", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //xóa theo tên
        btn_xoa.setOnClickListener(v -> {
            try {
                String Ten = ten.getText().toString().trim();
                if (Ten.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                    return;
                }

                db_chi.execSQL("DELETE FROM tbChi WHERE ten = ?", new String[]{Ten});
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                refreshList();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // sửa theo tên
        btn_sua.setOnClickListener(v -> {
            try {
                String Ten = ten.getText().toString().trim();
                String TienChiStr = tien_chi.getText().toString().trim();

                if (Ten.isEmpty() || TienChiStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int TienChi = Integer.parseInt(TienChiStr);
                String LoaiChi = spn_loaichi.getSelectedItem().toString();

                db_chi.execSQL("UPDATE tbChi SET tien_chi = ?, loai_chi = ? WHERE ten = ?",
                        new String[]{String.valueOf(TienChi), LoaiChi, Ten});
                Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                refreshList();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Tiền chi phải là số", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // chuyển sang màn hình thêm loại
        btn_themloai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Chi.this, LoaiChi.class);
                startActivity(it);
            }
        });

        // về màn hình chính
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Chi.this, MainActivity.class);
                startActivity(it);
            }
        });
    }

    private void refreshList() {
        try {
            listChi.clear();

            // Truy vấn sắp xếp theo loại chi
            Cursor cursor = db_chi.query(
                    "tbChi",
                    null,
                    null,
                    null,
                    null,
                    null,
                    "loai_chi ASC"
            );

            if (cursor.moveToFirst()) {
                String currentLoaiChi = "";
                StringBuilder loaiChiGroup = new StringBuilder();

                do {
                    String ten = cursor.getString(0);
                    String tien = cursor.getString(1);
                    String loaiChi = cursor.getString(2);

                    if (!loaiChi.equals(currentLoaiChi)) {
                        if (!currentLoaiChi.isEmpty()) {
                            listChi.add(loaiChiGroup.toString());
                        }
                        currentLoaiChi = loaiChi;
                        loaiChiGroup = new StringBuilder();
                        loaiChiGroup.append(currentLoaiChi).append("\n");
                    }

                    loaiChiGroup.append("    ").append(ten).append(": ").append(tien).append(" đ\n");

                } while (cursor.moveToNext());

                if (loaiChiGroup.length() > 0) {
                    listChi.add(loaiChiGroup.toString());
                }
            }
            cursor.close();

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}