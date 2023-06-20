package com.tinypet.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.tinypet.dao.SignatureDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Signature;
import com.tinypet.model.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SignatureServlet extends HttpServlet {

    private final SignatureDao signatureDao = new SignatureDao();
    private final UserDao userDao = new UserDao();

    // GET /petitions/{petitionId}/signatures
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Long petitionId = Long.parseLong(pathInfo.substring(1));

        List<Signature> signatures = signatureDao.getSignaturesByPetition(petitionId);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(signatures));
    }

    // POST /petitions/{petitionId}/signatures
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String[] pathSegments = pathInfo.split("/");
        Long petitionId = Long.parseLong(pathSegments[1]);

        // Authentification
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String token = authHeader.substring(7);
        User user = userDao.validateCredential(token);

        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You need to be logged in to sign a petition");
            return;
        }

        Signature signature = new Signature(null, user.getId().toString(), petitionId);
        com.googlecode.objectify.Key<Signature> key = signatureDao.createSignature(signature);
        user.addSignedPetition(petitionId);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(key));
    }

}
