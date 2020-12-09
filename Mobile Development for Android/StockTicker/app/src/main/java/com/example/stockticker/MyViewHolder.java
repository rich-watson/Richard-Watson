package com.example.stockticker;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView stock;
    TextView symbol;
    TextView price;
    TextView triangle;
    TextView change;


    MyViewHolder(@NonNull View itemView) {
        super(itemView);
        stock = itemView.findViewById(R.id.companyName);
        symbol = itemView.findViewById(R.id.stockSymbol);
        price = itemView.findViewById(R.id.stockPrice);
        triangle = itemView.findViewById(R.id.triangle);
        change = itemView.findViewById(R.id.stockChange);
    }
}
