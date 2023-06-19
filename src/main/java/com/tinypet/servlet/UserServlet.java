package com.tinypet.servlet;

import com.tinypet.dao.SignatureDao;
import com.tinypet.dao.UserDao;
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

    //On génère une clé secrète pour signer le token
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = userDao.validateIdToken(req);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authorized to access this resource.");
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user id.");
        } else {
            String[] splits = pathInfo.split("/");
            String endpoint = splits[1];

            // On check si on est /users/isLogged
            if (endpoint.equals("isLogged")) {
                handleIsLogged(req, resp);
            } else {
                // On check si l'utilisateur à les droits pour accéder à l'information
                if (!user.getId().equals(endpoint)) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to access another user's information.");
                    return;
                }
                // On check si on est /users/{userId}
                if (splits.length == 2) {
                    handleUserInfoRequest(user, resp);
                    //  On check si on est /users/{userId}/signatures
                } else if (splits.length == 3 && splits[2].equals("signatures")) {
                    handleUserSignaturesRequest(endpoint, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User userFromReq = new Gson().fromJson(req.getReader(), User.class);
        User existingUser = userDao.getUser(userFromReq.getId());

        if (existingUser == null) {
            User createdUser = userDao.createUser(userFromReq);
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(createdUser));
        } else {
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(existingUser));
        }
    }

    private void handleUserInfoRequest(User user, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(new Gson().toJson(user));
    }

    private void handleUserSignaturesRequest(String userId, HttpServletResponse resp) throws IOException {
        List<Signature> signatures = signatureDao.getSignaturesByUser(userId);
        resp.getWriter().println(new Gson().toJson(signatures));
    }

    private void handleIsLogged(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String jwt = authHeader.substring(7); // Strip 'Bearer ' prefix

        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            String userId = claims.getBody().getSubject();

            User user = userDao.getUser(userId);
            if (user != null) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println(new Gson().toJson(user));
            } else {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User does not exist.");
            }
        } catch (JwtException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT.");
        }
    }
}
