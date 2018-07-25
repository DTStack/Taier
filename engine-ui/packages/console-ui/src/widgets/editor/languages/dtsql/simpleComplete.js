import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';

function dtsqlWords() {
    return {
        builtinFunctions: ["DATEADD", "DATEDIFF", "DATEPART", "DATETRUNC", "FROM_UNIXTIME", "GETDATE", "ISDATE", "LASTDAY", "TO_DATE", "TO_CHAR", "UNIX_TIMESTAMP", "WEEKDAY", "WEEKOFYEAR", "ABS", "ACOS", "ASIN", "ATAN", "CEIL", "CONV", "COS", "COSH", "COT", "EXP", "FLOOR", "LN", "LOG", "POW", "RAND", "ROUND", "SIN", "SINH", "SQRT", "TAN", "TANH", "TRUNC", "CHAR_MATCHCOUNT", "CHR", "CONCAT", "GET_JSON_OBJECT", "INSTR", "IS_ENCODING", "IP2REGION", "KEYVALUE", "LENGTH", "LENGTHB", "MD5", "PARSE_URL", "REGEXP_EXTRACT", "REGEXP_INSTR", "REGEXP_REPLACE", "REGEXP_SUBSTR", "REGEXP_COUNT", "SPLIT_PART", "SUBSTR", "TOLOWER", "TOUPPER", "TO_CHAR", "TRIM", "LTRIM", "RTRIM", "URL_ENCODE", "URL_DECODE", "REVERSE", "SPACE", "REPEAT", "ASCII"],
        windowsFunctions: ["COUNT", "AVG", "MAX", "MIN", "MEDIAN", "STDDEV", "STDDEV_SAMP", "SUM", "DENSE_RANK", "RANK", "LAG", "LEAD", "PERCENT_RANK", "ROW_NUMBER", "CLUSTER_SAMPLE"],
        innerFunctions: ["COUNT", "AVG", "MAX", "MIN", "MEDIAN", "STDDEV", "STDDEV_SAMP", "SUM", "WM_CONCAT"],
        otherFunctions: ["ARRAY", "ARRAY_CONTAINS", "IF", "CAST", "COALESCE", "DECODE", "EXPLODE", "GET_IDCARD_AGE", "GET_IDCARD_BIRTHDAY", "GET_IDCARD_SEX", "GREATEST", "INDEX", "MAX_PT", "ORDINAL", "LEAST", "SAMPLE", "SIZE", "SPLIT", "STR_TO_MAP", "TRANS_ARRAY", "TRANS_COLS", "UNIQUE_ID", "UUID", "SIGN", "UNIFORM", "UDF_NORMALIZE", "SEGMENT", "SYNONYM"],
        keywords: ["SELECT", "FROM", "WHERE", "UNION ALL", "LEFT OUTER JOIN", "RIGHT OUTER JOIN", "FULL OUTER JOIN", "UNION", "INSERT", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "ARCHIVE", "AS", "ASC", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BUCKET", "BUCKETS", "BY", "CASCADE", "CASE", "CFILE", "CHANGE", "CLUSTER", "CLUSTERED", "CLUSTERSTATUS", "COLLECTION", "COLUMN", "COLUMNS", "COMMENT", "COMPUTE", "CONCATENATE", "CONTINUE", "CREATE", "CROSS", "CURRENT", "CURSOR", "DATA", "DATABASE", "DATABASES", "DATE", "DATETIME", "DBPROPERTIES", "DEFERRED", "DELETE", "DELIMITED", "DESC", "DESCRIBE", "DIRECTORY", "DISABLE", "DISTINCT", "DISTRIBUTE", "DOUBLE", "DROP", "ELSE", "ENABLE", "EXSTORE", "END", "ESCAPED", "EXCLUSIVE", "EXISTS", "EXPLAIN", "EXPORT", "EXTENDED", "EXTERNAL", "FALSE", "FETCH", "FIELDS", "FILEFORMAT", "FIRST", "FLOAT", "FOLLOWING", "FORMAT", "FORMATTED", "FULL", "FUNCTION", "FUNCTIONS", "GRANT", "GROUP", "HAVING", "HOLD_DDLTIME", "IDXPROPERTIES", "IMPORT", "IN", "INDEXES", "INPATH", "INPUTDRIVER", "INPUTFORMAT", "INT", "INTERSECT", "INTO", "IS", "ITEMS", "JOIN", "KEYS", "LATERAL", "LEFT", "LIFECYCLE", "LIKE", "LIMIT", "LINES", "LOAD", "LOCAL", "LOCATION", "LOCK", "LOCKS", "LONG", "MAP", "MAPJOIN", "MATERIALIZED", "MINUS", "MSCK", "NOT", "NO_DROP", "NULL", "OF", "OFFLINE", "ON", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUTDRIVER", "OUTPUTFORMAT", "OVER", "OVERWRITE", "PARTITION", "PARTITIONED", "PARTITIONPROPERTIES", "PARTITIONS", "PERCENT", "PLUS", "PRECEDING", "PRESERVE", "PROCEDURE", "PURGE", "RANGE", "RCFILE", "READ", "READONLY", "READS", "REBUILD", "RECORDREADER", "RECORDWRITER", "REDUCE", "REGEXP", "RENAME", "REPAIR", "REPLACE", "RESTRICT", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "SCHEMA", "SCHEMAS", "SEMI", "SEQUENCEFILE", "SERDE", "SERDEPROPERTIES", "SET", "SHARED", "SHOW", "SHOW_DATABASE", "SMALLINT", "SORT", "SORTED", "SSL", "STATISTICS", "STORED", "STREAMTABLE", "STRING", "STRUCT", "TABLE", "TABLES", "TABLESAMPLE", "TBLPROPERTIES", "TEMPORARY", "TERMINATED", "TEXTFILE", "THEN", "TIMESTAMP", "TINYINT", "TO", "TOUCH", "TRANSFORM", "TRIGGER", "TRUNCATE", "TRUE", "UNARCHIVE", "UNBOUNDED", "UNDO", "UNIONTYPE", "UNIQUEJOIN", "UNLOCK", "UNSIGNED", "UPDATE", "USE", "USING", "UTC", "UTC_TMESTAMP", "VIEW", "WHEN", "WHILE"],
    }
}
function keywordsCompleteItemCreater(words) {
    return words.map(
        (word) => {
            return {
                label: word,
                kind: monaco.languages.CompletionItemKind.Keyword,
                detail: "dtsql关键字",
                insertText: word + " "
            }
        }
    )
}
function functionsCompleteItemCreater(functions) {
    return functions.map(
        (functionName) => {
            return {
                label: functionName,
                kind: monaco.languages.CompletionItemKind.Function,
                detail: "dtsql函数",
                insertText: {
                    value: functionName + "($1) "
                }
            }
        }
    )
}
function createDependencyProposals() {
    const words = dtsqlWords();
    const functions = [].concat(words.builtinFunctions).concat(words.windowsFunctions).concat(words.innerFunctions).concat(words.otherFunctions);
    const keywords = [].concat(words.keywords);
    return keywordsCompleteItemCreater(keywords).concat(functionsCompleteItemCreater(functions))
}

monaco.languages.registerCompletionItemProvider("dtsql", {
    provideCompletionItems: function (model, position) {
        return createDependencyProposals();
    }
});
