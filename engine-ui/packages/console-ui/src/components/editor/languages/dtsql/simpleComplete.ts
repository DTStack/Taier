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

// import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
import * as monaco from 'monaco-editor';
import DtWorker from './dtsql.worker';
import { get } from 'lodash';

let _DtParserInstance: any;
class DtParser {
    _DtParser: any;
    _eventMap: any;

    constructor () {
        this._DtParser = new DtWorker();
        this._eventMap = {};
        this._DtParser.onmessage = (e: any) => {
            const data = e.data;
            const eventId = data.eventId;
            if (this._eventMap[eventId]) {
                this._eventMap[eventId].resolve(data.result)
                this._eventMap[eventId] = null;
            }
        }
    }
    parserSql () {
        const arg = arguments;
        const eventId = this._createId();
        return new Promise((resolve: any, reject: any) => {
            this._DtParser.postMessage({
                eventId: eventId,
                type: 'parserSql',
                data: Array.from(arg)
            });
            this._eventMap[eventId] = {
                resolve,
                reject,
                arg,
                type: 'parserSql'
            }
        })
    }
    parseSyntax () {
        const arg = arguments;
        const eventId = this._createId();
        return new Promise((resolve: any, reject: any) => {
            this._DtParser.postMessage({
                eventId: eventId,
                type: 'parseSyntax',
                data: Array.from(arg)
            });
            this._eventMap[eventId] = {
                resolve,
                reject,
                arg,
                type: 'parseSyntax'
            }
        })
    }
    _createId () {
        return new Date().getTime() + '' + ~~(Math.random() * 100000)
    }
}

function loadDtParser () {
    if (!_DtParserInstance) {
        _DtParserInstance = new DtParser();
    }
    return _DtParserInstance;
}
/**
 * Select thing from table, table, table;
 * select __+ thing __+ from __+ Table (__* , __*table)* __*;
 * thing=([,.\w])
 */
// const selectRegExp = /Select\s+[\s\S]+\s+from(\s+\w+)((\s*,\s*\w+)*)\s*;/i;
let cacheKeyWords: any = [];
let _completeProvideFunc: any = {};
let _tmpDecorations: any = [];
function dtsqlWords () {
    return {
        // builtinFunctions: ["FROM_UNIXTIME", "UNIX_TIMESTAMP", "TO_DATE", "YEAR", "QUARTER", "MONTH", "DAY", "HOUR", "MINUTE", "SECOND", "WEEKOFYEAR", "DATEDIFF", "DATE_ADD", "DATE_SUB", "FROM_UTC_TIMESTAMP", "TO_UTC_TIMESTAMP", "CURRENT_DATE", "CURRENT_TIMESTAMP", "ADD_MONTHS", "LAST_DAY", "NEXT_DAY", "TRUNC", "MONTHS_BETWEEN", "DATE_FORMAT", "ROUND", "BROUND", "FLOOR", "CEIL", "RAND", "EXP", "LN", "LOG10", "LOG2", "LOG", "POW", "SQRT", "BIN", "HEX", "UNHEX", "CONV", "ABS", "PMOD", "SIN", "ASIN", "COS", "ACOS", "TAN", "ATAN", "DEGREES", "RADIANS", "POSITIVE", "NEGATIVE", "SIGN", "E", "PI", "FACTORIAL", "CBRT", "SHIFTLEFT", "SHIFTRIGHT", "SHIFTRIGHTUNSIGNED", "GREATEST", "LEAST", "ASCII", "BASE64", "CONCAT", "CHR", "CONTEXT_NGRAMS", "CONCAT_WS", "DECODE", "ENCODE", "FIND_IN_SET", "FORMAT_NUMBER", "GET_JSON_OBJECT", "IN_FILE", "INSTR", "LENGTH", "LOCATE", "LOWER", "LPAD", "LTRIM", "NGRAMS", "PARSE_URL", "PRINTF", "REGEXP_EXTRACT", "REGEXP_REPLACE", "REPEAT", "REVERSE", "RPAD", "RTRIM", "SENTENCES", "SPACE", "SPLIT", "STR_TO_MAP", "SUBSTR", "SUBSTRING_INDEX", "TRANSLATE", "TRIM", "UNBASE64", "UPPER", "INITCAP", "LEVENSHTEIN", "SOUNDEX", "SIZE", "MAP_KEYS", "MAP_VALUES", "ARRAY_CONTAINS", "SORT_ARRAY", "ROW_NUMBER"],
        // windowsFunctions: ["COUNT", "AVG", "MAX", "MIN", "STDDEV_SAMP", "SUM"],
        // innerFunctions: ["COUNT", "SUM", "AVG", "MIN", "MAX", "VARIANCE", "VAR_SAMP", "STDDEV_POP", "STDDEV_SAMP", "COVAR_POP", "COVAR_SAMP", "CORR", "PERCENTILE", "EXPLODE", "POSEXPLODE", "STACK", "JSON_TUPLE", "PARSE_URL_TUPLE", "INLINE"],
        // otherFunctions: ["IF", "NVL", "COALESCE", "ISNULL", "ISNOTNULL", "ASSERT_TRUE", "CAST", "BINARY"],
        keywords: ['SELECT', 'FROM', 'WHERE', 'UNION ALL', 'LEFT OUTER JOIN', 'RIGHT OUTER JOIN', 'FULL OUTER JOIN', 'UNION', 'INSERT', 'ADD', 'ADMIN', 'AFTER', 'ALL', 'ALTER', 'ANALYZE', 'AND', 'ARCHIVE', 'ARRAY', 'AS', 'ASC', 'AUTHORIZATION', 'BEFORE', 'BETWEEN', 'BIGINT', 'BINARY', 'BOOLEAN', 'BOTH', 'BUCKET', 'BUCKETS', 'BY', 'CASCADE', 'CASE', 'CHANGE', 'CHAR', 'CLUSTER', 'CLUSTERED', 'CLUSTERSTATUS', 'COLLECTION', 'COLUMN', 'COLUMNS', 'COMMENT', 'COMPACT', 'COMPACTIONS', 'COMPUTE', 'CONCATENATE', 'CONF', 'CONTINUE', 'CREATE', 'CROSS', 'CUBE', 'CURRENT', 'CURSOR', 'DATA', 'DATABASE', 'DATABASES', 'DATE', 'DATETIME', 'DBPROPERTIES', 'DECIMAL', 'DEFERRED', 'DEFINED', 'DELETE', 'DELIMITED', 'DEPENDENCY', 'DESC', 'DESCRIBE', 'DIRECTORIES', 'DIRECTORY', 'DISABLE', 'DISTINCT', 'DISTRIBUTE', 'DOUBLE', 'DROP', 'ELEM_TYPE', 'ELSE', 'ENABLE', 'END', 'ESCAPED', 'EXCHANGE', 'EXCLUSIVE', 'EXISTS', 'EXPLAIN', 'EXPORT', 'EXTENDED', 'EXTERNAL', 'FALSE', 'FETCH', 'FIELDS', 'FILE', 'FILEFORMAT', 'FIRST', 'FLOAT', 'FOLLOWING', 'FORMAT', 'FORMATTED', 'FULL', 'FUNCTION', 'FUNCTIONS', 'GRANT', 'GROUP', 'GROUPING', 'HAVING', 'HOLD_DDLTIME', 'IDXPROPERTIES', 'IGNORE', 'IMPORT', 'IN', 'INDEX', 'INDEXES', 'INNER', 'INPATH', 'INPUTDRIVER', 'INPUTFORMAT', 'INT', 'INTERSECT', 'INTERVAL', 'INTO', 'IS', 'ITEMS', 'JOIN', 'JAR', 'KEYS', 'KEY_TYPE', 'LATERAL', 'LEFT', 'LESS', 'LIFECYCLE', 'LIKE', 'LIMIT', 'LINES', 'LOAD', 'LOCAL', 'LOCATION', 'LOCK', 'LOCKS', 'LOGICAL', 'LONG', 'MACRO', 'MAP', 'MAPJOIN', 'MATERIALIZED', 'MINUS', 'MORE', 'MSCK', 'NOT', 'NONE', 'NOSCAN', 'NO_DROP', 'NULL', 'OF', 'OFFLINE', 'ON', 'OPTION', 'OR', 'ORC', 'ORDER', 'OUT', 'OUTER', 'OUTPUTDRIVER', 'OUTPUTFORMAT', 'OVER', 'OVERWRITE', 'OWNER', 'PARTIALSCAN', 'PARTITION', 'PARTITIONED', 'PARTITIONS', 'PERCENT', 'PARQUET', 'PLUS', 'PRECEDING', 'PRESERVE', 'PRETTY', 'PRINCIPALS', 'PROCEDURE', 'PURGE', 'RANGE', 'READ', 'READONLY', 'READS', 'REBUILD', 'RECORDREADER', 'RECORDWRITER', 'REDUCE', 'REGEXP', 'RELOAD', 'RENAME', 'REPAIR', 'REPLACE', 'RESTRICT', 'REVOKE', 'REWRITE', 'RIGHT', 'RLIKE', 'ROLE', 'ROLES', 'ROLLUP', 'ROW', 'ROWS', 'SCHEMA', 'SCHEMAS', 'SEMI', 'SERDE', 'SERDEPROPERTIES', 'SERVER', 'SET', 'SETS', 'SHARED', 'SHOW', 'SHOW_DATABASE', 'SKEWED', 'SMALLINT', 'SORT', 'SORTED', 'SSL', 'STATISTICS', 'STORED', 'STREAMTABLE', 'STRING', 'STRUCT', 'TABLE', 'TABLES', 'TABLESAMPLE', 'TBLPROPERTIES', 'TEMPORARY', 'TERMINATED', 'TEXTFILE', 'THEN', 'TIMESTAMP', 'TINYINT', 'TO', 'TOUCH', 'TRANSACTIONS', 'TRANSFORM', 'TRIGGER', 'TRUNCATE', 'TRUE', 'UNARCHIVE', 'UNBOUNDED', 'UNDO', 'UNIONTYPE', 'UNIQUEJOIN', 'UNLOCK', 'UNSET', 'UNSIGNED', 'UPDATE', 'URI', 'USE', 'USER', 'USING', 'UTC', 'UTC_TMESTAMP', 'VALUES', 'VALUE_TYPE', 'VARCHAR', 'VIEW', 'WHEN', 'WHILE', 'WINDOW', 'WITH']
    }
}
function keywordsCompleteItemCreater (words: any) {
    return words.map(
        (word: any, index: any) => {
            return {
                label: word,
                kind: monaco.languages.CompletionItemKind.Keyword,
                detail: '关键字',
                insertText: word + ' ',
                sortText: '1000' + index + word,
                filterText: word.toLowerCase()
            }
        }
    )
}
function functionsCompleteItemCreater (functions: any) {
    return functions.map(
        (functionName: any, index: any) => {
            return {
                label: functionName,
                kind: monaco.languages.CompletionItemKind.Function,
                detail: '函数',
                insertText: {
                    value: functionName + '($1) '
                },
                sortText: '2000' + index + functionName,
                filterText: functionName.toLowerCase()
            }
        }
    )
}
function customCompletionItemsCreater (_customCompletionItems: any) {
    if (!_customCompletionItems) {
        return [];
    }
    return _customCompletionItems.map(
        ([ name, detail, sortIndex, type ]: any, index: any) => {
            sortIndex = sortIndex || '3000';
            return {
                label: name,
                kind: monaco.languages.CompletionItemKind[type || 'Text'],
                detail: detail,
                insertText: {
                    value: type == 'Function' ? (name + '($1) ') : (name)
                },
                sortText: sortIndex + index + name
            }
        }
    )
}
/**
 * 创建固定的补全项，例如：keywords...
 */
function createDependencyProposals () {
    if (!cacheKeyWords.length) {
        const words: any = dtsqlWords();
        const functions: any = [].concat(words.builtinFunctions).concat(words.windowsFunctions).concat(words.innerFunctions).concat(words.otherFunctions).filter(Boolean);
        const keywords: any = [].concat(words.keywords);
        cacheKeyWords = keywordsCompleteItemCreater(keywords).concat(functionsCompleteItemCreater(functions))
    }
    return cacheKeyWords
}

monaco.languages.registerCompletionItemProvider('dtsql', {
    triggerCharacters: ['.'],
    provideCompletionItems: function (model: any, position: any, token: any, CompletionContext: any) {
        const completeItems = createDependencyProposals();
        return new Promise<any>(async (resolve: any, reject: any) => {
            const completeProvideFunc = _completeProvideFunc[model.id]
            if (completeProvideFunc) {
                const textValue = model.getValue();
                const cursorIndex = model.getOffsetAt(position);
                const dtParser = loadDtParser();
                let autoComplete = await dtParser.parserSql([textValue.substr(0, cursorIndex), textValue.substr(cursorIndex)]);
                let columnContext: any;
                let tableContext: any;
                let suggestTablesIdentifierChain = get(autoComplete, 'suggestTables.identifierChain', []);
                if (suggestTablesIdentifierChain.length) {
                    tableContext = suggestTablesIdentifierChain[0].name;
                }
                if (autoComplete.suggestColumns && autoComplete.suggestColumns.tables && autoComplete.suggestColumns.tables.length) {
                    columnContext = autoComplete.suggestColumns.tables.map(
                        (table: any) => {
                            return table.identifierChain.map((identifier: any) => {
                                return identifier.name
                            }).join('.');
                        }
                    )
                }
                completeProvideFunc(completeItems, resolve, customCompletionItemsCreater, {
                    status: 0,
                    model: model,
                    position: position,
                    word: model.getWordAtPosition(position),
                    autoComplete: autoComplete,
                    context: {
                        columnContext: columnContext,
                        tableContext: tableContext,
                        completionContext: CompletionContext
                    }
                });
            } else {
                resolve(completeItems)
            }
        });
    }
});

/**
 * 该方法提供一个注册自定义补全函数的接口
 * @param {function} completeProvideFunc
 */
export function registeCompleteItemsProvider (completeProvideFunc: any, _editor: any) {
    const id = _editor.getModel().id;
    _completeProvideFunc[id] = completeProvideFunc;
}
/**
 * 注销自定义补全函数
 */
export function disposeProvider (_editor: any) {
    if (!_editor) {
        return;
    }
    const id = _editor.getModel().id;
    _completeProvideFunc[id] = undefined;
}
export async function onChange (value = '', _editor: any, callback: any) {
    const dtParser = loadDtParser();
    const model = _editor.getModel();
    // const cursorIndex = model.getOffsetAt(_editor.getPosition());
    let autoComplete = await dtParser.parserSql(value);
    let syntax = await dtParser.parseSyntax(value.replace(/\r\n/g, '\n'));
    if (syntax && syntax.token != 'EOF') {
        const message = messageCreate(syntax);
        monaco.editor.setModelMarkers(model, model.getModeId(), [{
            startLineNumber: syntax.loc.first_line,
            startColumn: syntax.loc.first_column + 1,
            endLineNumber: syntax.loc.last_line,
            endColumn: syntax.loc.last_column + 1,
            message: `[语法错误！] \n${message}`,
            severity: 8
        }])
        _tmpDecorations = _editor.deltaDecorations(_tmpDecorations, createLineMarker(syntax))
    } else {
        _editor.deltaDecorations(_tmpDecorations, [])
        monaco.editor.setModelMarkers(model, model.getModeId(), [])
    }
    if (callback) {
        callback(autoComplete, syntax);
    }
    console.log(syntax)
}

function createLineMarker (syntax: any) {
    return [{
        range: new monaco.Range(syntax.loc.first_line, 1, syntax.loc.last_line, 1),
        options: {
            isWholeLine: true,
            // linesDecorationsClassName: 'dt-monaco-line-error' ,
            className: 'dt-monaco-whole-line-error'
        }
    }]
}

function messageCreate (syntax: any) {
    let expected = syntax.expected || [];
    if (expected.length) {
        return `您可能想输入是${expected.map(
            (item: any) => {
                return ` '${item.text}'`
            }
        ).filter((value: any, index: any) => { return index < 20 }).join(',')}?`
    } else {
        return '请检查您的语法！'
    }
}
