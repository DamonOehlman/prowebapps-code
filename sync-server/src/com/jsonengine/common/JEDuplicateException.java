package com.jsonengine.common;

/**
 * Notifies that the specified JSON document has not found.
 * 
 * @author @knj77
 */
public class JEDuplicateException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public JEDuplicateException(String msg) {
        super(msg);
    }
    
    public JEDuplicateException(Throwable th) {
        super(th);
    }   
}
