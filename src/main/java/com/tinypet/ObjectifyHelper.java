package com.tinypet;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.tinypet.dao.TagDao;
import com.tinypet.model.Petition;
import com.tinypet.model.Tag;
import com.tinypet.model.User;
import com.tinypet.model.Signature;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.List;


@WebListener
public class ObjectifyHelper implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.init();

        ObjectifyService.register(Petition.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Tag.class);
        ObjectifyService.register(Signature.class);

        //Création tag
        List<String> initialTags = Arrays.asList("Environment", "Justice", "Politics"); // List of initial tags

        ObjectifyService.run(new VoidWork() {
            public void vrun() {
                TagDao tagDao = new TagDao();

                for (String tagName : initialTags) {
                    Tag existingTag = tagDao.getTagByName(tagName);

                    if (existingTag == null) {
                        Tag newTag = new Tag(tagName);
                        tagDao.save(newTag);
                    }
                }
            }
        });
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
