package com.tinypet;

import com.googlecode.objectify.ObjectifyService;
import com.tinypet.model.Petition;
import com.tinypet.model.Tag;
import com.tinypet.model.User;
import com.tinypet.model.Signature;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener
public class ObjectifyHelper implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectifyService.init();

        ObjectifyService.register(Petition.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Tag.class);
        ObjectifyService.register(Signature.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
