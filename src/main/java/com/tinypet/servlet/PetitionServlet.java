package com.tinypet.servlet;

import java.io.IOException;

import com.google.gson.Gson;
import com.tinypet.dao.PetitionDao;
import com.tinypet.dao.UserDao;
import com.tinypet.model.Petition;
import com.tinypet.model.User;
import com.googlecode.objectify.Key;

import javax.servlet.ServletException;
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
            // Delegate to SignatureServlet
            signatureServlet.service(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    // GET /petitions/{petitionId}
    // GET /petitions
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /petitions
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(petitionDao.getTop100Petitions()));
            return;
        }else {
            Long id = Long.parseLong(pathInfo.substring(1)); // Excludes the initial "/"
            Petition petition = petitionDao.getPetition(id);
            resp.setContentType("application/json");
            resp.getWriter().println(new Gson().toJson(petition));
        }
    }

    // POST /petitions
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Petition petitionFromReq = new Gson().fromJson(req.getReader(), Petition.class);

        com.googlecode.objectify.Key<Petition> createdPetition = petitionDao.createPetition(petitionFromReq);

        resp.setContentType("application/json");
        resp.getWriter().println(new Gson().toJson(createdPetition));
    }

}
