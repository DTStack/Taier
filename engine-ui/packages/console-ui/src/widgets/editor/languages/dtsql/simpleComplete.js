import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';

function dtsqlWords() {
    return {
        builtinFunctions: ["FROM_UNIXTIME", "UNIX_TIMESTAMP", "TO_DATE", "YEAR", "QUARTER", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "WEEKOFYEAR", "DATEDIFF", "DATE_ADD", "DATE_SUB", "FROM_UTC_TIMESTAMP", "TO_UTC_TIMESTAMP", "CURRENT_DATE", "CURRENT_TIMESTAMP", "ADD_MONTHS", "LAST_DAY", "NEXT_DAY", "TRUNC", "MONTHS_BETWEEN", "DATE_FORMAT", "ROUND", "BROUND", "FLOOR", "CEIL", "RAND", "EXP", "LN", "LOG10", "LOG2", "LOG", "POW", "SQRT", "BIN", "HEX", "UNHEX", "CONV", "ABS", "PMOD", "SIN", "ASIN", "COS", "ACOS", "TAN", "ATAN", "DEGREES", "RADIANS", "POSITIVE", "NEGATIVE", "SIGN", "E", "PI", "FACTORIAL", "CBRT", "SHIFTLEFT", "SHIFTRIGHT", "SHIFTRIGHTUNSIGNED", "GREATEST", "LEAST", "ASCII", "BASE64", "CONCAT", "CHR", "CONTEXT_NGRAMS", "CONCAT_WS", "DECODE", "ENCODE", "FIND_IN_SET", "FORMAT_NUMBER", "GET_JSON_OBJECT", "IN_FILE", "INSTR", "LENGTH", "LOCATE", "LOWER", "LPAD", "LTRIM", "NGRAMS", "PARSE_URL", "PRINTF", "REGEXP_EXTRACT", "REGEXP_REPLACE", "REPEAT", "REVERSE", "RPAD", "RTRIM", "SENTENCES", "SPACE", "SPLIT", "STR_TO_MAP", "SUBSTR", "SUBSTRING_INDEX", "TRANSLATE", "TRIM", "UNBASE64", "UPPER", "INITCAP", "LEVENSHTEIN", "SOUNDEX", "SIZE", "MAP_KEYS", "MAP_VALUES", "ARRAY_CONTAINS", "SORT_ARRAY", "ROW_NUMBER"],
        windowsFunctions: ["COUNT", "AVG", "MAX", "MIN", "STDDEV_SAMP", "SUM"],
        innerFunctions: ["COUNT", "SUM", "AVG", "MIN", "MAX", "VARIANCE", "VAR_SAMP", "STDDEV_POP", "STDDEV_SAMP", "COVAR_POP", "COVAR_SAMP", "CORR", "PERCENTILE", "EXPLODE", "POSEXPLODE", "STACK", "JSON_TUPLE", "PARSE_URL_TUPLE", "INLINE"],
        otherFunctions: ["IF", "NVL", "COALESCE", "ISNULL", "ISNOTNULL", "ASSERT_TRUE", "CAST", "BINARY"],
        keywords: ["SELECT", "FROM", "WHERE", "UNION ALL", "LEFT OUTER JOIN", "RIGHT OUTER JOIN", "FULL OUTER JOIN", "UNION", "INSERT", "ADD", "ADMIN", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "ARCHIVE", "ARRAY", "AS", "ASC", "AUTHORIZATION", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BOOLEAN", "BOTH", "BUCKET", "BUCKETS", "BY", "CASCADE", "CASE", "CHANGE", "CHAR", "CLUSTER", "CLUSTERED", "CLUSTERSTATUS", "COLLECTION", "COLUMN", "COLUMNS", "COMMENT", "COMPACT", "COMPACTIONS", "COMPUTE", "CONCATENATE", "CONF", "CONTINUE", "CREATE", "CROSS", "CUBE", "CURRENT", "CURSOR", "DATA", "DATABASE", "DATABASES", "DATE", "DATETIME", "DBPROPERTIES", "DECIMAL", "DEFERRED", "DEFINED", "DELETE", "DELIMITED", "DEPENDENCY", "DESC", "DESCRIBE", "DIRECTORIES", "DIRECTORY", "DISABLE", "DISTINCT", "DISTRIBUTE", "DOUBLE", "DROP", "ELEM_TYPE", "ELSE", "ENABLE", "END", "ESCAPED", "EXCHANGE", "EXCLUSIVE", "EXISTS", "EXPLAIN", "EXPORT", "EXTENDED", "EXTERNAL", "FALSE", "FETCH", "FIELDS", "FILE", "FILEFORMAT", "FIRST", "FLOAT", "FOLLOWING", "FORMAT", "FORMATTED", "FULL", "FUNCTION", "FUNCTIONS", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD_DDLTIME", "IDXPROPERTIES", "IF", "IGNORE", "IMPORT", "IN", "INDEX", "INDEXES", "INNER", "INPATH", "INPUTDRIVER", "INPUTFORMAT", "INT", "INTERSECT", "INTERVAL", "INTO", "IS", "ITEMS", "JOIN", "JAR", "KEYS", "KEY_TYPE", "LATERAL", "LEFT", "LESS", "LIFECYCLE", "LIKE", "LIMIT", "LINES", "LOAD", "LOCAL", "LOCATION", "LOCK", "LOCKS", "LOGICAL", "LONG", "MACRO", "MAP", "MAPJOIN", "MATERIALIZED", "MINUS", "MORE", "MSCK", "NOT", "NONE", "NOSCAN", "NO_DROP", "NULL", "OF", "OFFLINE", "ON", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUTDRIVER", "OUTPUTFORMAT", "OVER", "OVERWRITE", "OWNER", "PARTIALSCAN", "PARTITION", "PARTITIONED", "PARTITIONS", "PERCENT", "PLUS", "PRECEDING", "PRESERVE", "PRETTY", "PRINCIPALS", "PROCEDURE", "PURGE", "RANGE", "READ", "READONLY", "READS", "REBUILD", "RECORDREADER", "RECORDWRITER", "REDUCE", "REGEXP", "RELOAD", "RENAME", "REPAIR", "REPLACE", "RESTRICT", "REVOKE", "REWRITE", "RIGHT", "RLIKE", "ROLE", "ROLES", "ROLLUP", "ROW", "ROWS", "SCHEMA", "SCHEMAS", "SEMI", "SERDE", "SERDEPROPERTIES", "SERVER", "SET", "SETS", "SHARED", "SHOW", "SHOW_DATABASE", "SKEWED", "SMALLINT", "SORT", "SORTED", "SSL", "STATISTICS", "STORED", "STREAMTABLE", "STRING", "STRUCT", "TABLE", "TABLES", "TABLESAMPLE", "TBLPROPERTIES", "TEMPORARY", "TERMINATED", "TEXTFILE", "THEN", "TIMESTAMP", "TINYINT", "TO", "TOUCH", "TRANSACTIONS", "TRANSFORM", "TRIGGER", "TRUNCATE", "TRUE", "UNARCHIVE", "UNBOUNDED", "UNDO", "UNIONTYPE", "UNIQUEJOIN", "UNLOCK", "UNSET", "UNSIGNED", "UPDATE", "URI", "USE", "USER", "USING", "UTC", "UTC_TMESTAMP", "VALUES", "VALUE_TYPE", "VARCHAR", "VIEW", "WHEN", "WHILE", "WINDOW", "WITH"],
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
