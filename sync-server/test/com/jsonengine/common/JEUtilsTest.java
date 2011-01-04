package com.jsonengine.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;


//public class JEUtilsTest extends TestCase {
public class JEUtilsTest extends AppEngineTestCase {

    @Test
    public void testConvertBigDecimalToIndexKey() {

        // 0.1 > 0.01
        final String key0_1 =
            (new JEUtils()).convertBigDecimalToIndexKey(new BigDecimal(0.1));
        final String key0_01 =
            (new JEUtils()).convertBigDecimalToIndexKey(new BigDecimal(0.01));
        assertTrue("comparing: " + key0_1 + ", " + key0_01, key0_1
            .compareTo(key0_01) >= 1);

        // 100 > 1
        final String key100 =
            (new JEUtils()).convertBigDecimalToIndexKey(new BigDecimal(100));
        final String key1 =
            (new JEUtils()).convertBigDecimalToIndexKey(new BigDecimal(1));
        assertTrue(
            "comparing: " + key100 + ", " + key1,
            key100.compareTo(key1) >= 1);

        // build TreeMaps for random value test
        final TreeMap<BigDecimal, Integer> bdMap =
            new TreeMap<BigDecimal, Integer>();
        final TreeMap<String, Integer> strMap = new TreeMap<String, Integer>();
        for (int i = 0; i < 10000; i++) {
            final int randScale = (int) (Math.random() * 10) - 5;
            final BigDecimal randBd =
                new BigDecimal(Math.random() * Math.pow(10, randScale));
            bdMap.put(randBd, i);
            strMap.put((new JEUtils()).convertBigDecimalToIndexKey(randBd), i);
        }

        // check the TreeMaps
        while (!bdMap.isEmpty()) {
            final Entry<BigDecimal, Integer> bdEnt = bdMap.firstEntry();
            final Entry<String, Integer> strEnt = strMap.firstEntry();
            // System.out.println(bdEnt.getValue()
            // + "="
            // + bdEnt.getKey()
            // + ", "
            // + strEnt.getValue()
            // + "="
            // + strEnt.getKey());
            assertEquals(bdEnt.getValue(), strEnt.getValue());
            bdMap.remove(bdEnt.getKey());
            strMap.remove(strEnt.getKey());
        }
    }
    
    @Test
    public void testGenerateUUID() {
        final Set<String> uuids = new HashSet<String>();
        for (int i = 0; i < 100000; i++) {
            final String uuid = (new JEUtils()).generateUUID();
            assertFalse("UUID should be unique", uuids.contains(uuid));
            assertTrue("UUID should be made of alnums", uuid.matches("[a-zA-Z0-9]{32}"));
            uuids.add(uuid);
        }
        
    }
}
