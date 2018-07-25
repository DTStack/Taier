import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';

function dtsqlWords() {
    return {
        timeFunctions: ["YEAR", "QUARTER", "MONTH", "DAY", "DAYOFMONTH", "HOUR", "MINUTE", "SECOND", "FROM_UTC_TIMESTAMP", "CURRENT_TIMESTAMP", "ADD_MONTHS", "LAST_DAY", "NEXT_DAY", "MONTHS_BETWEEN"],
        mathFunctions: ["LOG2", "LOG10", "BIN", "HEX", "UNHEX", "RADIANS", "DEGREES", "SIGN", "E", "PI", "FACTORIAL", "CBRT", "SHIFTLEFT", "SHIFTRIGHT", "SHIFTRIGHTUNSIGNED"],
        charFunctions: ["CONCAT_WS", "LPAD", "RPAD", "REPLACE", "SOUNDEX", "SUBSTRING_INDEX"],
        innerFunctions: ["COLLECT_LIST", "COLLECT_SET", "SORT_ARRAY", "POSEXPLODE", "STRUCT", "NAMED_STRUCT", "INLINE", "COVAR_POP", "COVAR_SAMP", "VAR_SAMP", "VAR_POP"],
        keyWord: ["WITH", "SERDEPROPERTIES", "WITH SERDEPROPERTIES", "ANTI", "SEMI", "LEFT ANTI JOIN", "LEFT SEMI JOIN", "RIGHT ANTI JOIN", "RIGHT SEMI JOIN", "INNER", "VALUES", "LIST", "RESOURCES", "KILL", "WAIT", "LS", "USING", "STATUS", "SETPROJECT", "ALIAS"],
        constKeyWord: ["(WHERE|IF)\\s+EXISTS", "LATERAL\\s+VIEW", "NOT\\s+EXISTS", "PARTITIONED\\s+BY", "ADD", "ALL", "ALTER", "AND", "AS", "ASC", "BETWEEN", "BIGINT", "BOOLEAN", "BY", "CASE", "CAST", "COLUMN", "COMMENT", "CREATE", "DESC", "DISTINCT", "DISTRIBUTE", "DOUBLE", "DROP", "ELSE", "FALSE", "FROM", "FULL", "GROUP", "IF", "IN", "INSERT", "INTO", "IS", "JOIN", "LEFT", "LIFECYCLE", "LIKE", "LIMIT", "MAPJOIN", "NOT", "NULL", "ON", "OR", "ORDER", "OUTER", "OVERWRITE", "PARTITION", "RENAME", "REPLACE", "RIGHT", "RLIKE", "SELECT", "SORT", "STRING", "TABLE", "THEN", "TOUCH", "TRUE", "UNION", "VIEW", "WHEN", "WHERE", "HAVING", "END", "OVER"],
        dualRoleKey: ["VALUES", "EXISTS"]
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
    const functions = [].concat(words.timeFunctions).concat(words.mathFunctions).concat(words.charFunctions).concat(words.innerFunctions);
    const keywords = [].concat(words.keyWord).concat(words.constKeyWord).concat(words.dualRoleKey);
    return keywordsCompleteItemCreater(keywords).concat(functionsCompleteItemCreater(functions))
}

monaco.languages.registerCompletionItemProvider("dtsql", {
    provideCompletionItems: function (model, position) {
        return createDependencyProposals();
    }
});
