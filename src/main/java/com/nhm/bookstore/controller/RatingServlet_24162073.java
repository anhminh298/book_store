package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.Rating_24162073;
import com.nhm.bookstore.model.User_24162073;
import com.nhm.bookstore.service.RatingService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/review")
public class RatingServlet_24162073 extends HttpServlet {

    private RatingService_24162073 ratingService = new RatingService_24162073();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User_24162073 user = (session != null) ? (User_24162073) session.getAttribute("user") : null;
        
        String bookidStr = request.getParameter("bookid");
        
        if (user == null) {
            if (bookidStr != null) {
                response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookidStr);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }

        String ratingStr = request.getParameter("rating");
        String reviewText = request.getParameter("reviewText");

        try {
            int bookid = Integer.parseInt(bookidStr);
            int ratingValue = Integer.parseInt(ratingStr);

            Rating_24162073 rating = new Rating_24162073();
            rating.setUserid(user.getId());
            rating.setBookid(bookid);
            rating.setRating((short) ratingValue);
            rating.setReviewText(reviewText);

            ratingService.addOrUpdateRating(rating);

            response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookid);
        } catch (Exception e) {
            if (bookidStr != null) {
                response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookidStr);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        }
    }
}
