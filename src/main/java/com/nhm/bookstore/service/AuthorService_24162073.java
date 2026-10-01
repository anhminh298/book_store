package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.AuthorDAO_24162073;
import com.nhm.bookstore.model.Author_24162073;

import java.util.List;

public class AuthorService_24162073 {
    private AuthorDAO_24162073 authorDAO = new AuthorDAO_24162073();

    public List<Author_24162073> getAll() {
        return authorDAO.findAll();
    }

    public Author_24162073 getById(int authorId) {
        return authorDAO.findById(authorId);
    }

    public List<Author_24162073> getByBookId(int bookid) {
        return authorDAO.findByBookId(bookid);
    }
}
