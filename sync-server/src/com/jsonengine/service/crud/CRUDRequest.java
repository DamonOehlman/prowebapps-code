package com.jsonengine.service.crud;

import java.util.Map;

import net.arnx.jsonic.JSON;

import com.jsonengine.common.JERequest;
import com.jsonengine.model.JEDoc;

/**
 * Holds various request parameters required for processing jsonengine CRUD
 * operations.
 * 
 * @author @kazunori_279
 */
public class CRUDRequest extends JERequest {

    // if not null, check update conflict
    private Long checkUpdatesAfter;

    // JSON document ID
    private String docId;

    // the original JSON document sent from a client
    private final String jsonDoc;

    // a Map which is decoded from the jsonDoc
    private final Map<String, Object> jsonMap;

    /**
     * Creates a CRUDRequest without any JSON document.
     */
    public CRUDRequest() {
        jsonDoc = null;
        jsonMap = null;
    }

    /**
     * Creates a CRUDRequest instance from specified {@link Map}.
     * 
     * @param jsonMap
     */
    public CRUDRequest(Map<String, Object> jsonMap) {
        this.jsonMap = jsonMap;
        this.jsonDoc = null;
    }

    /**
     * Creates a CRUDRequest instance from specified JSON document.
     * 
     * @condParam jsonDoc
     */
    @SuppressWarnings("unchecked")
    public CRUDRequest(String jsonDoc) {
        this.jsonDoc = jsonDoc;
        if (jsonDoc != null) {
            // decode jsonDoc and fill it into jsonMap
            jsonMap = JSON.decode(jsonDoc, Map.class);
            final Object docId = jsonMap.get(JEDoc.PROP_NAME_DOCID);
            if (docId != null) {
                setDocId(docId.toString());
            }
        } else {
            jsonMap = null;
        }
    }

    public Long getCheckUpdatesAfter() {
        return checkUpdatesAfter;
    }

    public String getDocId() {
        return docId;
    }

    public String getJsonDoc() {
        return jsonDoc;
    }

    public Map<String, Object> getJsonMap() {
        return jsonMap;
    }

    public void setCheckUpdatesAfter(Long checkUpdatesAfter) {
        this.checkUpdatesAfter = checkUpdatesAfter;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setJsonDoc(String jsonDoc) {
        throw new IllegalStateException("Operation not supported");
    }

    public void setJsonMap(Map<String, Object> jsonMap) {
        throw new IllegalStateException("Operation not supported");
    }

}
