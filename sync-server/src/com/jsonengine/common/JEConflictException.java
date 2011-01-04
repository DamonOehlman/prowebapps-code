package com.jsonengine.common;

/**
 * Represents the service has detected a conflict of updates between clients.
 * 
 * @author @kazunori_279
 */
public class JEConflictException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public JEConflictException(String msg) {
        super(msg);
    }
    
    public JEConflictException(Throwable th) {
        super(th);
    }
    

}
