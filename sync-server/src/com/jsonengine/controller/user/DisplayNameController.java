package com.jsonengine.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.jsonengine.common.JEUserUtils;

public class DisplayNameController extends Controller {

    @Override
    protected Navigation run() throws Exception {
        requestScope("displayName", JEUserUtils.getDisplayName());
        return forward("displayName.jsp");
    }
}
