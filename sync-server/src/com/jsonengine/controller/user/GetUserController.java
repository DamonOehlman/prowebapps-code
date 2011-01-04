package com.jsonengine.controller.user;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.users.User;
import com.jsonengine.common.JEUserUtils;
import com.jsonengine.controller.FrontController;

public class GetUserController extends Controller {

    @Override
    protected Navigation run() throws Exception {
        
        Map<String, Object> result = new HashMap<String, Object>();
        final User user = JEUserUtils.getUser();
        result.put("user", user);
        final String displayName = JEUserUtils.getDisplayName();
        result.put("displayName", displayName);
        final String resultJson = JSON.encode(result);

        response.setContentType(FrontController.RESP_CONTENT_TYPE);
        final PrintWriter pw = response.getWriter();
        pw.append(resultJson);
        pw.close();        

        return null;
    }

}
