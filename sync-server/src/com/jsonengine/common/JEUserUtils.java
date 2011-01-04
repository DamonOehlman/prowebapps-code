package com.jsonengine.common;

import java.util.Date;

import org.slim3.datastore.Datastore;
import org.slim3.util.AppEngineUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.jsonengine.model.JEUser;

public class JEUserUtils {

    /**
     * Returns the current user's email address.
     * 
     * @return email address
     */
    public static String userEmail() {
        return getUser() != null ? getUser().getEmail() : "";
    }

    /**
     * Returns User object for the current user.
     * 
     * @return
     */
    public static User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }

    /**
     * Returns true if the user has Administrator role.
     * 
     * @return true if the user is admin
     */
    public static boolean isAdmin() {
        if (!isLoggedIn()) {
            return false;
        }
        return UserServiceFactory.getUserService().isUserAdmin();
    }

    /**
     * Returns true if the user has logged in.
     * 
     * @return true if logged in
     */
    public static boolean isLoggedIn() {
        User user = getUser();
        if (user == null) {
            return false;
        }
        if (user.getEmail().length() == 0) {
            return false;
        }
        return true;
    }

    public static String getLoginURL(String returnURL) {
        UserService userService = UserServiceFactory.getUserService();
        String URL = userService.createLoginURL(returnURL);
        if (AppEngineUtil.isDevelopment()) {
            URL = JEUtils.getRequestServer() + URL;
        }
        return URL;
    }

    public static String getLogoutURL(String returnURL) {
        UserService userService = UserServiceFactory.getUserService();
        String URL = userService.createLogoutURL(returnURL);
        if (AppEngineUtil.isDevelopment()) {
            URL = JEUtils.getRequestServer() + URL;
        }
        return URL;
    }

    public static String getDisplayName() {
        final User user = getUser();
        if (user == null) {
            return null;
        }
        Key key = Datastore.createKey(JEUser.class, user.getEmail());
        JEUser jeUser = Datastore.getOrNull(JEUser.class, key);
        return (jeUser == null) ? null : jeUser.getDisplayName();
    }

    public static void putDisplayName(String displayName)
            throws JEDuplicateException {
        final String UNIQUE_KEY_DISPLAY_NAME = "UniqueKeyDisplayName";
        final User user = getUser();
        final Key key = Datastore.createKey(JEUser.class, user.getEmail());
        if (Datastore.putUniqueValue(UNIQUE_KEY_DISPLAY_NAME, displayName) == false) {
            throw new JEDuplicateException("the display name is already used");
        }
        Transaction tx = Datastore.beginTransaction();
        try {
            JEUser jeUser = Datastore.getOrNull(tx, JEUser.class, key);
            final long now = new Date().getTime();
            if (jeUser == null) {
                jeUser = new JEUser();
                jeUser.setKey(key);
                jeUser.setCreatedAt(now);
            }
            jeUser.setDisplayName(displayName);
            jeUser.setUpdatedAt(now);
            Datastore.put(tx, jeUser);

            // TODO: Run TQ to update past's memos.

            Datastore.commit(tx);

        } catch (Exception e) {
            Datastore.rollback(tx);
            Datastore.deleteUniqueValue(UNIQUE_KEY_DISPLAY_NAME, displayName);
        }
    }
}
