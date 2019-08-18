package com.hw.rms.roommanagementsystem.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import com.hw.rms.roommanagementsystem.Data.ResponseRoom;
import com.hw.rms.roommanagementsystem.Helper.DAO;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<ResponseRoom> {

    private Context context;
    private List<ResponseRoom> responseRooms;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<ResponseRoom> objects) {
        super(context, resource, objects);
        this.context = context;
        this.responseRooms = objects;
    }

    @Override
    public int getCount() {
        return responseRooms.size();
    }

    @Nullable
    @Override
    public ResponseRoom getItem(int position) {
        return responseRooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(Color.WHITE);
        textView.setText(responseRooms.get(position).getRoom_name());

        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setTextColor(Color.WHITE);
        textView.setText(responseRooms.get(position).getRoom_name());

        return textView;
    }
}
