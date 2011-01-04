package com.jsonengine.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slim3.memcache.Memcache;
import org.slim3.util.RequestLocator;

/**
 * Provides utility methods for jsonengine.
 * 
 * @author kazunori_279
 */
public class JEUtils {

    public static final String ALNUMS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String MC_KEY_TIMESTAMP =
        "com.jsonengine.common.LogCounterService#timestamp";

    public static final int UUID_DIGITS = 32;

    public static final String YAHOO_APPID_JA =
        "QZWK7SGxg67FGZpOHgk2rMkwNL5EMOXhnNXqEDKpk32FwzA8PFcgFirTdE6zXJDnKtnp";

    public static final String YAHOO_PARSE_JA =
        "http://jlp.yahooapis.jp/MAService/V1/parse?filter=1|3|5|6|7|8|9|10&response=surface,reading&appid="
            + YAHOO_APPID_JA
            + "&sentence=";

    private static Pattern termPattern =
        Pattern.compile("<(surface|reading)>([^<]*)</(surface|reading)>");

    /**
     * Converts specified {@link BigDecimal} value to a String which can be
     * sorted by lexical order. It would be useful for building an index table
     * on Datastore. Currently it does not support negative values.
     * 
     * Thanks so much to @ashigeru who advised me how to implement this method.
     * 
     * TODO to support negative value.
     * 
     * @condParam bd a {@link BigDecimal} positive value
     * @return {@link String} key for building a index table for the value
     */
    public String convertBigDecimalToIndexKey(BigDecimal bd) {

        // check if it's positive value
        assert bd.signum() != -1;

        // normalize the value to make it less than 1
        int scaleOffset = 0;
        while (bd.longValue() >= 1) {
            bd = bd.movePointLeft(1);
            scaleOffset++;
        }

        // convert it to String
        final String scalePrefix = String.format("%02d", scaleOffset);
        return scalePrefix + ":" + bd.toPlainString();
    }

    /**
     * Encodes a property value of JSON doc into a String for filtering or
     * sorting. The value can be a String, Boolean or BigDecimal.
     * 
     * @param val
     *            the value to be encoded
     * @return encoded String
     */
    public String encodePropValue(Object val) {
        if (val == null) {
            return "";
        } else if (val instanceof String) {
            return (String) val;
        } else if (val instanceof Boolean) {
            return val.toString();
        } else if (val instanceof BigDecimal) {
            return (new JEUtils())
                .convertBigDecimalToIndexKey((BigDecimal) val);
        } else {
            // try to convert the value to BigDecimal
            try {
                return (new JEUtils())
                    .convertBigDecimalToIndexKey(new BigDecimal(val.toString()));
            } catch (Exception e) {
                // failed
            }
            return val.toString();
        }
    }

    /**
     * Generates a String with random characteres made of alpha numerics.
     * 
     * @condParam digits
     * @return random alnum String
     */
    public String generateRandomAlnums(int digits) {
        final StringBuilder sb = new StringBuilder();
        while (sb.length() < digits) {
            sb.append(ALNUMS.charAt((int) (Math.random() * ALNUMS.length())));
        }
        return sb.toString();
    }

    /**
     * Generates an UUID.
     * 
     * @return an UUID
     */
    public String generateUUID() {
        return generateRandomAlnums(UUID_DIGITS);
    }

    /**
     * Returns a global time stamp. The time stamp value is based on
     * {@link System#currentTimeMillis()}, but is assured to be the largest and
     * unique value in the application which may be served by several App
     * Servers. But please note it uses the Memcache service to assure the
     * uniqueness, and it would just return the
     * {@link System#currentTimeMillis()} as is when the Memcache value is
     * expired or lost.
     * 
     * TODO make this atomic
     * 
     * @return a global time stamp value
     */
    public long getGlobalTimestamp() {
        long timestamp = System.currentTimeMillis();
        Long lastTimestamp = (Long) Memcache.get(MC_KEY_TIMESTAMP);
        if (lastTimestamp != null && lastTimestamp >= timestamp) {
            timestamp = lastTimestamp + 1;
        }
        Memcache.put(MC_KEY_TIMESTAMP, timestamp);
        return timestamp;
    }

    /**
     * Extract terms from the text by using Yahoo's term extraction web service.
     * 
     * @param text
     * @return a Set of extracted terms
     */
    public Set<String> extractTerms(String text) {
        final Set<String> propValues = new HashSet<String>();
        final String result = callURL(YAHOO_PARSE_JA + text);
        final Matcher m = termPattern.matcher(result);
        while (m.find()) {
            propValues.add(m.group(2));
        }
        return propValues;
    }

    /**
     * Calls the specified URL and returns the response text.
     * 
     * @param url
     * @return String of the response
     */
    public String callURL(String url) {
        final StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(new URL(url)
                    .openStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getRequestServer() {
        final HttpServletRequest request = RequestLocator.get();
        StringBuilder sb =
            new StringBuilder(request.getScheme()).append("://").append(request.getServerName());
        if ((request.getScheme().equalsIgnoreCase("http") && request.getServerPort() != 80)
            || (request.getScheme().equalsIgnoreCase("https") && request.getServerPort() != 443)) {
            sb.append(":").append(request.getServerPort());
        }
        return sb.toString();
    }
}
