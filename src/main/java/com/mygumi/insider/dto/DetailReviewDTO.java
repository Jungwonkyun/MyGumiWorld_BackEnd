package com.mygumi.insider.dto;

import java.util.List;

public class DetailReviewDTO {

    private Long review_id;

    private String comment;

    private String user_id;

    private List<DetailImagePath> imagePath;

    @Override
    public String toString() {
        return "DetailReviewDTO{" +
                "review_id=" + review_id +
                ", comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    public Long getReview_id() {
        return review_id;
    }

    public void setReview_id(Long review_id) {
        this.review_id = review_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<DetailImagePath> getImagePath() {
        return imagePath;
    }

    public void setImagePath(List<DetailImagePath> imagePath) {
        this.imagePath = imagePath;
    }
}
