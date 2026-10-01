package com.nhm.bookstore.controller;

import com.nhm.bookstore.model.Author_24162073;
import com.nhm.bookstore.model.Book_24162073;
import com.nhm.bookstore.service.AuthorService_24162073;
import com.nhm.bookstore.service.BookService_24162073;
import com.nhm.bookstore.service.RatingService_24162073;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/home")
public class HomeServlet_24162073 extends HttpServlet {

    private AuthorService_24162073 authorService = new AuthorService_24162073();
    private BookService_24162073 bookService = new BookService_24162073();
    private RatingService_24162073 ratingService = new RatingService_24162073();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String authorIdStr = request.getParameter("authorId");
        String pageStr = request.getParameter("page");
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        List<Author_24162073> allAuthors = authorService.getAll();
        request.setAttribute("authors", allAuthors);

        if (authorIdStr != null && !authorIdStr.isEmpty()) {
            try {
                int authorId = Integer.parseInt(authorIdStr);
                Author_24162073 selectedAuthor = authorService.getById(authorId);
                int pageSize = 3;
                List<Book_24162073> books = bookService.getByAuthorId(authorId, page, pageSize);
                for (Book_24162073 book : books) {
                    book.setAuthors(authorService.getByBookId(book.getBookid()));
                    book.setRatingCount(ratingService.countByBookId(book.getBookid()));
                }
                int totalCount = bookService.countByAuthorId(authorId);
                int totalPages = (int) Math.ceil((double) totalCount / pageSize);

                request.setAttribute("books", books);
                request.setAttribute("currentAuthorId", authorId);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("selectedAuthor", selectedAuthor);
            } catch (NumberFormatException e) {
                // Ignore invalid authorId
            }
        } else {
            Map<Author_24162073, List<Book_24162073>> authorBooksMap = new LinkedHashMap<>();
            for (Author_24162073 author : allAuthors) {
                List<Book_24162073> books = bookService.getByAuthorId(author.getAuthorId(), 1, 3);
                for (Book_24162073 book : books) {
                    book.setAuthors(authorService.getByBookId(book.getBookid()));
                    book.setRatingCount(ratingService.countByBookId(book.getBookid()));
                }
                authorBooksMap.put(author, books);
            }
            request.setAttribute("authorBooksMap", authorBooksMap);
        }

        request.getRequestDispatcher("/views/home.jsp").forward(request, response);
    }
}
