package com.example.hotelchatbot.dto;

import java.util.List;


public class HotelDetailsDTO {
    private int id;
    private String name;
    private String description;
    private String imageUrl;
    private int starRating;
    private String address;
    private String city;
    private List<String> amenities;
    private List<HotelRoomDTO> rooms;

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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public int getStarRating() {
        return starRating;
    }
    public void setStarRating(int starRating) {
        this.starRating = starRating;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public List<String> getAmenities() {
        return amenities;
    }
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
    public List<HotelRoomDTO> getRooms() {
        return rooms;
    }
    public void setRooms(List<HotelRoomDTO> rooms) {
        this.rooms = rooms;
    }

}
    