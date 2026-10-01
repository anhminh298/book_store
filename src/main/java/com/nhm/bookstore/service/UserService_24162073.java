package com.nhm.bookstore.service;

import com.nhm.bookstore.dao.UserDAO_24162073;
import com.nhm.bookstore.model.User_24162073;

public class UserService_24162073 {
    private UserDAO_24162073 userDAO = new UserDAO_24162073();

    public boolean register(User_24162073 user) {
        try {
            userDAO.register(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User_24162073 login(String email, String passwd) {
        User_24162073 user = userDAO.login(email, passwd);
        if (user != null) {
            userDAO.updateLastLogin(user.getId());
        }
        return user;
    }

    public User_24162073 findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userDAO.emailExists(email);
    }
}
