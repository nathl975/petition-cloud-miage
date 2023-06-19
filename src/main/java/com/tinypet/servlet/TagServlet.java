package com.tinypet.servlet;

import com.google.gson.Gson;
import com.tinypet.dao.PetitionDao;
import com.tinypet.dao.TagDao;
import com.tinypet.model.Petition;
import com.tinypet.model.Tag;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/tags/*")
public class TagServlet extends HttpServlet {
    private final TagDao tagDao = new TagDao();
    private final PetitionDao petitionDao = new PetitionDao();

    // GET /tags/{tagId}/petitions
    // GET /tags/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Récupère tous les tags
            List<Tag> tags = tagDao.getAllTags();
            resp.getWriter().println(new Gson().toJson(tags));
        } else {
            // Récupère les pétitions d'un tag
            String[] splits = pathInfo.split("/");
            if (splits.length == 3 && splits[2].equals("petitions")) {
                String tagId = splits[1];
                List<Petition> petitions = petitionDao.getPetitionsByTag(tagId);
                resp.getWriter().println(new Gson().toJson(petitions));
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
            }
        }
    }
}
