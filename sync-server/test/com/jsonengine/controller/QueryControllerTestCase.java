package com.jsonengine.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.arnx.jsonic.JSON;

import org.junit.Before;
import org.junit.Test;
import org.slim3.tester.ControllerTestCase;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JETestUtils;

/**
 * Tests {@link QueryController}.
 * 
 * @author @kazunori_279
 */
public class QueryControllerTestCase extends ControllerTestCase {

    private static final String TEST_PREFIX_PATH =
        "/_je/" + JETestUtils.TEST_DOCTYPE;
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
            JENotFoundException, JEAccessDeniedException, NullPointerException,
            IllegalArgumentException, IOException, ServletException {

        // execute query
        tester.request.setMethod("get");
        tester.start(TEST_PREFIX_PATH);

        // validate the result
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(4, resultMaps.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryFirstTwoUsers() throws JEConflictException,
            JENotFoundException, JEAccessDeniedException, NullPointerException,
            IllegalArgumentException, IOException, ServletException {

        // execute query
        tester.request.setMethod("get");
        tester.param("limit", "2");
        tester.start(TEST_PREFIX_PATH);

        // validate the result size
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);
        assertEquals(2, resultMaps.size());
    }

    @Test
    public void queryWithEQ() throws Exception {

        // who's weight is equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "eq", 123.45);

        // compare the result with Betty
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getDaniel(), resultMaps.get(0)));
    }

    @Test
    public void queryWithLT() throws Exception {

        // who's weight is less than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "lt", 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getAmanda(), resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getMarc(), resultMaps.get(1)));
    }

    @Test
    public void queryWithLE() throws Exception {

        // who's weight is less than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "le", 123.45);

        // validate the result
        assertEquals(3, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getAmanda(), resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getMarc(), resultMaps.get(1)));
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getDaniel(), resultMaps.get(2)));
    }

    @Test
    public void queryWithGT() throws Exception {

        // who's weight is greater than 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "gt", 123.45);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getBetty(), resultMaps.get(0)));
    }

    @Test
    public void queryWithGE() throws Exception {

        // who's weight is greater than or equal to 123.45?
        final List<Map<String, Object>> resultMaps =
            queryOnAProp("weight", "ge", 123.45);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getDaniel(), resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getBetty(), resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithGEAndLE() throws Exception {

        // execute query with a condition
        tester.request.setMethod("get");
        tester.paramValues("cond", new String[] {
            "weight.ge.12.345",
            "weight.le.123.45" });
        tester.start(TEST_PREFIX_PATH);

        // execute query
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // validate the result
        assertEquals(2, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getMarc(), resultMaps.get(0)));
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getDaniel(), resultMaps.get(1)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWith3EQs() throws Exception {

        // execute query with a condition
        tester.request.setMethod("get");
        tester.paramValues("cond", new String[] {
            "weight.eq.123.45",
            "isMale.eq.true",
            "id.eq.\"002\"" });
        tester.start(TEST_PREFIX_PATH);

        // execute query
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // validate the result
        assertEquals(1, resultMaps.size());
        assertTrue((new JETestUtils()).areMapsEqual((new JETestUtils())
            .getDaniel(), resultMaps.get(0)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortAsc() throws Exception {

        // execute query with a condition
        tester.request.setMethod("get");
        tester.param("sort", "id.asc");
        tester.start(TEST_PREFIX_PATH);

        // execute query
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // validate the result
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void queryWithSortDesc() throws Exception {

        // execute query with a condition
        tester.request.setMethod("get");
        tester.param("sort", "weight.desc");
        tester.start(TEST_PREFIX_PATH);

        // execute query
        final String resultJson = tester.response.getOutputAsString();
        final List<Map<String, Object>> resultMaps =
            (List<Map<String, Object>>) JSON.decode(resultJson);

        // validate the result
        assertEquals(4, resultMaps.size());
        assertEquals("001", resultMaps.get(0).get("id"));
        assertEquals("002", resultMaps.get(1).get("id"));
        assertEquals("003", resultMaps.get(2).get("id"));
        assertEquals("004", resultMaps.get(3).get("id"));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryOnAProp(String propName, String cp,
            Object propValue) throws Exception {

        // execute query with a condition
        tester.request.setMethod("get");
        tester.param("cond", propName + "." + cp + "." + propValue);
        tester.start(TEST_PREFIX_PATH);

        // execute query
        final String resultJson = tester.response.getOutputAsString();
        log.info("Result: " + resultJson);
        return (List<Map<String, Object>>) JSON.decode(resultJson);
    }
}
