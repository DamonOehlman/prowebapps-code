package com.jsonengine.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.users.User;
import com.jsonengine.common.JEUserUtils;

public class IndexController extends Controller {

    @Override
    public Navigation run() {

        final User user = JEUserUtils.getUser();
        if (user == null) {
            final String loginURL = JEUserUtils.getLoginURL("/user/index");
            return redirect(loginURL);
        }
        requestScope("user", user);

        final String displayName = JEUserUtils.getDisplayName();
        if (displayName == null) {
            return redirect("/user/displayName");
        }
        requestScope("displayName", displayName);

        return forward("index.jsp");
    }

}