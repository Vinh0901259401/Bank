package com.example.bidv_demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {
    Context context;
    String[] flameName;
    int[] imageIds; // Đổi tên biến để dễ nhận biết

    LayoutInflater inflater; // Khai báo biến inflater đúng cách

    public GridAdapter(Context context, String[] flameName, int[] imageIds) {
        this.context = context;
        this.flameName = flameName;
        this.imageIds = imageIds; // Sửa lại tên biến
    }

    @Override
    public int getCount() {
        return flameName.length;
    }

    @Override
    public Object getItem(int position) {
        return flameName[position]; // Trả về item đúng
    }

    @Override
    public long getItemId(int position) {
        return position; // Trả về vị trí của item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) { // Sử dụng biến inflater đúng
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, parent, false); // Sử dụng layout item riêng
        }
        ImageView imageView = convertView.findViewById(R.id.image_view); // ID từ layout item
        TextView textView = convertView.findViewById(R.id.text_view); // ID từ layout item

        imageView.setImageResource(imageIds[position]); // Sử dụng biến đúng
        textView.setText(flameName[position]);

        return convertView;
    }
}
