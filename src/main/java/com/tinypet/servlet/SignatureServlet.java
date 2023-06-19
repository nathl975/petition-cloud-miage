package com.tinypet.servlet;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.cloud.datastore.Key;
import com.tinypet.dao.PetitionDao;
import com.tinypet.dao.SignatureDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Petition;
import com.tinypet.model.Signature;
import com.tinypet.model.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SignatureServlet", urlPatterns = {"/petitions/*/signatures"})
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
        Long petitionId = Long.parseLong(pathInfo.substring(1));

        // Authentification
        User user = userDao.validateIdToken(req);
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid ID token.");
            return;
        }

        Signature signature = new Signature(null, user.getId().toString(), petitionId);
        com.googlecode.objectify.Key<Signature> key = signatureDao.createSignature(signature);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(key));
    }

}
