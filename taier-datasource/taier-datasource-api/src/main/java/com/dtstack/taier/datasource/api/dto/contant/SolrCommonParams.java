/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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


