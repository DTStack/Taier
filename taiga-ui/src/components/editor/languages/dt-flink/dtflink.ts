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
    keywords: ['A', 'ABS', 'ABSOLUTE', 'ACTION', 'ADA', 'ADD', 'ADMIN', 'AFTER', 'ALL', 'ALLOCATE', 'ALLOW', 'ALTER', 'ALWAYS', 'AND', 'ANY', 'ARE', 'ARRAY', 'AS', 'ASC', 'ASENSITIVE', 'ASSERTION', 'ASSIGNMENT', 'ASYMMETRIC', 'AT', 'ATOMIC', 'ATTRIBUTE', 'ATTRIBUTES', 'AUTHORIZATION', 'AVG', 'BEFORE', 'BEGIN', 'BERNOULLI', 'BETWEEN', 'BIGINT', 'BINARY', 'BIT', 'BLOB', 'BOOLEAN', 'BOTH', 'BREADTH', 'BY', 'C', 'CALL', 'CALLED', 'CARDINALITY', 'CASCADE', 'CASCADED', 'CASE', 'CAST', 'CATALOG', 'CATALOG_NAME', 'CEIL', 'CEILING', 'CENTURY', 'CHAIN', 'CHAR', 'CHARACTER', 'CHARACTERISTICS', 'CHARACTERS', 'CHARACTER_LENGTH', 'CHARACTER_SET_CATALOG', 'CHARACTER_SET_NAME', 'CHARACTER_SET_SCHEMA', 'CHAR_LENGTH', 'CHECK', 'CLASS_ORIGIN', 'CLOB', 'CLOSE', 'COALESCE', 'COBOL', 'COLLATE', 'COLLATION', 'COLLATION_CATALOG', 'COLLATION_NAME', 'COLLATION_SCHEMA', 'COLLECT', 'COLUMN', 'COLUMN_NAME', 'COMMAND_FUNCTION', 'COMMAND_FUNCTION_CODE', 'COMMIT', 'COMMITTED', 'CONDITION', 'CONDITION_NUMBER', 'CONNECT', 'CONNECTION', 'CONNECTION_NAME', 'CONSTRAINT', 'CONSTRAINTS', 'CONSTRAINT_CATALOG', 'CONSTRAINT_NAME', 'CONSTRAINT_SCHEMA', 'CONSTRUCTOR', 'CONTAINS', 'CONTINUE', 'CONVERT', 'CORR', 'CORRESPONDING', 'COUNT', 'COVAR_POP', 'COVAR_SAMP', 'CREATE', 'CROSS', 'CUBE', 'CUME_DIST', 'CURRENT', 'CURRENT_CATALOG', 'CURRENT_DATE', 'CURRENT_DEFAULT_TRANSFORM_GROUP', 'CURRENT_PATH', 'CURRENT_ROLE', 'CURRENT_SCHEMA', 'CURRENT_TIME', 'CURRENT_TIMESTAMP', 'CURRENT_TRANSFORM_GROUP_FOR_TYPE', 'CURRENT_USER', 'CURSOR', 'CURSOR_NAME', 'CYCLE', 'DATA', 'DATABASE', 'DATE', 'DATETIME_INTERVAL_CODE', 'DATETIME_INTERVAL_PRECISION', 'DAY', 'DEALLOCATE', 'DEC', 'DECADE', 'DECIMAL', 'DECLARE', 'DEFAULT', 'DEFAULTS', 'DEFERRABLE', 'DEFERRED', 'DEFINED', 'DEFINER', 'DEGREE', 'DELETE', 'DENSE_RANK', 'DEPTH', 'DEREF', 'DERIVED', 'DESC', 'DESCRIBE', 'DESCRIPTION', 'DESCRIPTOR', 'DETERMINISTIC', 'DIAGNOSTICS', 'DISALLOW', 'DISCONNECT', 'DISPATCH', 'DISTINCT', 'DOMAIN', 'DOUBLE', 'DOW', 'DOY', 'DROP', 'DYNAMIC', 'DYNAMIC_FUNCTION', 'DYNAMIC_FUNCTION_CODE', 'EACH', 'ELEMENT', 'ELSE', 'END', 'END', '-', 'EXEC', 'EPOCH', 'EQUALS', 'ESCAPE', 'EVERY', 'EXCEPT', 'EXCEPTION', 'EXCLUDE', 'EXCLUDING', 'EXEC', 'EXECUTE', 'EXISTS', 'EXP', 'EXPLAIN', 'EXTEND', 'EXTERNAL', 'EXTRACT', 'FALSE', 'FETCH', 'FILTER', 'FINAL', 'FIRST', 'FIRST_VALUE', 'FLOAT', 'FLOOR', 'FOLLOWING', 'FOR', 'FOREIGN', 'FORTRAN', 'FOUND', 'FRAC_SECOND', 'FREE', 'FROM', 'FULL', 'FUNCTION', 'FUSION', 'G', 'GENERAL', 'GENERATED', 'GET', 'GLOBAL', 'GO', 'GOTO', 'GRANT', 'GRANTED', 'GROUP', 'GROUPING', 'HAVING', 'HIERARCHY', 'HOLD', 'HOUR', 'IDENTITY', 'IMMEDIATE', 'IMPLEMENTATION', 'IMPORT', 'IN', 'INCLUDING', 'INCREMENT', 'INDICATOR', 'INITIALLY', 'INNER', 'INOUT', 'INPUT', 'INSENSITIVE', 'INSERT', 'INSTANCE', 'INSTANTIABLE', 'INT', 'INTEGER', 'INTERSECT', 'INTERSECTION', 'INTERVAL', 'INTO', 'INVOKER', 'IS', 'ISOLATION', 'JAVA', 'JOIN', 'K', 'KEY', 'KEY_MEMBER', 'KEY_TYPE', 'LABEL', 'LANGUAGE', 'LARGE', 'LAST', 'LAST_VALUE', 'LATERAL', 'LEADING', 'LEFT', 'LENGTH', 'LEVEL', 'LIBRARY', 'LIKE', 'LIMIT', 'LN', 'LOCAL', 'LOCALTIME', 'LOCALTIMESTAMP', 'LOCATOR', 'LOWER', 'M', 'MAP', 'MATCH', 'MATCHED', 'MAX', 'MAXVALUE', 'MEMBER', 'MERGE', 'MESSAGE_LENGTH', 'MESSAGE_OCTET_LENGTH', 'MESSAGE_TEXT', 'METHOD', 'MICROSECOND', 'MILLENNIUM', 'MIN', 'MINUTE', 'MINVALUE', 'MOD', 'MODIFIES', 'MODULE', 'MONTH', 'MORE', 'MULTISET', 'MUMPS', 'NAME', 'NAMES', 'NATIONAL', 'NATURAL', 'NCHAR', 'NCLOB', 'NESTING', 'NEW', 'NEXT', 'NO', 'NONE', 'NORMALIZE', 'NORMALIZED', 'NOT', 'NULL', 'NULLABLE', 'NULLIF', 'NULLS', 'NUMBER', 'NUMERIC', 'OBJECT', 'OCTETS', 'OCTET_LENGTH', 'OF', 'OFFSET', 'OLD', 'ON', 'ONLY', 'OPEN', 'OPTION', 'OPTIONS', 'OR', 'ORDER', 'ORDERING', 'ORDINALITY', 'OTHERS', 'OUT', 'OUTER', 'OUTPUT', 'OVER', 'OVERLAPS', 'OVERLAY', 'OVERRIDING', 'PAD', 'PARAMETER', 'PARAMETER_MODE', 'PARAMETER_NAME', 'PARAMETER_ORDINAL_POSITION', 'PARAMETER_SPECIFIC_CATALOG', 'PARAMETER_SPECIFIC_NAME', 'PARAMETER_SPECIFIC_SCHEMA', 'PARTIAL', 'PARTITION', 'PASCAL', 'PASSTHROUGH', 'PATH', 'PERCENTILE_CONT', 'PERCENTILE_DISC', 'PERCENT_RANK', 'PLACING', 'PLAN', 'PLI', 'POSITION', 'POWER', 'PRECEDING', 'PRECISION', 'PREPARE', 'PRESERVE', 'PRIMARY', 'PRIOR', 'PRIVILEGES', 'PROCEDURE', 'PUBLIC', 'QUARTER', 'RANGE', 'RANK', 'READ', 'READS', 'REAL', 'RECURSIVE', 'REF', 'REFERENCES', 'REFERENCING', 'REGR_AVGX', 'REGR_AVGY', 'REGR_COUNT', 'REGR_INTERCEPT', 'REGR_R2', 'REGR_SLOPE', 'REGR_SXX', 'REGR_SXY', 'REGR_SYY', 'RELATIVE', 'RELEASE', 'REPEATABLE', 'RESET', 'RESTART', 'RESTRICT', 'RESULT', 'RETURN', 'RETURNED_CARDINALITY', 'RETURNED_LENGTH', 'RETURNED_OCTET_LENGTH', 'RETURNED_SQLSTATE', 'RETURNS', 'REVOKE', 'RIGHT', 'ROLE', 'ROLLBACK', 'ROLLUP', 'ROUTINE', 'ROUTINE_CATALOG', 'ROUTINE_NAME', 'ROUTINE_SCHEMA', 'ROW', 'ROWS', 'ROW_COUNT', 'ROW_NUMBER', 'SAVEPOINT', 'SCALE', 'SCHEMA', 'SCHEMA_NAME', 'SCOPE', 'SCOPE_CATALOGS', 'SCOPE_NAME', 'SCOPE_SCHEMA', 'SCROLL', 'SEARCH', 'SECOND', 'SECTION', 'SECURITY', 'SELECT', 'SELF', 'SENSITIVE', 'SEQUENCE', 'SERIALIZABLE', 'SERVER', 'SERVER_NAME', 'SESSION', 'SESSION_USER', 'SET', 'SETS', 'SIMILAR', 'SIMPLE', 'SIZE', 'SMALLINT', 'SOME', 'SOURCE', 'SPACE', 'SPECIFIC', 'SPECIFICTYPE', 'SPECIFIC_NAME', 'SQL', 'SQLEXCEPTION', 'SQLSTATE', 'SQLWARNING', 'SQL_TSI_DAY', 'SQL_TSI_FRAC_SECOND', 'SQL_TSI_HOUR', 'SQL_TSI_MICROSECOND', 'SQL_TSI_MINUTE', 'SQL_TSI_MONTH', 'SQL_TSI_QUARTER', 'SQL_TSI_SECOND', 'SQL_TSI_WEEK', 'SQL_TSI_YEAR', 'SQRT', 'START', 'STATE', 'STATEMENT', 'STATIC', 'STDDEV_POP', 'STDDEV_SAMP', 'STREAM', 'STRUCTURE', 'STYLE', 'SUBCLASS_ORIGIN', 'SUBMULTISET', 'SUBSTITUTE', 'SUBSTRING', 'SUM', 'SYMMETRIC', 'SYSTEM', 'SYSTEM_USER', 'TABLE', 'TABLESAMPLE', 'TABLE_NAME', 'TEMPORARY', 'THEN', 'TIES', 'TIME', 'TIMESTAMP', 'TIMESTAMPADD', 'TIMESTAMPDIFF', 'TIMEZONE_HOUR', 'TIMEZONE_MINUTE', 'TINYINT', 'TO', 'TOP_LEVEL_COUNT', 'TRAILING', 'TRANSACTION', 'TRANSACTIONS_ACTIVE', 'TRANSACTIONS_COMMITTED', 'TRANSACTIONS_ROLLED_BACK', 'TRANSFORM', 'TRANSFORMS', 'TRANSLATE', 'TRANSLATION', 'TREAT', 'TRIGGER', 'TRIGGER_CATALOG', 'TRIGGER_NAME', 'TRIGGER_SCHEMA', 'TRIM', 'TRUE', 'TYPE', 'UESCAPE', 'UNBOUNDED', 'UNCOMMITTED', 'UNDER', 'UNION', 'UNIQUE', 'UNKNOWN', 'UNNAMED', 'UNNEST', 'UPDATE', 'UPPER', 'UPSERT', 'USAGE', 'USER', 'USER_DEFINED_TYPE_CATALOG', 'USER_DEFINED_TYPE_CODE', 'USER_DEFINED_TYPE_NAME', 'USER_DEFINED_TYPE_SCHEMA', 'USING', 'VALUE', 'VALUES', 'VARBINARY', 'VARCHAR', 'VARYING', 'VAR_POP', 'VAR_SAMP', 'VERSION', 'VIEW', 'WEEK', 'WHEN', 'WHENEVER', 'WHERE', 'WIDTH_BUCKET', 'WINDOW', 'WITH', 'WITHIN', 'WITHOUT', 'WORK', 'WRAPPER', 'WRITE', 'XML', 'YEAR', 'ZONE'],
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
            [/`/, {
                token: 'identifier.quote',
                next: '@quotedIdentifier'
            }]
        ],
        bracketedIdentifier: [
            [/[^\]]+/, 'identifier'],
            [/]]/, 'identifier'],
            [/]/, {
                token: 'identifier.quote',
                next: '@pop'
            }]
        ],
        stringDouble: [
            [/[^"]+/, 'identifier'],
            [/""/, 'identifier'],
            [/"/, {
                token: 'identifier.quote',
                next: '@pop'
            }]
        ],
        quotedIdentifier: [
            [/[^`]+/, 'identifier'],
            [/``/, 'identifier'],
            [/`/, {
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
