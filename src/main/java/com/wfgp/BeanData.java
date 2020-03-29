package com.wfgp;

import java.math.BigDecimal;

class Result
{
    public int cout = 0;
    public BigDecimal weight;
    public BigDecimal amount;

    public Result(){
        weight = new BigDecimal(0.0);
        amount = weight;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

class CustomerExpressData
{
    public String dateStr;
    public String trackNumber;
    public String count;
    public String sender;
    public String dest;
    public String weight;
    public String freight;

    public CustomerExpressData(String dateStr, String trackNumber, String count,
                               String sender, String dest, String weight, String freight) {
        this.dateStr = dateStr;
        this.trackNumber = trackNumber;
        this.count = count;
        this.sender = sender;
        this.dest = dest;
        this.weight = weight;
        this.freight = freight;
    }
}

class ExcelData {

    public String trackNumber;
    public String addr;
    public String amount;

    public ExcelData(String trackNumber, String addr, String amount) {
        this.trackNumber = trackNumber;
        this.addr = addr;
        this.amount = amount;
    }
}

class Confs
{
    public String customer;
    public String number;

    public Confs(String customer, String number) {
        this.customer = customer;
        this.number = number;
    }
}
