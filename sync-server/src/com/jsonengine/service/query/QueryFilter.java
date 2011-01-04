package com.jsonengine.service.query;

import java.util.Set;

import org.slim3.datastore.StringCollectionAttributeMeta;

import com.jsonengine.common.JEUtils;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

/**
 * Represents a queryFilter.
 * 
 * @author @kazunori_279
 */
public abstract class QueryFilter {

    /**
     * Represents a EQ confFilter.
     */
    public static class EQ extends QueryFilter {
        private EQ(QueryRequest qReq, String propName, Object propValue) {
            super(qReq, propName, propValue);
            queryRequest.getEqCriteria().add(index.equal(condParam));
        }
    }

    /**
     * Represents a GE confFilter.
     */
    public static class GE extends QueryFilter {
        private GE(QueryRequest queryRequest, String propName, Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(index.lessThan(condMax), false);
            queryRequest.setGtOrGeCriterion(
                index.greaterThanOrEqual(condParam),
                true);
        }
    }

    /**
     * Represents a GT confFilter.
     */
    public static class GT extends QueryFilter {
        private GT(QueryRequest queryRequest, String propName, Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(index.lessThan(condMax), false);
            queryRequest.setGtOrGeCriterion(index.greaterThan(condParam), true);
        }
    }

    /**
     * Represents a LE confFilter.
     */
    public static class LE extends QueryFilter {
        private LE(QueryRequest queryRequest, String propName, Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(
                index.lessThanOrEqual(condParam),
                true);
            queryRequest.setGtOrGeCriterion(index.greaterThan(condMin), false);
        }
    }

    /**
     * Represents a limitFilter.
     */
    public static class LIMIT extends QueryFilter {
        private LIMIT(QueryRequest queryRequest, int limitCount) {
            super(queryRequest, null, null);
            queryRequest.setLimitCount(limitCount);
        }
    }

    /**
     * Represents a LT confFilter.
     */
    public static class LT extends QueryFilter {
        private LT(QueryRequest queryRequest, String propName, Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(index.lessThan(condParam), true);
            queryRequest.setGtOrGeCriterion(index.greaterThan(condMin), false);
        }
    }

    /**
     * Represents a sortFilter for asc.
     */
    public static class SortASC extends QueryFilter {
        private SortASC(QueryRequest queryRequest, String propName,
                Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(index.lessThan(condMax), false);
            queryRequest.setGtOrGeCriterion(index.greaterThan(condMin), false);
            queryRequest.setSortCriterion(index.asc);
        }
    }

    /**
     * Represents a sortFilter for desc.
     */
    public static class SortDESC extends QueryFilter {
        private SortDESC(QueryRequest queryRequest, String propName,
                Object propValue) {
            super(queryRequest, propName, propValue);
            queryRequest.setLtOrLeCriterion(index.lessThan(condMax), false);
            queryRequest.setGtOrGeCriterion(index.greaterThan(condMin), false);
            queryRequest.setSortCriterion(index.desc);
        }
    }

    /**
     * parses a token String and convert it to a Comparator.
     * 
     * @param token
     * @return a Comparator
     */
    public static void addCondFilter(QueryRequest qReq, String propName,
            String token, Object propValue) {
        if ("lt".equals(token)) {
            new LT(qReq, propName, propValue);
        } else if ("le".equals(token)) {
            new LE(qReq, propName, propValue);
        } else if ("gt".equals(token)) {
            new GT(qReq, propName, propValue);
        } else if ("ge".equals(token)) {
            new GE(qReq, propName, propValue);
        } else if ("eq".equals(token)) {
            new EQ(qReq, propName, propValue);
        } else {
            throw new IllegalArgumentException(
                "Illegal comparator for a confFilter: " + token);
        }
    }

    /**
     * Add limitFilter to the QueryRequest.
     * 
     * @param qReq
     * @param limitCount
     */
    public static void addLimitFilter(QueryRequest qReq, int limitCount) {
        new LIMIT(qReq, limitCount);
    }

    /**
     * Parses a token String and convert it to a SortOrder;
     * 
     * @param token
     * 
     * @return a SortOrder
     */
    public static void addSortFilter(QueryRequest qReq, String propName,
            String token) {
        if ("desc".equals(token)) {
            new SortDESC(qReq, propName, null);
        } else if ("asc".equals(token)) {
            new SortASC(qReq, propName, null);
        } else {
            throw new IllegalArgumentException(
                "Illegal sortOrder for a sortFilter: " + token);
        }
    }

    // the upper limit value for the property
    protected final String condMax;

    // the lower limit value for the property
    protected final String condMin;

    // user specified value for the filtering
    protected final String condParam;

    // indexEntries meta for the filterling
    protected final StringCollectionAttributeMeta<JEDoc, Set<String>> index =
        JEDocMeta.get().indexEntries;

    // docType for the filter
    protected final QueryRequest queryRequest;

    /**
     * Creates a confFilter.
     * 
     * @param queryRequest
     *            queryRequest of this filter
     * @param propName
     *            property name for the filtering
     * @param propValue
     *            property value for the filtering
     */
    public QueryFilter(QueryRequest queryRequest, String propName,
            Object propValue) {
        this.queryRequest = queryRequest;
        this.queryRequest.addQueryFilter(this);
        final String condPrefix =
            queryRequest.getDocType() + ":" + propName + ":";
        this.condParam = condPrefix + (new JEUtils()).encodePropValue(propValue);
        this.condMin = condPrefix;
        this.condMax = condPrefix + "\uffff";
    }

    @Override
    public String toString() {
        return "QueryFilter(docType="
            + queryRequest.getDocType()
            + ", cp="
            + this.getClass().getSimpleName()
            + ", condParam="
            + condParam
            + ", condMin="
            + condMin
            + ", condMax="
            + condMax
            + ") ";
    }
}