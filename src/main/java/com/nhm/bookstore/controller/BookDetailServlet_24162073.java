package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.Author_24162073;
import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.model.Rating_24162073;
import com.nhm.bookstore.service.AuthorService_24162073;
import com.nhm.bookstore.service.BookService_24162073;
import com.nhm.bookstore.service.RatingService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/book-detail")
public class BookDetailServlet_24162073 extends HttpServlet {

    private BookService_24162073 bookService = new BookService_24162073();
    private AuthorService_24162073 authorService = new AuthorService_24162073();
    private RatingService_24162073 ratingService = new RatingService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int bookid = Integer.parseInt(idStr);
            Book_24162073 book = bookService.getById(bookid);
            if (book == null) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            List<Author_24162073> authors = authorService.getByBookId(bookid);
            List<Rating_24162073> ratings = ratingService.getByBookId(bookid);
            int ratingCount = ratingService.countByBookId(bookid);

            request.setAttribute("book", book);
            request.setAttribute("authors", authors);
            request.setAttribute("ratings", ratings);
            request.setAttribute("ratingCount", ratingCount);

            request.getRequestDispatcher("/views/book-detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
