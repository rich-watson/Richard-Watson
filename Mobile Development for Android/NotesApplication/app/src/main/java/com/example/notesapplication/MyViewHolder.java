package com.example.notesapplication;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView dateTime;
    TextView snippet;


    MyViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.Title);
        dateTime = itemView.findViewById(R.id.dateTime);
        snippet = itemView.findViewById(R.id.snippet);

    }
}
