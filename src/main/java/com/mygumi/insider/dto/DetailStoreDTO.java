package com.mygumi.insider.dto;

import java.util.List;

public class DetailStoreDTO {

    private Long store_id;

    private String address;

    private String category;

    private String lat;

    private String lng;

    private String store_name;

    private List<DetailReviewDTO> reviews;

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public List<DetailReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<DetailReviewDTO> reviews) {
        this.reviews = reviews;
    }
}
