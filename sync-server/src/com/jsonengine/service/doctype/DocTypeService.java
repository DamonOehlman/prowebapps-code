package com.jsonengine.service.doctype;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;

import com.google.appengine.api.datastore.Transaction;
import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JEUtils;
import com.jsonengine.model.JEDoc;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;

/**
 * Provices various methods regarding {@link DocTypeInfo}, including access
 * control.
 * 
 * @author @kazunori_279
 */
public class DocTypeService {

    /**
     * An access level where only administrator is allowed to access.
     */
    public static final String ACCESS_LEVEL_ADMIN = "admin";

    /**
     * An access level where only the creator of the doc is allowed to access.
     */
    public static final String ACCESS_LEVEL_PRIVATE = "private";

    /**
     * An access level where only an authenticated user is allowed to access.
     */
    public static final String ACCESS_LEVEL_PROTECTED = "protected";

    /**
     * An access level where anyone is allowed to access.
     */
    public static final String ACCESS_LEVEL_PUBLIC = "public";

    /**
     * docId for storing DocTypeInfo
     */
    public static final String DOCID_DOCTYPEINFO_PREFIX = "_docTypeInfo:";

    /**
     * docType name for storing DocTypeInfo
     */
    public static final String DOCTYPE_DOCTYPEINFO = "_docTypeInfo";

    private static final String PROP_ACCESS_LEVEL_FOR_READ =
        "accessLevelForRead";

    private static final String PROP_ACCESS_LEVEL_FOR_WRITE =
        "accessLevelForWrite";

    private static final String PROP_DOC_TYPE = "docType";

    private static final long serialVersionUID = 1L;

    private CRUDRequest createCRUDRequestForDocTypeInfo(String docType,
            Map<String, Object> map) {
        final CRUDRequest cr = new CRUDRequest(map);
        cr.setDocType(DOCTYPE_DOCTYPEINFO);
        cr.setDocId(DOCID_DOCTYPEINFO_PREFIX + docType);
        cr.setRequestedAt((new JEUtils()).getGlobalTimestamp());
        cr.setAdmin(true);
        return cr;
    }

    private String getDocIdOfDocTypeInfo(String docType) {
        return DOCID_DOCTYPEINFO_PREFIX + docType;
    }

    /**
     * Returns {@link DocTypeInfo} for specified docType.
     * 
     * @param docType
     *            docType to get.
     * @return {@link DocTypeInfo} for the docType
     */
    public DocTypeInfo getDocTypeInfo(String docType) {

        // check docType is available
        assert docType != null;

        // try to get it from Memcache
        DocTypeInfo jdti =
            (DocTypeInfo) Memcache.get(getDocIdOfDocTypeInfo(docType));
        if (jdti != null) {
            return jdti;
        }

        // try to get docTypeInfo by docType
        final CRUDRequest cr =
            createCRUDRequestForDocTypeInfo(
                docType,
                new HashMap<String, Object>());
        final Transaction tx = Datastore.beginTransaction();
        try {
            final JEDoc jeDoc = (new CRUDService()).getJEDoc(tx, cr);
            final Map<String, Object> map = jeDoc.getDocValues();
            jdti = new DocTypeInfo();
            jdti.setDocType((String) map.get(PROP_DOC_TYPE));
            jdti.setAccessLevelForRead((String) map
                .get(PROP_ACCESS_LEVEL_FOR_READ));
            jdti.setAccessLevelForWrite((String) map
                .get(PROP_ACCESS_LEVEL_FOR_WRITE));
            Memcache.put(getDocIdOfDocTypeInfo(docType), jdti);
            tx.commit();
        } catch (JEConflictException e) {
            throw new IllegalStateException(e);
        } catch (JENotFoundException e) {
            // ignore if it's not found
        }
        return jdti;
    }

    /**
     * Checks if the docType is accessible for specified requestor, creator and
     * read/write mode.
     * 
     * @param docType
     *            docType to be checked.
     * @param requestedBy
     *            An user ID of the requestor (null means the user has not
     *            authenticated).
     * @param createdBy
     *            An user ID of the creator of the doc (null means that this is
     *            a create request).
     * @param isRead
     *            true if this is a read access (false if it's a write access)
     * @param isAdmin
     *            true if the requestor is an administrator.
     * @return true if the access is allowed.
     */
    public boolean isAccessible(String docType, String requestedBy,
            String displayName, String createdBy, boolean isRead,
            boolean isAdmin) {

        // get docTypeInfo
        final DocTypeInfo jdti = getDocTypeInfo(docType);

        // it it's admin access, allow it
        if (isAdmin) {
            return true;
        }

        // if there's no docTypeInfo specified, deny all access
        if (jdti == null) {
            return true; // TODO disallow it after preparing the admin console
        }

        // if it's "public", allow all accesses
        if (ACCESS_LEVEL_PUBLIC.equals(jdti.getAccessLevel(isRead))) {
            return true;
        }

        // if it's "protected", check requestor has an ID
        if (ACCESS_LEVEL_PROTECTED.equals(jdti.getAccessLevel(isRead))) {
            final boolean accessible = requestedBy != null;
            if(accessible && !isRead) {
                return (displayName != null);
            }
            return accessible;
        }

        // if it's "private", check if this is a create request, requestor =
        // creator, or an admin access
        if (ACCESS_LEVEL_PRIVATE.equals(jdti.getAccessLevel(isRead))) {
            final boolean isCreateRequest =
                !isRead && createdBy == null && requestedBy != null;
            final boolean isCreatorAccess =
                requestedBy != null && requestedBy.equals(createdBy);
            final boolean accessible = isCreateRequest || isCreatorAccess || isAdmin;
            if(accessible && !isRead) {
                return (displayName != null);
            }
            return accessible;
        }

        // otherwise, disallow the access
        return false;
    }

    /**
     * Checks if the docType is able to be queried.
     * 
     * @param docType
     *            docType to be checked.
     * @param requestedBy
     *            An user ID of the requestor (null means the user has not
     *            authenticated).
     * @param isAdmin
     *            true if the requestor is an administrator.
     * @return true if the access is allowed.
     */
    public boolean isAccessibleByQuery(String docType, String requestedBy,
            boolean isAdmin) {

        // get docTypeInfo
        final DocTypeInfo jdti = getDocTypeInfo(docType);

        // it it's admin access, allow it
        if (isAdmin) {
            return true;
        }

        // if there's no docTypeInfo specified deny it
        if (jdti == null) {
            return true; // TODO disallow it after preparing the admin console
        }

        // if there's no docTypeInfo specified, or it's "public", allow all
        // accesses
        if (ACCESS_LEVEL_PUBLIC.equals(jdti.getAccessLevel(true))) {
            return true;
        }

        // if it's "protected", check requestor has an ID
        if (ACCESS_LEVEL_PROTECTED.equals(jdti.getAccessLevel(true))) {
            return requestedBy != null;
        }

        // if it's "private", check requestor has an ID
        if (ACCESS_LEVEL_PRIVATE.equals(jdti.getAccessLevel(true))) {
            return requestedBy != null;
        }

        // otherwise, disallow the access
        return false;
    }

    /**
     * Create and save a {@link DocTypeInfo} for specified docType.
     * 
     * @param docType
     *            docType for the {@link DocTypeInfo}.
     * @param accessLevelForRead
     *            access level specified for this docType on read operations.
     * @param accessLevelForWrite
     *            access level specified for this docType on write operations.
     * @return a {@link DocTypeInfo} created.
     * @throws JEAccessDeniedException
     * @throws JEConflictException
     */
    public void saveDocTypeInfo(String docType, String accessLevelForRead,
            String accessLevelForWrite) throws JEConflictException,
            JEAccessDeniedException {

        // prepare for Map
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(PROP_ACCESS_LEVEL_FOR_READ, accessLevelForRead);
        map.put(PROP_ACCESS_LEVEL_FOR_WRITE, accessLevelForWrite);
        map.put(PROP_DOC_TYPE, docType);

        // prepare for CRUDRequest
        final CRUDRequest cr = createCRUDRequestForDocTypeInfo(docType, map);

        // save it
        // if existing JEDoc is not found, create new one
        final JEDoc jeDoc = JEDoc.createJEDoc(cr);

        // update properties (build index)
        jeDoc.update(cr);

        // save JEDoc
        final Transaction tx = Datastore.beginTransaction();
        try {
            Datastore.put(tx, jeDoc);
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }

        // clear cache
        Memcache.delete(getDocIdOfDocTypeInfo(docType));
    }

}
