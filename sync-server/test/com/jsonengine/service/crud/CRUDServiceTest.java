package com.jsonengine.service.crud;

import static org.junit.Assert.*;

import java.util.Map;

import net.arnx.jsonic.JSON;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;
import com.jsonengine.model.JEDoc;

/**
 * Tests CRUD operations of {@link CRUDService}.
 * 
 * @author @kazunori_279
 */
// public class CRUDServiceTest extends TestCase {
public class CRUDServiceTest extends AppEngineTestCase {

    final JETestUtils jtu = new JETestUtils();

    @SuppressWarnings("unchecked")
    @Test
    public void testCRUD() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // save a test data
        jtu.storeTestDocTypeInfo();
        final Map<String, Object> testMap = jtu.createTestMap();
        final CRUDRequest jeReq = jtu.createTestCRUDRequest(testMap);
        final String savedJson = (new CRUDService()).put(jeReq, false);
        final String docId =
            (String) ((Map<String, Object>) JSON.decode(savedJson))
                .get(JEDoc.PROP_NAME_DOCID);

        // get the stored test data
        jeReq.setDocId(docId);
        final String resultJson = (new CRUDService()).get(jeReq);
        System.out.println(resultJson);

        // verify it
        final Map<String, Object> resultMap =
            (Map<String, Object>) JSON.decode(resultJson);
        final Long updatedAt = jtu.getUpdatedAtFromTestMap(resultMap);
        assertNotNull("_updatedAt must exists", updatedAt);
        assertEquals(docId, resultMap.remove(JEDoc.PROP_NAME_DOCID));
        assertTrue(jtu.areMapsEqual(testMap, resultMap));

        // update the test data
        testMap.put("001", "foo2");
        testMap.put("004", "hoge");
        testMap.remove("002");
        final CRUDRequest jeReq2 = jtu.createTestCRUDRequest(testMap);
        jeReq2.setCheckUpdatesAfter(updatedAt);
        jeReq2.setDocId(docId);
        (new CRUDService()).put(jeReq2, false);
        
        // get it again
        jeReq2.setCheckUpdatesAfter(null);
        final String resultJson2 = (new CRUDService()).get(jeReq2);
        System.out.println(resultJson2);

        // verify it
        final Map<String, Object> resultMap2 =
            (Map<String, Object>) JSON.decode(resultJson2);
        assertNotNull("_updatedAt must exists", resultMap2
            .remove(JEDoc.PROP_NAME_UPDATED_AT));
        assertEquals(docId, resultMap2.remove(JEDoc.PROP_NAME_DOCID));
        assertTrue(jtu.areMapsEqual(testMap, resultMap2));

        // try saving the old data again and check if the conflict detection is
        // working
        jeReq.setCheckUpdatesAfter(updatedAt);
        try {
            (new CRUDService()).put(jeReq, false);
            fail("Should throw a JEConflictionException");
        } catch (JEConflictException e) {
            // OK
        }

        // try saving again without the conflict detection
        (new CRUDService()).put(jeReq2, false);

        // try removing the data and check if the conflict detection is
        // working
        try {
            (new CRUDService()).delete(jeReq);
            fail("Should throw a JEConflictionException");
        } catch (JEConflictException e) {
            // OK
        }

        // try removing the data again without the conflict detection
        (new CRUDService()).delete(jeReq2);

        // make sure the data is removed
        try {
            (new CRUDService()).get(jeReq2);
            fail("Should throw a JENotFoundException");
        } catch (JENotFoundException e) {
            // OK
        }
    }

    // @SuppressWarnings("unchecked")
    @Test
    public void testDeleteDocType() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // save test data
        (new JETestUtils()).storeTestDocTypeInfo();
        (new JETestUtils()).storeTestUsers(JETestUtils.TEST_DOCTYPE);

        // delete all
        final CRUDRequest cr = (new JETestUtils()).createTestCRUDRequest();
        (new CRUDService()).delete(cr);

    }

}
