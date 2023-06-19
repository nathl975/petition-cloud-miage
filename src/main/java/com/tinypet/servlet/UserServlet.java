package com.tinypet.servlet;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.googlecode.objectify.ObjectifyService;
import com.tinypet.dao.SignatureDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Signature;
import com.tinypet.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.gson.Gson;
import com.tinypet.dao.UserDao;
import com.tinypet.model.User;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {"/users/*"})
public class UserServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();
    private final SignatureDao signatureDao = new SignatureDao();


    // /users/{userId}
    // /users/{userId}/signatures
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Authentification
        User user = userDao.validateIdToken(req);

        // Check si l'utilisateur existe
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to access this resource.");
            return;
        }

        String pathInfo = req.getPathInfo();

        // On split le path pour récupérer les informations
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user id.");
        } else {
           // On récupère le userID
            String[] splits = pathInfo.split("/");
            String userIdFromPath = splits[1];

            // On vérifie que c'est l'utilisateur qui demande ses informations
            if (!user.getId().equals(userIdFromPath)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to access another user's information.");
                return;
            }

            if (splits.length == 2) {
                // Si il y a 2 arguments '/users/{userId}', on sait que c'est pour récuperer les informations de l'utilisateur
                resp.getWriter().println(new Gson().toJson(user));
            } else if (splits.length == 3 && splits[2].equals("signatures")) {
                // Si il y a 3 arguments '/users/{userId}/signatures', on sait qu'on souhaite récupérer les signatures de l'utilisateur
                List<Signature> signatures = signatureDao.getSignaturesByUser(userIdFromPath);
                resp.getWriter().println(new Gson().toJson(signatures));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = userDao.validateIdToken(req);
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid ID token.");
            return;
        }

        User userFromReq = new Gson().fromJson(req.getReader(), User.class);

        userFromReq.setId(user.getId());

        User createdUser = userDao.createUser(userFromReq);
        if (createdUser == null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "User already exists.");
            return;
        }

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(createdUser));
    }

}
