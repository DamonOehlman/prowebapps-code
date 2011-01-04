package com.jsonengine.controller.user;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

import com.jsonengine.common.JEDuplicateException;
import com.jsonengine.common.JEUserUtils;
import com.jsonengine.meta.JEUserMeta;

public class UpdateDisplayNameController extends Controller {

    @Override
    protected Navigation run() throws Exception {
        if (!validate()) {
            return forward("index.jsp");
        }
        final String newDisplayName = asString("displayName");
        final String oldDisplayName = JEUserUtils.getDisplayName();
        if ((oldDisplayName == null)
            || (newDisplayName.equals(oldDisplayName) == false)) {
            try {
                JEUserUtils.putDisplayName(newDisplayName);
            } catch (JEDuplicateException e) {
                errors.put("displayName", e.getMessage());
                return forward("displayName.jsp");
            }
        }
        return redirect("/user/index");
    }

    protected boolean validate() {
        Validators v = new Validators(request);
        JEUserMeta meta = JEUserMeta.get();
        v.add(meta.displayName, v.required());
        return v.validate();
    }

}
