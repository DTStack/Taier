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

/* eslint-disable no-useless-escape */
/* ---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*-------------------------------------------------------------------------------------------- */
'use strict';
export var conf: any = {
    comments: {
        lineComment: '--'
    },
    brackets: [
        ['{', '}'],
        ['[', ']'],
        ['(', ')']
    ],
    autoClosingPairs: [{
        open: '{',
        close: '}'
    }, {
        open: '[',
        close: ']'
    }, {
        open: '(',
        close: ')'
    }, {
        open: '"',
        close: '"'
    }, {
        open: "'",
        close: "'"
    }, {
        open: '`',
        close: '`'
    }],
    surroundingPairs: [{
        open: '{',
        close: '}'
    }, {
        open: '[',
        close: ']'
    }, {
        open: '(',
        close: ')'
    }, {
        open: '"',
        close: '"'
    }, {
        open: "'",
        close: "'"
    }, {
        open: '`',
        close: '`'
    }]
};
export var language: any = {
    defaultToken: '',
    tokenPostfix: '.sql',
    ignoreCase: !0,
    brackets: [{
        open: '[',
        close: ']',
        token: 'delimiter.square'
    }, {
        open: '(',
        close: ')',
        token: 'delimiter.parenthesis'
    }],
    keywords: ['SELECT', 'FROM', 'WHERE', 'UNION ALL', 'LEFT OUTER JOIN', 'RIGHT OUTER JOIN', 'FULL OUTER JOIN', 'UNION', 'INSERT', 'ADD', 'ADMIN', 'AFTER', 'ALL', 'ALTER', 'ANALYZE', 'AND', 'ARCHIVE', 'ARRAY', 'AS', 'ASC', 'AUTHORIZATION', 'BEFORE', 'BETWEEN', 'BIGINT', 'BINARY', 'BOOLEAN', 'BOTH', 'BUCKET', 'BUCKETS', 'BY', 'CASCADE', 'CASE', 'CHANGE', 'CHAR', 'CLUSTER', 'CLUSTERED', 'CLUSTERSTATUS', 'COLLECTION', 'COLUMN', 'COLUMNS', 'COMMENT', 'COMPACT', 'COMPACTIONS', 'COMPUTE', 'CONCATENATE', 'CONF', 'CONTINUE', 'CREATE', 'CROSS', 'CUBE', 'CURRENT', 'CURSOR', 'DATA', 'DATABASE', 'DATABASES', 'DATE', 'DATETIME', 'DBPROPERTIES', 'DECIMAL', 'DEFERRED', 'DEFINED', 'DELETE', 'DELIMITED', 'DEPENDENCY', 'DESC', 'DESCRIBE', 'DIRECTORIES', 'DIRECTORY', 'DISABLE', 'DISTINCT', 'DISTRIBUTE', 'DOUBLE', 'DROP', 'ELEM_TYPE', 'ELSE', 'ENABLE', 'END', 'ESCAPED', 'EXCHANGE', 'EXCLUSIVE', 'EXISTS', 'EXPLAIN', 'EXPORT', 'EXTENDED', 'EXTERNAL', 'FALSE', 'FETCH', 'FIELDS', 'FILE', 'FILEFORMAT', 'FIRST', 'FLOAT', 'FOLLOWING', 'FORMAT', 'FORMATTED', 'FULL', 'FUNCTION', 'FUNCTIONS', 'GRANT', 'GROUP', 'GROUPING', 'HAVING', 'HOLD_DDLTIME', 'IDXPROPERTIES', 'IGNORE', 'IMPORT', 'IN', 'INDEX', 'INDEXES', 'INNER', 'INPATH', 'INPUTDRIVER', 'INPUTFORMAT', 'INT', 'INTERSECT', 'INTERVAL', 'INTO', 'IS', 'ITEMS', 'JOIN', 'JAR', 'KEYS', 'KEY_TYPE', 'LATERAL', 'LEFT', 'LESS', 'LIFECYCLE', 'LIKE', 'LIMIT', 'LINES', 'LOAD', 'LOCAL', 'LOCATION', 'LOCK', 'LOCKS', 'LOGICAL', 'LONG', 'MACRO', 'MAP', 'MAPJOIN', 'MATERIALIZED', 'MINUS', 'MORE', 'MSCK', 'NOT', 'NONE', 'NOSCAN', 'NO_DROP', 'NULL', 'OF', 'OFFLINE', 'ON', 'OPTION', 'OR', 'ORC', 'ORDER', 'OUT', 'OUTER', 'OUTPUTDRIVER', 'OUTPUTFORMAT', 'OVER', 'OVERWRITE', 'OWNER', 'PARTIALSCAN', 'PARTITION', 'PARTITIONED', 'PARTITIONS', 'PERCENT', 'PARQUET', 'PLUS', 'PRECEDING', 'PRESERVE', 'PRETTY', 'PRINCIPALS', 'PROCEDURE', 'PURGE', 'RANGE', 'READ', 'READONLY', 'READS', 'REBUILD', 'RECORDREADER', 'RECORDWRITER', 'REDUCE', 'REGEXP', 'RELOAD', 'RENAME', 'REPAIR', 'REPLACE', 'RESTRICT', 'REVOKE', 'REWRITE', 'RIGHT', 'RLIKE', 'ROLE', 'ROLES', 'ROLLUP', 'ROW', 'ROWS', 'SCHEMA', 'SCHEMAS', 'SEMI', 'SERDE', 'SERDEPROPERTIES', 'SERVER', 'SET', 'SETS', 'SHARED', 'SHOW', 'SHOW_DATABASE', 'SKEWED', 'SMALLINT', 'SORT', 'SORTED', 'SSL', 'STATISTICS', 'STORED', 'STREAMTABLE', 'STRING', 'STRUCT', 'TABLE', 'TABLES', 'TABLESAMPLE', 'TBLPROPERTIES', 'TEMPORARY', 'TERMINATED', 'TEXTFILE', 'THEN', 'TIMESTAMP', 'TINYINT', 'TO', 'TOUCH', 'TRANSACTIONS', 'TRANSFORM', 'TRIGGER', 'TRUNCATE', 'TRUE', 'UNARCHIVE', 'UNBOUNDED', 'UNDO', 'UNIONTYPE', 'UNIQUEJOIN', 'UNLOCK', 'UNSET', 'UNSIGNED', 'UPDATE', 'URI', 'USE', 'USER', 'USING', 'UTC', 'UTC_TMESTAMP', 'VALUES', 'VALUE_TYPE', 'VARCHAR', 'VIEW', 'WHEN', 'WHILE', 'WINDOW', 'WITH'],
    operators: ['|', '%', '&', '&&', '*', '+', '-', '.', '/', ';', '<', '<=', '<>', '=', '>', '>=', '?'],
    builtinFunctions: ['FROM_UNIXTIME', 'UNIX_TIMESTAMP', 'TO_DATE', 'YEAR', 'QUARTER', 'MONTH', 'DAY', 'HOUR', 'MINUTE', 'SECOND', 'WEEKOFYEAR', 'DATEDIFF', 'DATE_ADD', 'DATE_SUB', 'FROM_UTC_TIMESTAMP', 'TO_UTC_TIMESTAMP', 'CURRENT_DATE', 'CURRENT_TIMESTAMP', 'ADD_MONTHS', 'LAST_DAY', 'NEXT_DAY', 'TRUNC', 'MONTHS_BETWEEN', 'DATE_FORMAT', 'ROUND', 'BROUND', 'FLOOR', 'CEIL', 'RAND', 'EXP', 'LN', 'LOG10', 'LOG2', 'LOG', 'POW', 'SQRT', 'BIN', 'HEX', 'UNHEX', 'CONV', 'ABS', 'PMOD', 'SIN', 'ASIN', 'COS', 'ACOS', 'TAN', 'ATAN', 'DEGREES', 'RADIANS', 'POSITIVE', 'NEGATIVE', 'SIGN', 'E', 'PI', 'FACTORIAL', 'CBRT', 'SHIFTLEFT', 'SHIFTRIGHT', 'SHIFTRIGHTUNSIGNED', 'GREATEST', 'LEAST', 'ASCII', 'BASE64', 'CONCAT', 'CHR', 'CONTEXT_NGRAMS', 'CONCAT_WS', 'DECODE', 'ENCODE', 'FIND_IN_SET', 'FORMAT_NUMBER', 'GET_JSON_OBJECT', 'IN_FILE', 'INSTR', 'LENGTH', 'LOCATE', 'LOWER', 'LPAD', 'LTRIM', 'NGRAMS', 'PARSE_URL', 'PRINTF', 'REGEXP_EXTRACT', 'REGEXP_REPLACE', 'REPEAT', 'REVERSE', 'RPAD', 'RTRIM', 'SENTENCES', 'SPACE', 'SPLIT', 'STR_TO_MAP', 'SUBSTR', 'SUBSTRING_INDEX', 'TRANSLATE', 'TRIM', 'UNBASE64', 'UPPER', 'INITCAP', 'LEVENSHTEIN', 'SOUNDEX', 'SIZE', 'MAP_KEYS', 'MAP_VALUES', 'ARRAY_CONTAINS', 'SORT_ARRAY', 'ROW_NUMBER'],
    windowsFunctions: ['COUNT', 'AVG', 'MAX', 'MIN', 'STDDEV_SAMP', 'SUM'],
    innerFunctions: ['COUNT', 'SUM', 'AVG', 'MIN', 'MAX', 'VARIANCE', 'VAR_SAMP', 'STDDEV_POP', 'STDDEV_SAMP', 'COVAR_POP', 'COVAR_SAMP', 'CORR', 'PERCENTILE', 'EXPLODE', 'POSEXPLODE', 'STACK', 'JSON_TUPLE', 'PARSE_URL_TUPLE', 'INLINE'],
    otherFunctions: ['IF', 'NVL', 'COALESCE', 'ISNULL', 'ISNOTNULL', 'ASSERT_TRUE', 'CAST', 'BINARY'],
    customFunctions: [],
    pseudoColumns: ['$ACTION', '$IDENTITY', '$ROWGUID', '$PARTITION'],
    tokenizer: {
        root: [{
            include: '@comments'
        }, {
            include: '@whitespace'
        }, {
            include: '@pseudoColumns'
        }, {
            include: '@numbers'
        }, {
            include: '@strings'
        }, {
            include: '@complexIdentifiers'
        }, {
            include: '@scopes'
        },
        // eslint-disable-next-line no-control-regex
        [/[\x00-\x1F\x7F-\x9F]/, 'invalid'],
        [/[;,.]/, 'delimiter'],
        [/[()]/, '@brackets'],
        [/[\w@#$]+/, {
            cases: {
                '@keywords': 'keyword',
                '@operators': 'operator',
                '@windowsFunctions': 'predefined',
                '@builtinFunctions': 'predefined',
                '@innerFunctions': 'predefined',
                '@otherFunctions': 'predefined',
                '@customFunctions': 'predefined',
                '@default': 'identifier'
            }
        }],
        [/[<>=!%&+\-*/|~^]/, 'operator']
        ],
        whitespace: [
            [/\s+/, 'white']
        ],
        comments: [
            [/--+.*/, 'comment']
        ],
        pseudoColumns: [
            [/[$][A-Za-z_][\w@#$]*/, {
                cases: {
                    '@pseudoColumns': 'predefined',
                    '@default': 'identifier'
                }
            }]
        ],
        numbers: [
            [/0[xX][0-9a-fA-F]*/, 'number'],
            [/[$][+-]*\d*(\.\d*)?/, 'number'],
            [/((\d+(\.\d*)?)|(\.\d+))([eE][\-+]?\d+)?/, 'number']
        ],
        strings: [
            [/'$/, 'string', '@popall'],
            [/'/, 'string', '@stringBody'],
            [/"$/, 'string', '@popall'],
            [/"/, 'string', '@dblStringBody']
        ],
        stringBody: [
            [/\\./, 'string'],
            [/'/, 'string', '@popall'],
            [/.(?=.*')/, 'string'],
            [/.*\\$/, 'string'],
            [/.*$/, 'string', '@popall']
        ],
        dblStringBody: [
            [/\\./, 'string'],
            [/"/, 'string', '@popall'],
            [/.(?=.*")/, 'string'],
            [/.*\\$/, 'string'],
            [/.*$/, 'string', '@popall']
        ],
        complexIdentifiers: [
            [/\[/, {
                token: 'identifier.quote',
                next: '@bracketedIdentifier'
            }],
            [/"/, {
                token: 'identifier.quote',
                next: '@stringDouble'
            }],
            [/`/, { token: 'identifier.quote', next: '@quotedIdentifier' }]
        ],
        bracketedIdentifier: [
            [/[^\]]+/, 'identifier'],
            [/]]/, 'identifier'],
            [/]/, {
                token: 'identifier.quote',
                next: '@pop'
            }]
        ],
        quotedIdentifier: [
            [/[^`]+/, 'identifier'],
            [/``/, 'identifier'],
            [/`/, { token: 'identifier.quote', next: '@pop' }]
        ],
        stringDouble: [
            [/[^"]+/, 'identifier'],
            [/""/, 'identifier'],
            [/"/, {
                token: 'identifier.quote',
                next: '@pop'
            }]
        ],
        scopes: [
            [/BEGIN\s+(DISTRIBUTED\s+)?TRAN(SACTION)?\b/i, 'keyword'],
            [/BEGIN\s+TRY\b/i, {
                token: 'keyword.try'
            }],
            [/END\s+TRY\b/i, {
                token: 'keyword.try'
            }],
            [/BEGIN\s+CATCH\b/i, {
                token: 'keyword.catch'
            }],
            [/END\s+CATCH\b/i, {
                token: 'keyword.catch'
            }],
            [/(BEGIN|CASE)\b/i, {
                token: 'keyword.block'
            }],
            [/END\b/i, {
                token: 'keyword.block'
            }],
            [/WHEN\b/i, {
                token: 'keyword.choice'
            }],
            [/THEN\b/i, {
                token: 'keyword.choice'
            }]
        ]
    }
};
