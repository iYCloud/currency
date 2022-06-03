package com.example.currencyconversionprovider;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class Currency {
    String name;
    Double userToInter;
    Double interToUser;
    Double profit;

    DecimalFormat df = new DecimalFormat("#.#####");

    public String getName(){
        return this.name;
    }

    public Double getUserToInter(){
        return this.userToInter;
    }

    public void setUserToInter(Double userToInter){
        this.userToInter = userToInter;
    }

    public Double getInterToUser(){
        return this.interToUser;
    }

    public void setInterToUser(Double interToUser){
        this.interToUser = interToUser;
    }

    public Double getProfit(){
        return this.profit;
    }

    public void setProfit(Double profit){
        this.profit = profit;
    }

    public List<String> list(){
        return Arrays.asList(this.name, String.valueOf(this.userToInter), String.valueOf(this.interToUser), String.valueOf(df.format(this.profit)));
    }

    public Currency(String name){
        this.name = name;
    }

}
