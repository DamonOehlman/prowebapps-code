package com.jsonengine.controller.task;

import java.util.List;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.jsonengine.meta.JEDocMeta;

public class DeleteDocTypeTaskController extends Controller {

    private static final Logger logger =
        Logger.getLogger(DeleteDocTypeTaskController.class.getName());

    private static final String QUENAME_JETASKS = "jetasks";

    private static final String PARAM_DOCTYPE = "docType";

    @Override
    public Navigation run() throws Exception {
        logger.info("Call TQController#run");

        // find 500 entities for the docType
        final String docType = asString(PARAM_DOCTYPE);
        final JEDocMeta jdm = JEDocMeta.get();
        final List<Key> keys =
            Datastore
                .query(jdm)
                .filter(jdm.docType.equal(docType))
                .limit(500)
                .asKeyList();

        // if there's no entities for the docType, finish the task
        if (keys.isEmpty()) {
            return null;
        }

        // delete them all
        Datastore.delete(keys);

        // put another task for deletion
        addDeleteAllTask(docType);

        return null;
    }

    /**
     * Adds a task on the queue to delete all the entities for a docType.
     *
     * @param docType
     *            docType to delete
     */
    public static void addDeleteAllTask(String docType) {
        final Queue que = QueueFactory.getQueue(QUENAME_JETASKS);
        final TaskOptions to =
            TaskOptions.Builder.url("/task/deleteDocTypeTask").param(
                PARAM_DOCTYPE,
                docType);
        que.add(to);
    }
}