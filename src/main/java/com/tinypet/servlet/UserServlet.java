package com.tinypet.servlet;

import com.tinypet.dao.UserDao;
import com.tinypet.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private UserDao userDao;
    @Override
    public void init() {
        userDao = new UserDao();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // implementation of getting a user...
        String userId = req.getPathInfo().substring(1); // Get user ID from the URL
       // User user = userDao.getById(Long.valueOf(userId));
       // req.setAttribute("user", user);
       // req.getRequestDispatcher("/user.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // implementation of creating a user...
        String name = req.getParameter("name");
        //User user = new User(name);
       // userDao.save(user);
        //resp.sendRedirect("/users/" + user.getId());
    }
}
