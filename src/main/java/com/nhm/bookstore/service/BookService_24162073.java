package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.AuthorDAO_24162073;
import com.nhm.bookstore.dao.BookAuthorDAO_24162073;
import com.nhm.bookstore.dao.BookDAO_24162073;
import com.nhm.bookstore.dao.RatingDAO_24162073;
import com.nhm.bookstore.model.Book_24162073;

import java.util.List;

public class BookService_24162073 {
    private BookDAO_24162073 bookDAO = new BookDAO_24162073();
    private BookAuthorDAO_24162073 bookAuthorDAO = new BookAuthorDAO_24162073();
    private RatingDAO_24162073 ratingDAO = new RatingDAO_24162073();
    private AuthorDAO_24162073 authorDAO = new AuthorDAO_24162073();

    public List<Book_24162073> getByAuthorId(int authorId, int page, int pageSize) {
        return bookDAO.findByAuthorId(authorId, page, pageSize);
    }

    public Book_24162073 getById(int bookid) {
        Book_24162073 book = bookDAO.findById(bookid);
        if (book != null) {
            book.setAuthors(authorDAO.findByBookId(bookid));
            book.setRatingCount(ratingDAO.countByBookId(bookid));
        }
        return book;
    }

    public List<Book_24162073> getAll(int page, int pageSize) {
        return bookDAO.findAll(page, pageSize);
    }

    public List<Book_24162073> getAllForAdmin(int page, int pageSize) {
        return bookDAO.findAllForAdmin(page, pageSize);
    }

    public Book_24162073 getByIdForAdmin(int bookId) {
        return bookDAO.findByIdForAdmin(bookId);
    }

    public int countAll() {
        return bookDAO.countAll();
    }

    public int countAllForAdmin() {
        return bookDAO.countAllForAdmin();
    }

    public int countByAuthorId(int authorId) {
        return bookDAO.countByAuthorId(authorId);
    }

    public boolean create(Book_24162073 book, int authorId) {
        try {
            int bookid = bookDAO.insert(book);
            if (bookid != -1) {
                bookAuthorDAO.insert(bookid, authorId);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean create(Book_24162073 book, List<Integer> authorIds) {
        try {
            int bookid = bookDAO.insert(book);
            if (bookid != -1 && authorIds != null) {
                for (Integer authorId : authorIds) {
                    bookAuthorDAO.insert(bookid, authorId);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Book_24162073 book, int authorId) {
        try {
            bookDAO.update(book);
            bookAuthorDAO.deleteByBookId(book.getBookid());
            bookAuthorDAO.insert(book.getBookid(), authorId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Book_24162073 book, List<Integer> authorIds) {
        try {
            bookDAO.update(book);
            if (authorIds != null) {
                bookAuthorDAO.deleteByBookId(book.getBookid());
                for (Integer authorId : authorIds) {
                    bookAuthorDAO.insert(book.getBookid(), authorId);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int bookid) {
        return bookDAO.delete(bookid);
    }
}
