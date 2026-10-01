package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.RatingDAO_24162073;
import com.nhm.bookstore.model.Rating_24162073;

import java.util.List;

public class RatingService_24162073 {
    private RatingDAO_24162073 ratingDAO = new RatingDAO_24162073();

    public List<Rating_24162073> getByBookId(int bookid) {
        return ratingDAO.findByBookId(bookid);
    }

    public int countByBookId(int bookid) {
        return ratingDAO.countByBookId(bookid);
    }

    public void addOrUpdateRating(Rating_24162073 rating) {
        if (ratingDAO.exists(rating.getUserid(), rating.getBookid())) {
            ratingDAO.update(rating);
        } else {
            ratingDAO.insert(rating);
        }
    }
}
