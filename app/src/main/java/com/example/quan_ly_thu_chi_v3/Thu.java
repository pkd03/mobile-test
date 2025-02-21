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

public class Thu extends AppCompatActivity {
    EditText ten, tien_thu;
    Spinner spn_loaithu;
    Button btn_them, btn_sua, btn_xoa, btn_themloai , btn_back;
    ListView lv_thu;
    ArrayList<String> listThu, listLoaiThu;
    ArrayAdapter<String> adapter, loaiThuAdapter;
    SQLiteDatabase db_thu, db_loaithu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thu);

        initializeViews();
        setupDatabases();
        loadLoaiThu();
        setupListeners();
        refreshList();
    }

    private void initializeViews() {
        ten = findViewById(R.id.ten);
        tien_thu = findViewById(R.id.tien_chi);
        spn_loaithu = findViewById(R.id.spn_loaichi);
        btn_themloai = findViewById(R.id.btn_themloai);
        btn_sua = findViewById(R.id.btn_sua);
        btn_xoa = findViewById(R.id.btn_xoa);
        lv_thu = findViewById(R.id.lv_chi);
        btn_them = findViewById(R.id.btn_them);
        btn_back = findViewById(R.id.btn_back);


        listThu = new ArrayList<>();
        listLoaiThu = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listThu);
        loaiThuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listLoaiThu);
        loaiThuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lv_thu.setAdapter(adapter);
        spn_loaithu.setAdapter(loaiThuAdapter);
    }

    private void setupDatabases() {
        db_thu = openOrCreateDatabase("Thu.db", MODE_PRIVATE, null);
        db_loaithu = openOrCreateDatabase("LoaiThu.db", MODE_PRIVATE, null);

        try {
            db_thu.execSQL("CREATE TABLE IF NOT EXISTS tbThu (ten TEXT, tien_thu INTEGER, loai_thu TEXT)");
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo bảng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLoaiThu() {
        try {
            Cursor cursor = db_loaithu.query("tbLoaiThu", null, null, null, null, null, null);
            listLoaiThu.clear();

            if (cursor.moveToFirst()) {
                do {
                    listLoaiThu.add(cursor.getString(1)); // ten_loai
                } while (cursor.moveToNext());
            }
            cursor.close();
            loaiThuAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi load loại thu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        //thêm
        btn_them.setOnClickListener(v -> {
            try {
                if (listLoaiThu.isEmpty()) {
                    Toast.makeText(this, "Vui lòng thêm loại thu trước", Toast.LENGTH_SHORT).show();
                    return;
                }

                String Ten = ten.getText().toString().trim();
                String TienThuStr = tien_thu.getText().toString().trim();

                if (Ten.isEmpty() || TienThuStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int TienThu = Integer.parseInt(TienThuStr);
                String LoaiThu = spn_loaithu.getSelectedItem().toString();

                ContentValues values = new ContentValues();
                values.put("ten", Ten);
                values.put("tien_thu", TienThu);
                values.put("loai_thu", LoaiThu);

                long result = db_thu.insert("tbThu", null, values);
                if (result > 0) {
                    Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    ten.setText("");
                    tien_thu.setText("");
                    refreshList();
                } else {
                    Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Tiền thu phải là số", Toast.LENGTH_SHORT).show();
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

                db_thu.execSQL("DELETE FROM tbThu WHERE ten = ?", new String[]{Ten});
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
                String TienThuStr = tien_thu.getText().toString().trim();

                if (Ten.isEmpty() || TienThuStr.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int TienThu = Integer.parseInt(TienThuStr);
                String LoaiThu = spn_loaithu.getSelectedItem().toString();

                db_thu.execSQL("UPDATE tbThu SET tien_thu = ?, loai_thu = ? WHERE ten = ?",
                        new String[]{String.valueOf(TienThu), LoaiThu, Ten});
                Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                refreshList();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Tiền thu phải là số", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // chuyển sang màn hình thêm loại
        btn_themloai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(Thu.this, LoaiThu.class);
                startActivity(it);

            }
        });

        // về màn hình chính
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //khởi tao intent để di chuyển giữa 2 màn hình
                Intent it = new Intent(Thu.this, MainActivity.class);
                startActivity(it);

            }
        });
    }

    private void refreshList() {
        try {
            listThu.clear();

            // Truy vấn sắp xếp theo loại thu
            Cursor cursor = db_thu.query(
                    "tbThu", // Tên bảng
                    null,    // Lấy tất cả cột
                    null,    // Không có điều kiện WHERE
                    null,    // Không có giá trị WHERE
                    null,    // Không GROUP BY
                    null,    // Không HAVING
                    "loai_thu ASC" // Sắp xếp theo loại thu
            );

            if (cursor.moveToFirst()) {
                String currentLoaiThu = "";
                StringBuilder loaiThuGroup = new StringBuilder();

                do {
                    String ten = cursor.getString(0);  // Tên thu
                    String tien = cursor.getString(1); // Tiền thu
                    String loaiThu = cursor.getString(2); // Loại thu

                    // Nếu loại thu thay đổi, thêm tiêu đề mới
                    if (!loaiThu.equals(currentLoaiThu)) {
                        if (!currentLoaiThu.isEmpty()) {
                            listThu.add(loaiThuGroup.toString()); // Thêm nhóm cũ vào danh sách
                        }
                        currentLoaiThu = loaiThu;
                        loaiThuGroup = new StringBuilder(); // Tạo nhóm mới
                        loaiThuGroup.append(currentLoaiThu).append("\n");
                    }

                    // Thêm chi tiết vào nhóm hiện tại
                    loaiThuGroup.append("    ").append(ten).append(": ").append(tien).append(" đ\n");

                } while (cursor.moveToNext());

                // Thêm nhóm cuối cùng vào danh sách
                if (loaiThuGroup.length() > 0) {
                    listThu.add(loaiThuGroup.toString());
                }
            }
            cursor.close();

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}