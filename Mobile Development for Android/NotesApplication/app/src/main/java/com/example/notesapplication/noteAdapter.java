package com.example.notesapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;


public class noteAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private List<Note> noteList;
    private MainActivity mainAct;

    noteAdapter(List<Note> noteList, MainActivity ma) {
        this.noteList = noteList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_entry, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note n = noteList.get(position);
        String t = n.getTitle();
        String s = n.getBody();
        if (s.length() > 80){
            s = s.substring(0,80) + "...";
        }

        if (t.length() > 80) {
            t = t.substring(0, 80) + "...";
        }

        holder.title.setText(t);
        holder.snippet.setText(s);
        holder.dateTime.setText(new Date(n.getDateTime()).toString());

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
