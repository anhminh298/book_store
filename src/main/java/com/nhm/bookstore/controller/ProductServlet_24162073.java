package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.model.Author_24162073;
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

@WebServlet("/products")
public class ProductServlet_24162073 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BookService_24162073 bookService = new BookService_24162073();
        AuthorService_24162073 authorService = new AuthorService_24162073();
        RatingService_24162073 ratingService = new RatingService_24162073();

        // Pagination
        int page = 1;
        int pageSize = 6;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get all books with pagination
        List<Book_24162073> books = bookService.getAll(page, pageSize);

        // Load authors and rating count for each book
        for (Book_24162073 book : books) {
            List<Author_24162073> authors = authorService.getByBookId(book.getBookid());
            book.setAuthors(authors);
            book.setRatingCount(ratingService.countByBookId(book.getBookid()));
        }

        int totalCount = bookService.countAll();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        request.setAttribute("books", books);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCount", totalCount);

        request.getRequestDispatcher("/views/products.jsp").forward(request, response);
    }
}
