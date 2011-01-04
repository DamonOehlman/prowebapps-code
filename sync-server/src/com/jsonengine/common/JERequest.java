package com.jsonengine.common;

import com.jsonengine.service.doctype.DocTypeService;

/**
 * Holds various request parameters required for processing jsonengine
 * operations.
 * 
 * @author @kazunori_279
 */
public abstract class JERequest {

    // docType of this request
    private String docType;

    // true if this is a request from admin
    private boolean isAdmin = false;

    // a timestamp of the request time
    private long requestedAt;

    // an User ID of the requestor
    private String requestedBy;

    // an User displayName of the requestor
    private String displayName;

    public JERequest() {
        super();
    }

    public String getDocType() {
        return docType;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the docType is accessible for this request.
     * 
     * @param createdBy
     *            an user ID of the creator of the doc.
     * @param isRead
     *            true if it's a read operation (false for write operation)
     * @return true if the access is allowed.
     */
    public boolean isAccessible(String createdBy, boolean isRead) {
        return (new DocTypeService()).isAccessible(
            docType,
            requestedBy,
            displayName,
            createdBy,
            isRead,
            isAdmin);
    }

    /**
     * Checks if the docType is accessible by a query.
     * 
     * @return true if the access is allowed.
     */
    public boolean isAccessibleByQuery() {
        return (new DocTypeService()).isAccessibleByQuery(
            docType,
            requestedBy,
            isAdmin);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setDocType(String docName) {
        this.docType = docName;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}