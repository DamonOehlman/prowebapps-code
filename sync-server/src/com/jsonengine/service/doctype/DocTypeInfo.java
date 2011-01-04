package com.jsonengine.service.doctype;

import java.io.Serializable;

/**
 * Represents a DTO that holds meta info for a docType.
 * 
 * @author @kazunori_279
 */
public class DocTypeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // access level required for read operations (read and query)
    private String accessLevelForRead = DocTypeService.ACCESS_LEVEL_PUBLIC;

    // access level required for write operations (create, update and delete)
    private String accessLevelForWrite = DocTypeService.ACCESS_LEVEL_PUBLIC;

    private String docType;

    /**
     * Returns access level for read or write access of this docType.
     * 
     * @param isRead
     *            true if it's read access
     * @return access level String
     */
    public String getAccessLevel(boolean isRead) {
        return isRead ? getAccessLevelForRead() : getAccessLevelForWrite();
    }

    public String getAccessLevelForRead() {
        return accessLevelForRead;
    }

    public String getAccessLevelForWrite() {
        return accessLevelForWrite;
    }

    public String getDocType() {
        return docType;
    }

    public void setAccessLevelForRead(String accessLevelForRead) {
        this.accessLevelForRead = accessLevelForRead;
    }

    public void setAccessLevelForWrite(String accessLevelForWrite) {
        this.accessLevelForWrite = accessLevelForWrite;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
