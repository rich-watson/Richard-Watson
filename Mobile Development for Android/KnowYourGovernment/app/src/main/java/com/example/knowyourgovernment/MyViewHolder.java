package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView office;
    TextView name;
    TextView party;


     MyViewHolder(@NonNull View itemView) {
        super(itemView);

        office = itemView.findViewById(R.id.office);
        name = itemView.findViewById(R.id.officialName);
        party = itemView.findViewById(R.id.officialParty);


    }
}
