package com.jsonengine.common;

/**
 * Represents the request is denied because the doc is not accessible.
 * 
 * @author @kazunori_279
 */
public class JEInvalidRequestException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public JEInvalidRequestException() {
        super();
    }

    public JEInvalidRequestException(String msg) {
        super(msg);
    }

    public JEInvalidRequestException(Throwable th) {
        super(th);
    }

}
