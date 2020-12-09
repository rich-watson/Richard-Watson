package com.example.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OfficialAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private MainActivity mainActivity;
    private List<Official> officials;


    OfficialAdapter(List<Official> officials, MainActivity ma) {
        this.officials = officials;
        mainActivity = ma;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official_entry, parent, false);
        itemView.setOnClickListener(mainActivity);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Official official = officials.get(position);

        holder.name.setText(official.getName());
        holder.office.setText(official.getOffice());
        holder.party.setText(String.format("(%s)", official.getParty()));

    }

    @Override
    public int getItemCount() {
        return officials.size();
    }
}
