package com.jsonengine.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

import com.jsonengine.common.JEAccessDeniedException;
import com.jsonengine.common.JEConflictException;
import com.jsonengine.common.JENotFoundException;
import com.jsonengine.common.JERequest;
import com.jsonengine.common.JEUserUtils;
import com.jsonengine.common.JEUtils;
import com.jsonengine.service.crud.CRUDRequest;
import com.jsonengine.service.crud.CRUDService;
import com.jsonengine.service.query.QueryFilter;
import com.jsonengine.service.query.QueryRequest;
import com.jsonengine.service.query.QueryService;

/**
 * Handles all incoming requests to jsonengine.
 * 
 * @author @kazunori_279
 */
public class FrontController extends Controller {

    public static final String PARAM_COND = "cond";

    public static final String PARAM_LIMIT = "limit";

    public static final String PARAM_CHECK_UPDATES_AFTER = "_checkUpdatesAfter";

    public static final String PARAM_DOC = "_doc";

    public static final String PARAM_DOCID = "_docId";

    public static final String PARAM_DOC_TYPE = "_docType";

    public static final String PARAM_SORT = "sort";

    public static final String PARAM_METHOD = "_method";

    public static final String PARAM_METHOD_DELETE = "delete";

    public static final String PARAM_METHOD_PUT = "put";

    public static final String RESP_CONTENT_TYPE =
        "application/json; charset=UTF-8";

    private static final Pattern condPattern =
        Pattern.compile("^([^\\.]*)\\.(eq|gt|ge|lt|le)\\.(.*)$");

    private static final Logger logger =
        Logger.getLogger(FrontController.class.getName());

    private static final Pattern quotePattern =
        Pattern.compile("^[\"'](.*)[\"']$");

    @Override
    public Navigation run() throws Exception {
        logger.info("Call CRUDController#run");

        // delete (DELETE or POST w/ _method=delete)
        if (isDelete()
            || (isPost() && PARAM_METHOD_DELETE.equals(asString(PARAM_METHOD)))) {
            doDelete(request, response);
            return null;
        }

        // update only (PUT or POST w/ _method=update)
        if (isPut()
            || (isPost() && PARAM_METHOD_PUT.equals(asString(PARAM_METHOD)))) {
            doUpdate(request, response, true);
            return null;
        }

        // create or update (POST)
        if (isPost()) {
            doUpdate(request, response, false);
            return null;
        }

        // get or query (GET)
        if (isGet()) {
            if (asString(PARAM_DOCID) == null) {
                doQuery(request, response);
            } else {
                doGet(request, response);
            }
            return null;
        }
        throw new IllegalArgumentException("Unsupported method: "
            + request.getMethod());
    }

    private Object convertPropValue(final String propValue) {

        // if it's quoted, treat it as a String
        final Matcher m = quotePattern.matcher(propValue);
        if (m.find()) {
            return m.group(1);
        }

        // if it's not quoted, try to parse it as a BigDecimal
        try {
            return new BigDecimal(propValue);
        } catch (NumberFormatException e) {
            // failed
        }

        // try to parse as a Boolean
        if ("true".equals(propValue)) {
            return true;
        } else if ("false".equals(propValue)) {
            return false;
        }

        // otherwise, treat it as a String
        return propValue;
    }

    private CRUDRequest createCRUDRequest(HttpServletRequest req)
            throws UnsupportedEncodingException {

        // parse JSON doc
        final String jsonDocParam = asString(PARAM_DOC);
        final CRUDRequest jeReq;
        if (jsonDocParam != null) {
            // JSON style params
            jeReq = new CRUDRequest(jsonDocParam);
        } else {
            // FORM style params
            jeReq = new CRUDRequest(decodeFormStyleParams(req));
        }

        // init the request
        initJERequest(jeReq, req);

        // decode docId
        final String docId = req.getParameter(PARAM_DOCID);
        if (!StringUtil.isEmpty(docId)) {
            jeReq.setDocId(docId);
        }

        // set checkConflict flag
        try {
            jeReq.setCheckUpdatesAfter(Long.parseLong(req
                .getParameter(PARAM_CHECK_UPDATES_AFTER)));
        } catch (Exception e) {
            // NPE or NumberFormatException
        }
        return jeReq;
    }

    @SuppressWarnings("unchecked")
    private String decodeFormStyleParams(HttpServletRequest req) {

        // convert all the parameters into Map
        final Enumeration<String> paramNames = req.getParameterNames();
        final Map<String, Object> jsonMap = new HashMap<String, Object>();
        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            if (!PARAM_DOC_TYPE.equals(paramName)) { // skip _docType param
                final Object paramValue = decodeOneParam(req, paramName);
                jsonMap.put(paramName, paramValue);
            }
        }

        // convert the Map into JSON
        return JSON.encode(jsonMap);
    }

    private Object decodeOneParam(HttpServletRequest req, String paramName) {
        final String[] paramValues = req.getParameterValues(paramName);
        final Object paramValue;
        if (paramValues.length == 1 || PARAM_DOCID.equals(paramName)) {
            // if there's only one param value or paramName is _docId, use it
            paramValue = decodeOneParamValue(paramValues[0]);
        } else {
            // if there're multiple param values, put them into a List
            final List<Object> ls = new LinkedList<Object>();
            for (String s : paramValues) {
                ls.add(decodeOneParamValue(s));
            }
            paramValue = ls;
        }
        return paramValue;
    }

    private Object decodeOneParamValue(String valueStr) {

        // try to decode it as a Long
        try {
            return Long.parseLong(valueStr);
        } catch (NumberFormatException e) {
            // if failed, try next
        }

        // try to decode it as a Double
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            // if failed, try next
        }

        // try to decode it as a Boolean
        if ("true".equals(valueStr)) {
            return Boolean.TRUE;
        } else if ("false".equals(valueStr)) {
            return Boolean.FALSE;
        }

        // use the value as is
        return valueStr;
    }

    private void initJERequest(JERequest jeReq, HttpServletRequest req) {

        // decode docType
        String docType = asString(PARAM_DOC_TYPE);
        if (StringUtil.isEmpty(docType)) {
            throw new IllegalArgumentException("No docType found");
        }
        jeReq.setDocType(docType);

        // set timestamp
        jeReq.setRequestedAt((new JEUtils()).getGlobalTimestamp());

        // set Google account info
        if (JEUserUtils.isLoggedIn()) {
            jeReq.setRequestedBy(JEUserUtils.userEmail());
            jeReq.setAdmin(JEUserUtils.isAdmin());
        }

        // set display name
        String displayName = JEUserUtils.getDisplayName();
        jeReq.setDisplayName(displayName);
    }

    private void parseCondFilter(final QueryRequest qReq, final String cond) {

        // try to parse the cond params
        final Matcher m = condPattern.matcher(cond);
        if (!m.find()) {
            throw new IllegalArgumentException("Illegal condFilter: " + cond);
        }
        final String propName = m.group(1);
        final String condToken = m.group(2);
        final String propValue = m.group(3);

        // try to convert propValue
        final Object propValueObj = convertPropValue(propValue);

        // if propName ends with "_", extract terms from propValue;
        if (propName.endsWith("_")) {
            final Set<String> values = (new JEUtils()).extractTerms(propValue);
            for (String value : values) {
                QueryFilter.addCondFilter(qReq, propName, condToken, value);
            }
        } else {
            QueryFilter.addCondFilter(qReq, propName, condToken, propValueObj);
        }
    }

    private void parseLimitFilter(final QueryRequest qReq,
            final String limitParam) {
        final int limit = Integer.parseInt(limitParam);
        QueryFilter.addLimitFilter(qReq, limit);
    }

    private void parseSortFilter(final QueryRequest qReq, final String sortParam) {
        final String[] sortTokens = sortParam.split("\\.");
        final String propName = sortTokens[0];
        final String sortOrder = sortTokens[1];
        QueryFilter.addSortFilter(qReq, propName, sortOrder);
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // do delete
        final CRUDRequest jeReq = createCRUDRequest(req);
        try {
            (new CRUDService()).delete(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            if (JEUserUtils.isLoggedIn() == false) {
                jsonRedirectToLogin();
                return;
            }
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
    }

    protected void doUpdate(HttpServletRequest req, HttpServletResponse resp,
            boolean isUpdateOnly) throws IOException {

        // do put
        final CRUDRequest jeReq = createCRUDRequest(req);
        final String resultJson;
        try {
            resultJson = (new CRUDService()).put(jeReq, isUpdateOnly);
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            if (JEUserUtils.isLoggedIn() == false) {
                jsonRedirectToLogin();
                return;
            }
            if (jeReq.getDisplayName() == null) {
                jsonRedirectToDisplayName();
                return;
            }
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // return the result
        resp.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // do get
        final CRUDRequest jeReq = createCRUDRequest(req);
        final String resultJson;
        try {
            resultJson = (new CRUDService()).get(jeReq);
        } catch (JENotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (JEConflictException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        } catch (JEAccessDeniedException e) {
            if (JEUserUtils.isLoggedIn() == false) {
                jsonRedirectToLogin();
                return;
            }
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // return the result
        resp.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    protected void doQuery(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // add QueryFilters for "cond" parameters
        final QueryRequest qReq = new QueryRequest();
        initJERequest(qReq, req);

        // add QueryFilters for "cond"
        final String[] conds = req.getParameterValues(PARAM_COND);
        if (conds != null) {
            for (String cond : conds) {
                parseCondFilter(qReq, cond);
            }
        }

        // add QueryFilters for "sort"
        final String sortParam = asString(PARAM_SORT);
        if (sortParam != null) {
            parseSortFilter(qReq, sortParam);
        }

        // add QueryFilters for "limit"
        final String limitParam = asString(PARAM_LIMIT);
        if (limitParam != null) {
            parseLimitFilter(qReq, limitParam);
        }

        // execute the query
        final String resultJson;
        try {
            resultJson = (new QueryService()).query(qReq);
        } catch (JEAccessDeniedException e) {
            if (JEUserUtils.isLoggedIn() == false) {
                jsonRedirectToLogin();
                return;
            }
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // return the result
        resp.setContentType(FrontController.RESP_CONTENT_TYPE);
        final PrintWriter pw = resp.getWriter();
        pw.append(resultJson);
        pw.close();
    }

    /**
     * redirect to loginURL.
     * 
     * @throws IOException
     */
    protected void jsonRedirectToLogin() throws IOException {
        final String redirectURL =
            JEUserUtils.getLoginURL("/user/index").toString();
        jsonRedirect(redirectURL);
    }

    /**
     * redirect to Setting DisplayName URL.
     * 
     * @throws IOException
     */
    protected void jsonRedirectToDisplayName() throws IOException {
        final String redirectURL =
            JEUtils.getRequestServer() + "/user/displayName";
        jsonRedirect(redirectURL);
    }

    /**
     * return the special json, instead of status code 302.
     * 
     * @throws IOException
     */
    protected void jsonRedirect(String redirectURL) throws IOException {
        final String resultJson = "{ \"redirect\": \"" + redirectURL + "\"}";
        response.setContentType(RESP_CONTENT_TYPE);
        final PrintWriter pw = response.getWriter();
        pw.append(resultJson);
        pw.close();
    }
}