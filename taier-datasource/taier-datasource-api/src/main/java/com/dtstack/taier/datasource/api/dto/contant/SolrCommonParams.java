package com.dtstack.taier.datasource.api.dto.contant;


/**
 * Parameters used across many handlers
 */
public interface SolrCommonParams {

    /**
     * the Request Handler (formerly known as the Query Type) - which Request Handler should handle the request
     */
    String QT = "qt";

    /**
     * query string
     */
    String Q = "q";


    /**
     * sort order
     */
    String SORT = "sort";

    /**
     * Lucene query string(s) for filtering the results without affecting scoring
     */
    String FQ = "fq";

    /**
     * zero based offset of matching documents to retrieve
     */
    String START = "start";

    /**
     * number of documents to return starting at "start"
     */
    String ROWS = "rows";

    /**
     * query and init param for field list
     */
    String FL = "fl";

    /**
     * default query field
     */
    String DF = "df";

    /**
     * whether to include debug data for all components pieces, including doing explains
     */
    String DEBUG_QUERY = "debugQuery";
}


