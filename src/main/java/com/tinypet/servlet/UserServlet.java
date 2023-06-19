package com.tinypet.servlet;

import com.tinypet.dao.SignatureDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Petition;
import com.tinypet.model.Signature;
import com.tinypet.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {"/users/*"})
public class UserServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private final SignatureDao signatureDao = new SignatureDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = new Gson().fromJson(req.getReader(), User.class);

        User existingUser = userDao.getUser(user.getId());
        String message;

        if (existingUser == null) {
            User createdUser = userDao.createUser(user);
            message = "User has been created.";
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(new Gson().toJson(new UserResponse(createdUser, UserDao.createJwt(createdUser), message)));
        } else {
            message = "User has logged in.";
             resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(new Gson().toJson(new UserResponse(existingUser, UserDao.createJwt(existingUser), message)));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String token = authHeader.substring(7);
        User user = userDao.validateCredential(token);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You need to be logged in to create a petition");
            return;
        }

            String pathInfo = req.getPathInfo();

            // On check si on est /users/
            if (pathInfo == null || pathInfo.equals("/")) {
                handleUserInfoRequest(user, resp);
                //  On check si on est /users/signatures
            } else if (pathInfo.equals("/signatures")) {
                handleUserSignaturesRequest(user.getId(), resp);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
            }

    }

    private void handleUserInfoRequest(User user, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(new Gson().toJson(user));
    }

    private void handleUserSignaturesRequest(String userId, HttpServletResponse resp) throws IOException {
        List<Signature> signatures = signatureDao.getSignaturesByUser(userId);
        resp.getWriter().println(new Gson().toJson(signatures));
    }



    public static class UserResponse {
        public final User user;
        public final String jwt;
        public final String message;

        public UserResponse(User user, String jwt, String message) {
            this.user = user;
            this.jwt = jwt;
            this.message = message;
        }
    }
}