package com.jsonengine.service.query;

import java.util.LinkedList;
import java.util.List;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.SortCriterion;

import com.jsonengine.common.JERequest;
import com.jsonengine.meta.JEDocMeta;
import com.jsonengine.model.JEDoc;

/**
 * Holds various request parameters required for processing jsonengine query
 * operations.
 * 
 * @author @kazunori_279
 */
public class QueryRequest extends JERequest {

    private final List<FilterCriterion> eqCriteria =
        new LinkedList<FilterCriterion>();

    private FilterCriterion gtOrGeCriterion = null;

    private Integer limitCount = null;

    private FilterCriterion ltOrLeCriterion = null;

    private final List<QueryFilter> queryFilters =
        new LinkedList<QueryFilter>();

    private SortCriterion sortCriterion = null;
    
    public void addQueryFilter(QueryFilter qf) {
        queryFilters.add(qf);
    }

    /**
     * Applies all the filters in this request.
     * 
     * @param modelQuery
     *            {@link ModelQuery} to apply the filters.
     * @return {@link ModelQuery}
     */
    public ModelQuery<JEDoc> applyFilters(ModelQuery<JEDoc> modelQuery) {

        // convert QueryFilters to Slim3 filters
        if (gtOrGeCriterion != null) {
            modelQuery = modelQuery.filter(gtOrGeCriterion);
        }
        if (ltOrLeCriterion != null) {
            modelQuery = modelQuery.filter(ltOrLeCriterion);
        }
        for (FilterCriterion eqCriterion : eqCriteria) {
            modelQuery = modelQuery.filter(eqCriterion);
        }
        if (sortCriterion != null) {
            modelQuery = modelQuery.sort(sortCriterion);
        }
        if (limitCount != null) {
            modelQuery = modelQuery.limit(limitCount);
        }

        // if there's no condFilters, add a filter for a docType
        if (gtOrGeCriterion == null && ltOrLeCriterion == null) {
            modelQuery =
                modelQuery.filter(JEDocMeta.get().docType.equal(getDocType()));
        }
        return modelQuery;
    }

    public List<FilterCriterion> getEqCriteria() {
        return eqCriteria;
    }

    public FilterCriterion getGtOrGeCriterion() {
        return gtOrGeCriterion;
    }

    public Integer getLimitCount() {
        return limitCount;
    }

    public FilterCriterion getLtOrLeCriterion() {
        return ltOrLeCriterion;
    }

    public SortCriterion getSortCriterion() {
        return sortCriterion;
    }

    public void setGtOrGeCriterion(FilterCriterion gtOrGeCriterion,
            boolean isOverwrite) {
        if (this.gtOrGeCriterion == null || isOverwrite) {
            this.gtOrGeCriterion = gtOrGeCriterion;
        }
    }

    public void setLimitCount(Integer limitCount) {
        this.limitCount = limitCount;
    }

    public void setLtOrLeCriterion(FilterCriterion ltOrLeCriterion,
            boolean isOverwrite) {
        if (this.ltOrLeCriterion == null || isOverwrite) {
            this.ltOrLeCriterion = ltOrLeCriterion;
        }
    }

    public void setSortCriterion(SortCriterion sortCriterion) {
        this.sortCriterion = sortCriterion;
    }

    @Override
    public String toString() {
        return "QueryRequest(" + queryFilters + ")";
    }
}
