package com.tinypet.servlet;

import java.io.IOException;

import com.google.gson.Gson;
import com.tinypet.dao.PetitionDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Petition;
import com.tinypet.model.Signature;
import com.tinypet.model.User;
import com.googlecode.objectify.Key;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet(name = "PetitionServlet", urlPatterns = {"/petitions/*"})
public class PetitionServlet extends HttpServlet {
    private final PetitionDao petitionDao = new PetitionDao();
    private final UserDao userDao = new UserDao();
    private final SignatureServlet signatureServlet = new SignatureServlet();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if(pathInfo == null) {
            super.service(req, resp);
            return;
        }
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length > 2 && "signatures".equals(pathParts[2])) {
            signatureServlet.service(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    // GET /petitions/{petitionId}
    // GET /petitions
    // GET /petitions/user
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /petitions
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(petitionDao.getTop100Petitions()));
            return;
        } else if (pathInfo.equals("/user")) {
            // GET /petitions/user
            String authHeader = req.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
                return;
            }

            String token = authHeader.substring(7);
            User user = userDao.validateCredential(token);

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You need to be logged in to view signed petitions");
                return;
            }

            List<Petition> userSignedPetitions = petitionDao.getSignedPetitionsByUser(user.getId());
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(userSignedPetitions));
        } else {
            // GET /petitions/{petitionId}
            Long id = Long.parseLong(pathInfo.substring(1));
            Petition petition = petitionDao.getPetition(id);
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(petition));
        }
    }


    // POST /petitions
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        Petition petitionFromReq = new Gson().fromJson(req.getReader(), Petition.class);

        petitionFromReq.setOwner(user.getId());
        petitionFromReq.setDate(new Date());
        petitionFromReq.setSignatureCount(0);

        com.googlecode.objectify.Key<Petition> createdPetition = petitionDao.createPetition(petitionFromReq);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(createdPetition));
    }

}
