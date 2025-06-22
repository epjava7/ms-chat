package com.example.hotelchatbot.dto;

public class HotelDTO {
    private int id;
    private String name;
    private String city;
    private int starRating;
    private double averagePrice;
    private int timesBooked;
    private String ImageUrl;
    private String description;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public int getStarRating() {
        return starRating;
    }
    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }
    public double getAveragePrice() {
        return averagePrice;
    }
    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }
    public int getTimesBooked() {
        return timesBooked;
    }
    public void setTimesBooked(int timesBooked) {
        this.timesBooked = timesBooked;
    }
    public String getImageUrl() {
        return ImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}