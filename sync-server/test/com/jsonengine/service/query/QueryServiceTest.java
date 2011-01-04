package com.jsonengine.service.query;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;

import org.junit.Before;
import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;
import com.jsonengine.controller.QueryControllerTestCase;

/**
 * Tests query operations of {@link QueryService}.
 * 
 * @deprecated This class have been moved to {@link QueryControllerTestCase}.
 * @author kazunori_279
 */
public class QueryServiceTest extends AppEngineTestCase {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Before
    public void before() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // setup test docTypeInfo and test users
        (new JETestUtils()).storeTestDocTypeInfo();
        (new JETestUtils()).storeTestUsers(JETestUtils.TEST_DOCTYPE);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryAllUsers() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // find all users
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        final String resultJson = (new QueryService()).query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryFirstTwoUsers() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // find all users with a limit
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addLimitFilter(qr, 2);
        final String resultJson = (new QueryService()).query(qr);

        // validate the result size
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(2, resultMaps.size());
    }

    @Test
    public void queryWithEQ() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "eq", 123.45);

        // compare the result with Betty
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getDaniel(),
            resultMaps.get(0)));
    }

    @Test
    public void queryWithLT() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is less than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "lt", 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getAmanda(),
            resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getMarc(),
            resultMaps.get(1)));
    }

    @Test
    public void queryWithLE() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is less than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "le", 123.45);

        // validate the result
        assertEquals(3, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getAmanda(),
            resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getMarc(),
            resultMaps.get(1)));
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getDaniel(),
            resultMaps.get(2)));
    }

    @Test
    public void queryWithGT() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is greater than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "gt", 123.45);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getBetty(),
            resultMaps.get(0)));
    }

    @Test
    public void queryWithGE() throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // who's weight is greater than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "ge", 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getDaniel(),
            resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getBetty(),
            resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithGEAndLE() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // build query filters
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addCondFilter(qr, "weight", "ge", 12.345);
        QueryFilter.addCondFilter(qr, "weight", "le", 123.45);

        // execute query
        final String resultJson = (new QueryService()).query(qr);
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        log.info("Result: " + resultJson);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getMarc(),
            resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getDaniel(),
            resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWith3EQs() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // build query filters
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addCondFilter(qr, "weight", "eq", 123.45);
        QueryFilter.addCondFilter(qr, "isMale", "eq", true);
        QueryFilter.addCondFilter(qr, "id", "eq", "002");

        // execute query
        final String resultJson = (new QueryService()).query(qr);
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        log.info("Result: " + resultJson);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual(
            (new JETestUtils()).getDaniel(),
            resultMaps.get(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortAsc() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // find all users
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addSortFilter(qr, "id", "asc");

        // execute query
        final String resultJson = (new QueryService()).query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortDesc() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // find all users
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addSortFilter(qr, "weight", "desc");

        // execute query
        final String resultJson = (new QueryService()).query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryOnAProp(String propName, String cp,
            Object propValue) throws JEConflictException, JENotFoundException,
            JEAccessDeniedException {

        // build query filters
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE);
        QueryFilter.addCondFilter(qr, propName, cp, propValue);
        log.info("Query: " + qr);

        // execute query
        final String resultJson = (new QueryService()).query(qr);
        log.info("Result: " + resultJson);
        return (List<Map<String, Object>>) JSON.decode(resultJson);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryOnEmptyDocType() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException {

        // find all users
        final QueryRequest qr =
            (new JETestUtils()).createTestQueryRequest(JETestUtils.TEST_DOCTYPE2);
        qr.setAdmin(true);
        final String resultJson = (new QueryService()).query(qr);

        // validate the result
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(0, resultMaps.size());
    }

}
