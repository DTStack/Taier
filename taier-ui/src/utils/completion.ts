import { languages } from '@dtinsight/molecule/esm/monaco';

export const Snippets = (range: languages.CompletionItem['range']): languages.CompletionItem[] => [
    {
        label: 'INSERT:snippet',
        kind: languages.CompletionItemKind.Snippet,
        documentation: 'The INSERT INTO statement is used to insert new records in a table.',
        insertText: 'INSERT INTO ${1:tableName} VALUES(${2:value});',
        insertTextRules: languages.CompletionItemInsertTextRule.InsertAsSnippet,
        range,
    },
    {
        label: 'CREATE:snippet',
        kind: languages.CompletionItemKind.Snippet,
        documentation: 'The CREATE TABLE statement is used to create a new table in a database.',
        insertText: 'CREATE TABLE ${1:table_name} (\n\t${2:column} ${3:datatype}\n);',
        insertTextRules: languages.CompletionItemInsertTextRule.InsertAsSnippet,
        range,
    },
    {
        label: 'SELECT:snippet',
        kind: languages.CompletionItemKind.Snippet,
        documentation: 'The SELECT statement is used to select data from a database.',
        insertText: 'SELECT ${1:column} FROM ${2:table_name};',
        insertTextRules: languages.CompletionItemInsertTextRule.InsertAsSnippet,
        range,
    },
    {
        label: 'SHOW:snippet',
        kind: languages.CompletionItemKind.Snippet,
        documentation: 'show tables;',
        insertText: 'show tables;',
        insertTextRules: languages.CompletionItemInsertTextRule.InsertAsSnippet,
        range,
    },
];
