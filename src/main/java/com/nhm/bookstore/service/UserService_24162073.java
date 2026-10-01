package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.UserDAO_24162073;
import com.nhm.bookstore.model.User_24162073;

import java.sql.SQLException;

public class UserService_24162073 {
    private final UserDAO_24162073 userDAO = new UserDAO_24162073();

    public boolean register(User_24162073 user, String passwordHash) throws SQLException {
        return userDAO.register(user, passwordHash);
    }

    public User_24162073 login(String email, String password) throws SQLException {
        User_24162073 user = userDAO.login(email, password);
        if (user != null) userDAO.updateLastLogin(user.getId());
        return user;
    }

    public User_24162073 findByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }

    public boolean emailExists(String email) throws SQLException {
        return userDAO.emailExists(email);
    }
}
