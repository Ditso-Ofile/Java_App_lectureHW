package com.example.homework.soap;

/** Form data of the SOAP menu. */
public class MessagePrice {
    private String currency = "EUR";
    private String startDate;
    private String endDate;

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
