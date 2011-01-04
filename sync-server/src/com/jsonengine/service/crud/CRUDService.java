package com.jsonengine.service.crud;

import java.util.ConcurrentModificationException;

import net.arnx.jsonic.JSON;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.controller.task.DeleteDocTypeTaskController;
import com.jsonengine.model.JEDoc;

/**
 * Implements CRUD operations for jsonengine.
 * 
 * @author @kazunori_279
 */
public class CRUDService {

    /**
     * Deletes the specified JSON document from Datastore. You need to provide a
     * docId (via {@link CRUDRequest} parameter) to remove an existing document
     * with the same docId.
     * 
     * Or you can omit docId to delete all the documents under a docType. This
     * deletion will be processed background so it may take a while if there are
     * many docs to delete.
     * 
     * If checkConflict property of specified {@link CRUDRequest} is set true,
     * and you provide the original JSON document with _updatedAt property in
     * {@link CRUDRequest}, it checks if anyone has already updated the same
     * document. If yes, it throws a {@link JEConflictException}.
     * 
     * @condParam json JSON document string to be saved
     * @condParam jeReq {@link CRUDRequest}
     * @return docId of the saved JSON document.
     * @throws JENotFoundException
     *             if it can not find any JSON document with specified docId.
     * @throws JEConflictException
     *             if it detected a update confliction
     * @throws JEAccessDeniedException
     *             if the requestor is not allowed to delete the doc.
     */
    public void delete(CRUDRequest jeReq) throws JENotFoundException,
            JEConflictException, JEAccessDeniedException {

        // if there's docId specified, delete single doc. otherwise, delete all
        // the docs.
        if (jeReq.getDocId() != null) {
            deleteSingleDoc(jeReq);
        } else {
            DeleteDocTypeTaskController.addDeleteAllTask(jeReq.getDocType());
        }

    }

    private void deleteSingleDoc(CRUDRequest jeReq) throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // try to find an existing JEDoc for the docId
        final Transaction tx = Datastore.beginTransaction();
        final JEDoc jeDoc = getJEDoc(tx, jeReq);

        // check if accessible
        if (!jeReq.isAccessible(jeDoc.getCreatedBy(), false)) {
            throw new JEAccessDeniedException();
        }

        // remove it
        try {
            Datastore.delete(tx, jeDoc.getKey());
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }
    }

    /**
     * Returns a JSON document specified by the CRUDRequest's docId. Throws a
     * {@link JENotFoundException} if there's no such JSON document with the
     * docId. You can also pass a JSON document in the {@link CRUDRequest} to
     * check if it has been updated or not.
     * 
     * @condParam jeReq a CRUDRequest with docName to be retrieved.
     * @return a JSON document retrieved.
     * @throws JENotFoundException
     *             if it can not find any JSON document with specified docId.
     * @throws JEConflictException
     *             if checkConflict property of {@link CRUDRequest} is set true
     *             and if it detected that the JSON document in
     *             {@link CRUDRequest} is updated.
     * @throws JEAccessDeniedException
     *             if the requestor is not allowed to get this doc.
     */
    public String get(CRUDRequest jeReq) throws JENotFoundException,
            JEConflictException, JEAccessDeniedException {

        // try to get the JEDoc
        assert jeReq.getDocId() != null;
        final Transaction tx = Datastore.beginTransaction();
        final JEDoc jeDoc;
        try {
            jeDoc = getJEDoc(tx, jeReq);
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }

        // check if accessible
        if (!jeReq.isAccessible(jeDoc.getCreatedBy(), true)) {
            throw new JEAccessDeniedException();
        }
        return jeDoc.encodeJSON();
    }

    public JEDoc getJEDoc(Transaction tx, CRUDRequest jeReq)
            throws JEConflictException, JENotFoundException {

        // try to get specified JEDoc
        final Key jeDocKey = Datastore.createKey(JEDoc.class, jeReq.getDocId());
        JEDoc jeDoc = null;
        try {
            jeDoc = Datastore.get(tx, JEDoc.class, jeDocKey);
        } catch (EntityNotFoundRuntimeException e) {
            throw new JENotFoundException(e);
        }

        // check if it's found
        if (jeDoc == null) {
            throw new JENotFoundException("JEDoc not found");
        }

        // check update confliction by checking updatedAt
        if (jeDoc != null
            && jeReq.getCheckUpdatesAfter() != null
            && isConflicted(jeReq, jeDoc)) {
            throw new JEConflictException("Detedted a conflict by_updatedAt");
        }
        return jeDoc;
    }

    private boolean isConflicted(CRUDRequest jeReq, JEDoc jeDoc) {
        return jeReq.getCheckUpdatesAfter() != null
            && jeDoc.getUpdatedAt() > jeReq.getCheckUpdatesAfter().longValue();
    }

    /**
     * Creates or updates specified JSON document into Datastore. If you provide
     * a docId (via {@link CRUDRequest} parameter), it checks if there's
     * existing document with the same docId. If yes, it updates it. If no, it
     * creates new one.
     * 
     * If checkConflict property of specified {@link CRUDRequest} is set true,
     * it checks if anyone has already updated the same document. If yes, it
     * throws a {@link JEConflictException}.
     * 
     * @param jeReq
     *            {@link CRUDRequest}
     * @param isUpdateOnly
     *            set true if you only want to update the doc and do not insert
     *            it.
     * @return the saved JSON document with _docId and _updatedAt properties
     * @throws JEConflictException
     *             if it detected a update confliction
     * @throws JEAccessDeniedException
     *             if the requestor is not allowed to put a doc.
     * @throws JENotFoundException 
     *             if there's no existing doc (if isUpdateOnly).
     */
    public String put(CRUDRequest jeReq, boolean isUpdateOnly)
            throws JEConflictException, JEAccessDeniedException, JENotFoundException {

        // try to find an existing JEDoc for the docId
        final Transaction tx = Datastore.beginTransaction();
        JEDoc jeDoc = null;
        if (jeReq.getDocId() != null) {
            try {
                jeDoc = getJEDoc(tx, jeReq);
            } catch (JENotFoundException e) {
                // not found
            }
        }

        // check if accessible
        final String createdBy = jeDoc != null ? jeDoc.getCreatedBy() : null;
        if (!jeReq.isAccessible(createdBy, false)) {
            throw new JEAccessDeniedException();
        }

        // if there's an existing doc, merge the old values with the updated
        // values (partial update)
        if (jeDoc != null) {
            jeDoc.getDocValues().putAll(jeReq.getJsonMap());
            jeReq.getJsonMap().putAll(jeDoc.getDocValues());
        }

        // if there's no existing doc for the docId...
        if (jeDoc == null) {
            if (isUpdateOnly) {
                // if isUpdateOnly, throw an exception
                throw new JENotFoundException("No such doc: "
                    + jeReq.getDocId());
            } else {
                // otherwise, create it
                jeDoc = JEDoc.createJEDoc(jeReq);
            }
        }

        // update properties (build index)
        jeDoc.update(jeReq);

        // save JEDoc
        try {
            Datastore.put(tx, jeDoc);
            Datastore.commit(tx);
        } catch (ConcurrentModificationException e) {
            throw new JEConflictException(e);
        }

        // return the saved JSON document
        return JSON.encode(jeDoc.getDocValues());
    }
}
