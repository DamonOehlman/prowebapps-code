package com.jsonengine.controller.user;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.jsonengine.common.JEUserUtils;
import com.jsonengine.controller.FrontController;

public class LoginURLController extends Controller {

    @Override
    protected Navigation run() throws Exception {

        final String URL = JEUserUtils.getLoginURL("/user/index");
        Map<String, String> result = new HashMap<String, String>();
        result.put("URL", URL);
        final String resultJson = JSON.encode(result);

        response.setContentType(FrontController.RESP_CONTENT_TYPE);
        final PrintWriter pw = response.getWriter();
        pw.append(resultJson);
        pw.close();

        return null;
    }

}
