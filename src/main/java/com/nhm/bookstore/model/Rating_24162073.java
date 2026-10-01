package com.nhm.bookstore.model;

public class Rating_24162073 {
    private int userid;
    private int bookid;
    private int rating;
    private String reviewText;
    
    private transient String userFullname;

    public Rating_24162073() {
    }

    public Rating_24162073(int userid, int bookid, int rating, String reviewText) {
        this.userid = userid;
        this.bookid = bookid;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }
}
