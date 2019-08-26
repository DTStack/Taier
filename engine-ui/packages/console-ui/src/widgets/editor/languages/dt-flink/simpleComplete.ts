// import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
import * as monaco from 'monaco-editor';

let cacheKeyWords: any = [];
let _completeProvideFunc: any = {};

function dtsqlWords (): {
    builtinFunctions?: string[];
    windowsFunctions?: string[];
    innerFunctions?: string[];
    otherFunctions?: string[];
    keywords?: string[]; } {
    return {
        // builtinFunctions: ['FROM_UNIXTIME', 'UNIX_TIMESTAMP', 'TO_DATE', 'YEAR', 'QUARTER', 'MONTH', 'DAY', 'HOUR', 'MINUTE', 'SECOND', 'WEEKOFYEAR', 'DATEDIFF', 'DATE_ADD', 'DATE_SUB', 'FROM_UTC_TIMESTAMP', 'TO_UTC_TIMESTAMP', 'CURRENT_DATE', 'CURRENT_TIMESTAMP', 'ADD_MONTHS', 'LAST_DAY', 'NEXT_DAY', 'TRUNC', 'MONTHS_BETWEEN', 'DATE_FORMAT', 'ROUND', 'BROUND', 'FLOOR', 'CEIL', 'RAND', 'EXP', 'LN', 'LOG10', 'LOG2', 'LOG', 'POW', 'SQRT', 'BIN', 'HEX', 'UNHEX', 'CONV', 'ABS', 'PMOD', 'SIN', 'ASIN', 'COS', 'ACOS', 'TAN', 'ATAN', 'DEGREES', 'RADIANS', 'POSITIVE', 'NEGATIVE', 'SIGN', 'E', 'PI', 'FACTORIAL', 'CBRT', 'SHIFTLEFT', 'SHIFTRIGHT', 'SHIFTRIGHTUNSIGNED', 'GREATEST', 'LEAST', 'ASCII', 'BASE64', 'CONCAT', 'CHR', 'CONTEXT_NGRAMS', 'CONCAT_WS', 'DECODE', 'ENCODE', 'FIND_IN_SET', 'FORMAT_NUMBER', 'GET_JSON_OBJECT', 'IN_FILE', 'INSTR', 'LENGTH', 'LOCATE', 'LOWER', 'LPAD', 'LTRIM', 'NGRAMS', 'PARSE_URL', 'PRINTF', 'REGEXP_EXTRACT', 'REGEXP_REPLACE', 'REPEAT', 'REVERSE', 'RPAD', 'RTRIM', 'SENTENCES', 'SPACE', 'SPLIT', 'STR_TO_MAP', 'SUBSTR', 'SUBSTRING_INDEX', 'TRANSLATE', 'TRIM', 'UNBASE64', 'UPPER', 'INITCAP', 'LEVENSHTEIN', 'SOUNDEX', 'SIZE', 'MAP_KEYS', 'MAP_VALUES', 'ARRAY_CONTAINS', 'SORT_ARRAY', 'ROW_NUMBER'],
        // windowsFunctions: ['COUNT', 'AVG', 'MAX', 'MIN', 'STDDEV_SAMP', 'SUM'],
        // innerFunctions: ['COUNT', 'AVG', 'MIN', 'MAX', 'VARIANCE', 'VAR_SAMP', 'STDDEV_POP', 'STDDEV_SAMP', 'COVAR_POP', 'COVAR_SAMP', 'CORR', 'PERCENTILE', 'EXPLODE', 'POSEXPLODE', 'STACK', 'JSON_TUPLE', 'PARSE_URL_TUPLE', 'INLINE'],
        // otherFunctions: ['IF', 'NVL', 'COALESCE', 'ISNULL', 'ISNOTNULL', 'ASSERT_TRUE', 'CAST', 'BINARY'],
        keywords: ['A', 'ABS', 'ABSOLUTE', 'ACTION', 'ADA', 'ADD', 'ADMIN', 'AFTER', 'ALL', 'ALLOCATE', 'ALLOW', 'ALTER', 'ALWAYS', 'AND', 'ANY', 'ARE', 'ARRAY', 'AS', 'ASC', 'ASENSITIVE', 'ASSERTION', 'ASSIGNMENT', 'ASYMMETRIC', 'AT', 'ATOMIC', 'ATTRIBUTE', 'ATTRIBUTES', 'AUTHORIZATION', 'AVG', 'BEFORE', 'BEGIN', 'BERNOULLI', 'BETWEEN', 'BIGINT', 'BINARY', 'BIT', 'BLOB', 'BOOLEAN', 'BOTH', 'BREADTH', 'BY', 'C', 'CALL', 'CALLED', 'CARDINALITY', 'CASCADE', 'CASCADED', 'CASE', 'CAST', 'CATALOG', 'CATALOG_NAME', 'CEIL', 'CEILING', 'CENTURY', 'CHAIN', 'CHAR', 'CHARACTER', 'CHARACTERISTICS', 'CHARACTERS', 'CHARACTER_LENGTH', 'CHARACTER_SET_CATALOG', 'CHARACTER_SET_NAME', 'CHARACTER_SET_SCHEMA', 'CHAR_LENGTH', 'CHECK', 'CLASS_ORIGIN', 'CLOB', 'CLOSE', 'COALESCE', 'COBOL', 'COLLATE', 'COLLATION', 'COLLATION_CATALOG', 'COLLATION_NAME', 'COLLATION_SCHEMA', 'COLLECT', 'COLUMN', 'COLUMN_NAME', 'COMMAND_FUNCTION', 'COMMAND_FUNCTION_CODE', 'COMMIT', 'COMMITTED', 'CONDITION', 'CONDITION_NUMBER', 'CONNECT', 'CONNECTION', 'CONNECTION_NAME', 'CONSTRAINT', 'CONSTRAINTS', 'CONSTRAINT_CATALOG', 'CONSTRAINT_NAME', 'CONSTRAINT_SCHEMA', 'CONSTRUCTOR', 'CONTAINS', 'CONTINUE', 'CONVERT', 'CORR', 'CORRESPONDING', 'COUNT', 'COVAR_POP', 'COVAR_SAMP', 'CREATE', 'CROSS', 'CUBE', 'CUME_DIST', 'CURRENT', 'CURRENT_CATALOG', 'CURRENT_DATE', 'CURRENT_DEFAULT_TRANSFORM_GROUP', 'CURRENT_PATH', 'CURRENT_ROLE', 'CURRENT_SCHEMA', 'CURRENT_TIME', 'CURRENT_TIMESTAMP', 'CURRENT_TRANSFORM_GROUP_FOR_TYPE', 'CURRENT_USER', 'CURSOR', 'CURSOR_NAME', 'CYCLE', 'DATA', 'DATABASE', 'DATE', 'DATETIME_INTERVAL_CODE', 'DATETIME_INTERVAL_PRECISION', 'DAY', 'DEALLOCATE', 'DEC', 'DECADE', 'DECIMAL', 'DECLARE', 'DEFAULT', 'DEFAULTS', 'DEFERRABLE', 'DEFERRED', 'DEFINED', 'DEFINER', 'DEGREE', 'DELETE', 'DENSE_RANK', 'DEPTH', 'DEREF', 'DERIVED', 'DESC', 'DESCRIBE', 'DESCRIPTION', 'DESCRIPTOR', 'DETERMINISTIC', 'DIAGNOSTICS', 'DISALLOW', 'DISCONNECT', 'DISPATCH', 'DISTINCT', 'DOMAIN', 'DOUBLE', 'DOW', 'DOY', 'DROP', 'DYNAMIC', 'DYNAMIC_FUNCTION', 'DYNAMIC_FUNCTION_CODE', 'EACH', 'ELEMENT', 'ELSE', 'END', 'END', '-', 'EXEC', 'EPOCH', 'EQUALS', 'ESCAPE', 'EVERY', 'EXCEPT', 'EXCEPTION', 'EXCLUDE', 'EXCLUDING', 'EXEC', 'EXECUTE', 'EXISTS', 'EXP', 'EXPLAIN', 'EXTEND', 'EXTERNAL', 'EXTRACT', 'FALSE', 'FETCH', 'FILTER', 'FINAL', 'FIRST', 'FIRST_VALUE', 'FLOAT', 'FLOOR', 'FOLLOWING', 'FOR', 'FOREIGN', 'FORTRAN', 'FOUND', 'FRAC_SECOND', 'FREE', 'FROM', 'FULL', 'FUNCTION', 'FUSION', 'G', 'GENERAL', 'GENERATED', 'GET', 'GLOBAL', 'GO', 'GOTO', 'GRANT', 'GRANTED', 'GROUP', 'GROUPING', 'HAVING', 'HIERARCHY', 'HOLD', 'HOUR', 'IDENTITY', 'IMMEDIATE', 'IMPLEMENTATION', 'IMPORT', 'IN', 'INCLUDING', 'INCREMENT', 'INDICATOR', 'INITIALLY', 'INNER', 'INOUT', 'INPUT', 'INSENSITIVE', 'INSERT', 'INSTANCE', 'INSTANTIABLE', 'INT', 'INTEGER', 'INTERSECT', 'INTERSECTION', 'INTERVAL', 'INTO', 'INVOKER', 'IS', 'ISOLATION', 'JAVA', 'JOIN', 'K', 'KEY', 'KEY_MEMBER', 'KEY_TYPE', 'LABEL', 'LANGUAGE', 'LARGE', 'LAST', 'LAST_VALUE', 'LATERAL', 'LEADING', 'LEFT', 'LENGTH', 'LEVEL', 'LIBRARY', 'LIKE', 'LIMIT', 'LN', 'LOCAL', 'LOCALTIME', 'LOCALTIMESTAMP', 'LOCATOR', 'LOWER', 'M', 'MAP', 'MATCH', 'MATCHED', 'MAX', 'MAXVALUE', 'MEMBER', 'MERGE', 'MESSAGE_LENGTH', 'MESSAGE_OCTET_LENGTH', 'MESSAGE_TEXT', 'METHOD', 'MICROSECOND', 'MILLENNIUM', 'MIN', 'MINUTE', 'MINVALUE', 'MOD', 'MODIFIES', 'MODULE', 'MONTH', 'MORE', 'MULTISET', 'MUMPS', 'NAME', 'NAMES', 'NATIONAL', 'NATURAL', 'NCHAR', 'NCLOB', 'NESTING', 'NEW', 'NEXT', 'NO', 'NONE', 'NORMALIZE', 'NORMALIZED', 'NOT', 'NULL', 'NULLABLE', 'NULLIF', 'NULLS', 'NUMBER', 'NUMERIC', 'OBJECT', 'OCTETS', 'OCTET_LENGTH', 'OF', 'OFFSET', 'OLD', 'ON', 'ONLY', 'OPEN', 'OPTION', 'OPTIONS', 'OR', 'ORDER', 'ORDERING', 'ORDINALITY', 'OTHERS', 'OUT', 'OUTER', 'OUTPUT', 'OVER', 'OVERLAPS', 'OVERLAY', 'OVERRIDING', 'PAD', 'PARAMETER', 'PARAMETER_MODE', 'PARAMETER_NAME', 'PARAMETER_ORDINAL_POSITION', 'PARAMETER_SPECIFIC_CATALOG', 'PARAMETER_SPECIFIC_NAME', 'PARAMETER_SPECIFIC_SCHEMA', 'PARTIAL', 'PARTITION', 'PASCAL', 'PASSTHROUGH', 'PATH', 'PERCENTILE_CONT', 'PERCENTILE_DISC', 'PERCENT_RANK', 'PLACING', 'PLAN', 'PLI', 'POSITION', 'POWER', 'PRECEDING', 'PRECISION', 'PREPARE', 'PRESERVE', 'PRIMARY', 'PRIOR', 'PRIVILEGES', 'PROCEDURE', 'PUBLIC', 'QUARTER', 'RANGE', 'RANK', 'READ', 'READS', 'REAL', 'RECURSIVE', 'REF', 'REFERENCES', 'REFERENCING', 'REGR_AVGX', 'REGR_AVGY', 'REGR_COUNT', 'REGR_INTERCEPT', 'REGR_R2', 'REGR_SLOPE', 'REGR_SXX', 'REGR_SXY', 'REGR_SYY', 'RELATIVE', 'RELEASE', 'REPEATABLE', 'RESET', 'RESTART', 'RESTRICT', 'RESULT', 'RETURN', 'RETURNED_CARDINALITY', 'RETURNED_LENGTH', 'RETURNED_OCTET_LENGTH', 'RETURNED_SQLSTATE', 'RETURNS', 'REVOKE', 'RIGHT', 'ROLE', 'ROLLBACK', 'ROLLUP', 'ROUTINE', 'ROUTINE_CATALOG', 'ROUTINE_NAME', 'ROUTINE_SCHEMA', 'ROW', 'ROWS', 'ROW_COUNT', 'ROW_NUMBER', 'SAVEPOINT', 'SCALE', 'SCHEMA', 'SCHEMA_NAME', 'SCOPE', 'SCOPE_CATALOGS', 'SCOPE_NAME', 'SCOPE_SCHEMA', 'SCROLL', 'SEARCH', 'SECOND', 'SECTION', 'SECURITY', 'SELECT', 'SELF', 'SENSITIVE', 'SEQUENCE', 'SERIALIZABLE', 'SERVER', 'SERVER_NAME', 'SESSION', 'SESSION_USER', 'SET', 'SETS', 'SIMILAR', 'SIMPLE', 'SIZE', 'SMALLINT', 'SOME', 'SOURCE', 'SPACE', 'SPECIFIC', 'SPECIFICTYPE', 'SPECIFIC_NAME', 'SQL', 'SQLEXCEPTION', 'SQLSTATE', 'SQLWARNING', 'SQL_TSI_DAY', 'SQL_TSI_FRAC_SECOND', 'SQL_TSI_HOUR', 'SQL_TSI_MICROSECOND', 'SQL_TSI_MINUTE', 'SQL_TSI_MONTH', 'SQL_TSI_QUARTER', 'SQL_TSI_SECOND', 'SQL_TSI_WEEK', 'SQL_TSI_YEAR', 'SQRT', 'START', 'STATE', 'STATEMENT', 'STATIC', 'STDDEV_POP', 'STDDEV_SAMP', 'STREAM', 'STRUCTURE', 'STYLE', 'SUBCLASS_ORIGIN', 'SUBMULTISET', 'SUBSTITUTE', 'SUBSTRING', 'SYMMETRIC', 'SYSTEM', 'SYSTEM_USER', 'TABLE', 'TABLESAMPLE', 'TABLE_NAME', 'TEMPORARY', 'THEN', 'TIES', 'TIME', 'TIMESTAMP', 'TIMESTAMPADD', 'TIMESTAMPDIFF', 'TIMEZONE_HOUR', 'TIMEZONE_MINUTE', 'TINYINT', 'TO', 'TOP_LEVEL_COUNT', 'TRAILING', 'TRANSACTION', 'TRANSACTIONS_ACTIVE', 'TRANSACTIONS_COMMITTED', 'TRANSACTIONS_ROLLED_BACK', 'TRANSFORM', 'TRANSFORMS', 'TRANSLATE', 'TRANSLATION', 'TREAT', 'TRIGGER', 'TRIGGER_CATALOG', 'TRIGGER_NAME', 'TRIGGER_SCHEMA', 'TRIM', 'TRUE', 'TYPE', 'UESCAPE', 'UNBOUNDED', 'UNCOMMITTED', 'UNDER', 'UNION', 'UNIQUE', 'UNKNOWN', 'UNNAMED', 'UNNEST', 'UPDATE', 'UPPER', 'UPSERT', 'USAGE', 'USER', 'USER_DEFINED_TYPE_CATALOG', 'USER_DEFINED_TYPE_CODE', 'USER_DEFINED_TYPE_NAME', 'USER_DEFINED_TYPE_SCHEMA', 'USING', 'VALUE', 'VALUES', 'VARBINARY', 'VARCHAR', 'VARYING', 'VAR_POP', 'VAR_SAMP', 'VERSION', 'VIEW', 'WEEK', 'WHEN', 'WHENEVER', 'WHERE', 'WIDTH_BUCKET', 'WINDOW', 'WITH', 'WITHIN', 'WITHOUT', 'WORK', 'WRAPPER', 'WRITE', 'XML', 'YEAR', 'ZONE']
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
// eslint-disable-next-line @typescript-eslint/no-unused-vars
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
function createDependencyProposals () {
    if (!cacheKeyWords.length) {
        const words = dtsqlWords();
        const functions: any = [].concat(words.builtinFunctions).concat(words.windowsFunctions).concat(words.innerFunctions).concat(words.otherFunctions).filter(Boolean);
        const keywords: any = [].concat(words.keywords);
        cacheKeyWords = keywordsCompleteItemCreater(keywords).concat(functionsCompleteItemCreater(functions))
    }
    return cacheKeyWords
}

monaco.languages.registerCompletionItemProvider('dtflink', {
    provideCompletionItems: function (model: any, position: any, token: any, CompletionContext: any) {
        const completeItems = createDependencyProposals();
        return new Promise<any>(async (resolve: any, reject: any) => {
            const completeProvideFunc = _completeProvideFunc[model.id]
            if (completeProvideFunc) {
                completeProvideFunc(completeItems, resolve, customCompletionItemsCreater, {
                    status: 0,
                    model: model,
                    position: position
                });
            } else {
                resolve(completeItems);
            }
        });
    }
});
export function registeCompleteItemsProvider (completeProvideFunc: any, _editor: any) {
    const id = _editor.getModel().id;
    _completeProvideFunc[id] = completeProvideFunc;
}
export function disposeProvider (_editor: any) {
    if (!_editor) {
        return;
    }
    const id = _editor.getModel().id;
    _completeProvideFunc[id] = undefined;
}
