package com.jsonengine.common;

/**
 * Represents the request is malformed in regards of the query rules.
 * 
 * @author @kazunori_279
 */
public class JEAccessDeniedException extends Exception {

    private static final long serialVersionUID = 1L;

    public JEAccessDeniedException() {
        super();
    }

    public JEAccessDeniedException(String msg) {
        super(msg);
    }

    public JEAccessDeniedException(Throwable th) {
        super(th);
    }

}
