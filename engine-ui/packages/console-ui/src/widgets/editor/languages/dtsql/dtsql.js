/*---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*--------------------------------------------------------------------------------------------*/
'use strict';
export var conf = {
    comments: {
        lineComment: "--"
      },
      brackets: [
        ["{", "}"],
        ["[", "]"],
        ["(", ")"]
      ],
      autoClosingPairs: [{
        open: "{",
        close: "}"
      }, {
        open: "[",
        close: "]"
      }, {
        open: "(",
        close: ")"
      }, {
        open: '"',
        close: '"'
      }, {
        open: "'",
        close: "'"
      }, {
        open: "`",
        close: "`"
      }],
      surroundingPairs: [{
        open: "{",
        close: "}"
      }, {
        open: "[",
        close: "]"
      }, {
        open: "(",
        close: ")"
      }, {
        open: '"',
        close: '"'
      }, {
        open: "'",
        close: "'"
      }, {
        open: "`",
        close: "`"
      }]
};
export var language = {
    defaultToken: "",
    tokenPostfix: ".sql",
    ignoreCase: !0,
    brackets: [{
      open: "[",
      close: "]",
      token: "delimiter.square"
    }, {
      open: "(",
      close: ")",
      token: "delimiter.parenthesis"
    }],
    keywords: ["SELECT", "FROM", "WHERE", "UNION ALL", "LEFT OUTER JOIN", "RIGHT OUTER JOIN", "FULL OUTER JOIN", "UNION", "INSERT", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "ARCHIVE", "AS", "ASC", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BUCKET", "BUCKETS", "BY", "CASCADE", "CASE", "CFILE", "CHANGE", "CLUSTER", "CLUSTERED", "CLUSTERSTATUS", "COLLECTION", "COLUMN", "COLUMNS", "COMMENT", "COMPUTE", "CONCATENATE", "CONTINUE", "CREATE", "CROSS", "CURRENT", "CURSOR", "DATA", "DATABASE", "DATABASES", "DATE", "DATETIME", "DBPROPERTIES", "DEFERRED", "DELETE", "DELIMITED", "DESC", "DESCRIBE", "DIRECTORY", "DISABLE", "DISTINCT", "DISTRIBUTE", "DOUBLE", "DROP", "ELSE", "ENABLE", "EXSTORE", "END", "ESCAPED", "EXCLUSIVE", "EXISTS", "EXPLAIN", "EXPORT", "EXTENDED", "EXTERNAL", "FALSE", "FETCH", "FIELDS", "FILEFORMAT", "FIRST", "FLOAT", "FOLLOWING", "FORMAT", "FORMATTED", "FULL", "FUNCTION", "FUNCTIONS", "GRANT", "GROUP", "HAVING", "HOLD_DDLTIME", "IDXPROPERTIES", "IMPORT", "IN", "INDEXES", "INPATH", "INPUTDRIVER", "INPUTFORMAT", "INT", "INTERSECT", "INTO", "IS", "ITEMS", "JOIN", "KEYS", "LATERAL", "LEFT", "LIFECYCLE", "LIKE", "LIMIT", "LINES", "LOAD", "LOCAL", "LOCATION", "LOCK", "LOCKS", "LONG", "MAP", "MAPJOIN", "MATERIALIZED", "MINUS", "MSCK", "NOT", "NO_DROP", "NULL", "OF", "OFFLINE", "ON", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUTDRIVER", "OUTPUTFORMAT", "OVER", "OVERWRITE", "PARTITION", "PARTITIONED", "PARTITIONPROPERTIES", "PARTITIONS", "PERCENT", "PLUS", "PRECEDING", "PRESERVE", "PROCEDURE", "PURGE", "RANGE", "RCFILE", "READ", "READONLY", "READS", "REBUILD", "RECORDREADER", "RECORDWRITER", "REDUCE", "REGEXP", "RENAME", "REPAIR", "REPLACE", "RESTRICT", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "SCHEMA", "SCHEMAS", "SEMI", "SEQUENCEFILE", "SERDE", "SERDEPROPERTIES", "SET", "SHARED", "SHOW", "SHOW_DATABASE", "SMALLINT", "SORT", "SORTED", "SSL", "STATISTICS", "STORED", "STREAMTABLE", "STRING", "STRUCT", "TABLE", "TABLES", "TABLESAMPLE", "TBLPROPERTIES", "TEMPORARY", "TERMINATED", "TEXTFILE", "THEN", "TIMESTAMP", "TINYINT", "TO", "TOUCH", "TRANSFORM", "TRIGGER", "TRUNCATE", "TRUE", "UNARCHIVE", "UNBOUNDED", "UNDO", "UNIONTYPE", "UNIQUEJOIN", "UNLOCK", "UNSIGNED", "UPDATE", "USE", "USING", "UTC", "UTC_TMESTAMP", "VIEW", "WHEN", "WHILE"],
    operators: ["|", "%", "&", "&&", "*", "+", "-", ".", "/", ";", "<", "<=", "<>", "=", ">", ">=", "?"],
    builtinFunctions: ["DATEADD", "DATEDIFF", "DATEPART", "DATETRUNC", "FROM_UNIXTIME", "GETDATE", "ISDATE", "LASTDAY", "TO_DATE", "TO_CHAR", "UNIX_TIMESTAMP", "WEEKDAY", "WEEKOFYEAR", "ABS", "ACOS", "ASIN", "ATAN", "CEIL", "CONV", "COS", "COSH", "COT", "EXP", "FLOOR", "LN", "LOG", "POW", "RAND", "ROUND", "SIN", "SINH", "SQRT", "TAN", "TANH", "TRUNC", "CHAR_MATCHCOUNT", "CHR", "CONCAT", "GET_JSON_OBJECT", "INSTR", "IS_ENCODING", "IP2REGION", "KEYVALUE", "LENGTH", "LENGTHB", "MD5", "PARSE_URL", "REGEXP_EXTRACT", "REGEXP_INSTR", "REGEXP_REPLACE", "REGEXP_SUBSTR", "REGEXP_COUNT", "SPLIT_PART", "SUBSTR", "TOLOWER", "TOUPPER", "TO_CHAR", "TRIM", "LTRIM", "RTRIM", "URL_ENCODE", "URL_DECODE", "REVERSE", "SPACE", "REPEAT", "ASCII"],
    windowsFunctions: ["COUNT", "AVG", "MAX", "MIN", "MEDIAN", "STDDEV", "STDDEV_SAMP", "SUM", "DENSE_RANK", "RANK", "LAG", "LEAD", "PERCENT_RANK", "ROW_NUMBER", "CLUSTER_SAMPLE"],
    innerFunctions: ["COUNT", "AVG", "MAX", "MIN", "MEDIAN", "STDDEV", "STDDEV_SAMP", "SUM", "WM_CONCAT"],
    otherFunctions: ["ARRAY", "ARRAY_CONTAINS", "IF", "CAST", "COALESCE", "DECODE", "EXPLODE", "GET_IDCARD_AGE", "GET_IDCARD_BIRTHDAY", "GET_IDCARD_SEX", "GREATEST", "INDEX", "MAX_PT", "ORDINAL", "LEAST", "SAMPLE", "SIZE", "SPLIT", "STR_TO_MAP", "TRANS_ARRAY", "TRANS_COLS", "UNIQUE_ID", "UUID", "SIGN", "UNIFORM", "UDF_NORMALIZE", "SEGMENT", "SYNONYM"],
    pseudoColumns: ["$ACTION", "$IDENTITY", "$ROWGUID", "$PARTITION"],
    tokenizer: {
      root: [{
          include: "@comments"
        }, {
          include: "@whitespace"
        }, {
          include: "@pseudoColumns"
        }, {
          include: "@numbers"
        }, {
          include: "@strings"
        }, {
          include: "@complexIdentifiers"
        }, {
          include: "@scopes"
        },
        [/[;,.]/, "delimiter"],
        [/[()]/, "@brackets"],
        [/[\w@#$]+/, {
          cases: {
            "@keywords": "keyword",
            "@operators": "operator",
            "@windowsFunctions": "predefined",
            "@builtinFunctions": "predefined",
            "@innerFunctions": "predefined",
            "@otherFunctions": "predefined",
            "@default": "identifier"
          }
        }],
        [/[<>=!%&+\-*/|~^]/, "operator"]
      ],
      whitespace: [
        [/\s+/, "white"]
      ],
      comments: [
        [/--+.*/, "comment"]
      ],
      pseudoColumns: [
        [/[$][A-Za-z_][\w@#$]*/, {
          cases: {
            "@pseudoColumns": "predefined",
            "@default": "identifier"
          }
        }]
      ],
      numbers: [
        [/0[xX][0-9a-fA-F]*/, "number"],
        [/[$][+-]*\d*(\.\d*)?/, "number"],
        [/((\d+(\.\d*)?)|(\.\d+))([eE][\-+]?\d+)?/, "number"]
      ],
      strings: [
        [/'$/, "string", "@popall"],
        [/'/, "string", "@stringBody"],
        [/"$/, "string", "@popall"],
        [/"/, "string", "@dblStringBody"]
      ],
      stringBody: [
        [/\\./, "string"],
        [/'/, "string", "@popall"],
        [/.(?=.*')/, "string"],
        [/.*\\$/, "string"],
        [/.*$/, "string", "@popall"]
      ],
      dblStringBody: [
        [/\\./, "string"],
        [/"/, "string", "@popall"],
        [/.(?=.*")/, "string"],
        [/.*\\$/, "string"],
        [/.*$/, "string", "@popall"]
      ],
      complexIdentifiers: [
        [/\[/, {
          token: "identifier.quote",
          next: "@bracketedIdentifier"
        }],
        [/"/, {
          token: "identifier.quote",
          next: "@quotedIdentifier"
        }]
      ],
      bracketedIdentifier: [
        [/[^\]]+/, "identifier"],
        [/]]/, "identifier"],
        [/]/, {
          token: "identifier.quote",
          next: "@pop"
        }]
      ],
      quotedIdentifier: [
        [/[^"]+/, "identifier"],
        [/""/, "identifier"],
        [/"/, {
          token: "identifier.quote",
          next: "@pop"
        }]
      ],
      scopes: [
        [/BEGIN\s+(DISTRIBUTED\s+)?TRAN(SACTION)?\b/i, "keyword"],
        [/BEGIN\s+TRY\b/i, {
          token: "keyword.try"
        }],
        [/END\s+TRY\b/i, {
          token: "keyword.try"
        }],
        [/BEGIN\s+CATCH\b/i, {
          token: "keyword.catch"
        }],
        [/END\s+CATCH\b/i, {
          token: "keyword.catch"
        }],
        [/(BEGIN|CASE)\b/i, {
          token: "keyword.block"
        }],
        [/END\b/i, {
          token: "keyword.block"
        }],
        [/WHEN\b/i, {
          token: "keyword.choice"
        }],
        [/THEN\b/i, {
          token: "keyword.choice"
        }]
      ]
    }
};
