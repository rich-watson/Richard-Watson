package com.example.stockticker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private MainActivity mainAct;
    private List<Stock> stockList;

    StockAdapter(List<Stock> stockList, MainActivity ma) {
        this.stockList = stockList;
        mainAct = ma;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_row, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        
        if (stock.getPriceChange() < -0.0) {
            holder.stock.setTextColor(Color.RED);
            holder.symbol.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.triangle.setTextColor(Color.RED);
            holder.triangle.setText(R.string.triangle1);
            holder.change.setTextColor(Color.RED);

        }
        if (stock.getPriceChange() > 0.0) {
            holder.stock.setTextColor(Color.GREEN);
            holder.symbol.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.triangle.setTextColor(Color.GREEN);
            holder.triangle.setText(R.string.triangle);
            holder.change.setTextColor(Color.GREEN);

        }
        if (stock.getPriceChange() == 0.0) {
            holder.stock.setTextColor(Color.WHITE);
            holder.symbol.setTextColor(Color.WHITE);
            holder.price.setTextColor(Color.WHITE);
            holder.change.setTextColor(Color.WHITE);

        }
        holder.stock.setText(stock.getName());
        holder.symbol.setText(stock.getSymbol());
        holder.price.setText(String.format(Locale.US, "%.2f" ,stock.getPrice()));
        holder.change.setText(String.format(Locale.US, "%.2f (%.2f %%)",
                stock.getPriceChange(),
                stock.getPercentChange()));


    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
