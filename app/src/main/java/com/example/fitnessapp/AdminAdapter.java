package com.example.fitnessapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AdminAdapter extends BaseAdapter {

    private ArrayList <Object> list;
    private LayoutInflater inflater;
    private static final int Header = 1;
    private static final int users = 0;

    public AdminAdapter(Context context, ArrayList <Object> list) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position) instanceof User)
            return users;
        else
            return Header;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            switch (getItemViewType(position)) {
                case users:
                    convertView = inflater.inflate(R.layout.general_user, null);
                    break;
                case Header:
                    convertView = inflater.inflate(R.layout.listview_header, null);
                    break;
            }
        }

        switch (getItemViewType(position)) {
            case users:
                assert convertView != null;
                TextView name = convertView.findViewById(R.id.nameId);
                TextView email = convertView.findViewById(R.id.emailId);
                //TextView bloodGroup = convertView.findViewById(R.id.bloodGroupId);

                name.setText("Name: " + ((User) list.get(position)).getName());
                email.setText("Email: " + ((User) list.get(position)).getEmail());
                //bloodGroup.setText("Blood Group: " + ((User) list.get(position)).getBloodGroup());
                break;
            case Header:
                assert convertView != null;
                TextView title = convertView.findViewById(R.id.txtTitle);
                Button addBtn = convertView.findViewById(R.id.addBtn);

                title.setText((String) list.get(position));
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent temp = new Intent(v.getContext(), MainActivity.class);
                        temp.putExtra("register", true);
                        v.getContext().startActivity(temp);

                    }
                });
                break;
        }

        return convertView;
    }
}
