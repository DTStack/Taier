package com.dtstack.taier.datasource.plugin.solr;

public class SolrConsistent {
    /**
     * solr JAAS 内容
     */
    public static String SOLR_JAAS_CONTENT = "SolrJClient {\n" +
            "    com.sun.security.auth.module.Krb5LoginModule required\n" +
            "    useKeyTab=true\n" +
            "    keyTab=\"%s\"\n" +
            "    storeKey=true\n" +
            "    useTicketCache=true\n" +
            "    debug=true\n" +
            "    principal=\"%s\";\n" +
            "};";
}