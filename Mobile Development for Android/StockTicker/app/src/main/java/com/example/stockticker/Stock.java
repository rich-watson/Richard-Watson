package com.example.stockticker;

import androidx.annotation.NonNull;




public class Stock implements Comparable<Stock> {

    private String name;
    private String symbol;
    private Double price;
    private Double priceChange;
    private Double percentChange;


    Stock(String name, String symbol, Double price,
          Double priceChange, Double percentChange) {

        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.priceChange = priceChange;
        this.percentChange = percentChange;

    }

    public String getName() {  return name;  }

    public void setName(String s) {  this.name = s;  }

    public String getSymbol() {  return symbol;  }

    public void setSymbol(String s) {  this.symbol = s; }

    public Double getPrice() {  return price;  }

    public void setPrice(Double d) {  this.price = d;  }

    public Double getPriceChange() {  return priceChange;  }

    public void setPriceChange(Double d) {  this.priceChange = d;  }

    public Double getPercentChange() {  return percentChange;  }

    public void setPercentChange(Double d) {  this.percentChange = d;  }



    @NonNull
    @Override
    public String toString() {  return name + "(" + symbol + ") : " + price;  }

    @Override
    public int compareTo(Stock s) {
        return this.getSymbol().compareTo(s.getSymbol());
    }
}
