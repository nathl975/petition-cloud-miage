package com.tinypet.servlet;

import java.io.IOException;

import com.google.gson.Gson;
import com.tinypet.dao.PetitionDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Petition;
import com.tinypet.model.User;
import com.googlecode.objectify.Key;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet(name = "PetitionServlet", urlPatterns = {"/petitions/*"})
public class PetitionServlet extends HttpServlet {
    private final PetitionDao petitionDao = new PetitionDao();
    private final UserDao userDao = new UserDao();

    // GET /petitions/{petitionId}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        Long id = Long.parseLong(pathInfo.substring(1)); // Excludes the initial "/"
        Petition petition = petitionDao.getPetition(id);
        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(petition));
    }

    // POST /petitions
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user = userDao.validateIdToken(req);
        if (user == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid ID token.");
            return;
        }
        Petition petitionFromReq = new Gson().fromJson(req.getReader(), Petition.class);

        petitionFromReq.setOwner(user.getId().toString());

        com.googlecode.objectify.Key<Petition> createdPetition = petitionDao.createPetition(petitionFromReq);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(createdPetition));
    }

}
