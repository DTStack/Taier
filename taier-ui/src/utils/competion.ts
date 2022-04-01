/* eslint-disable no-template-curly-in-string */
// competion for monaco-editor
import { languages } from '@dtinsight/molecule/esm/monaco';

export const Keywords = (range: languages.CompletionItem['range']): languages.CompletionItem[] => [
	{
		label: 'SELECT',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The SELECT statement is used to select data from a database.',
		insertText: 'SELECT',
		range,
	},
	{
		label: 'CREATE TABLE',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The CREATE TABLE statement is used to create a new table in a database.',
		insertText: 'CREATE TABLE',
		range,
	},
	{
		label: 'WHERE',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The WHERE clause is used to filter records.',
		insertText: 'WHERE',
		range,
	},
	{
		label: 'AND',
		kind: languages.CompletionItemKind.Operator,
		documentation:
			'The AND operator is used to filter records based on more than one condition, which displays a record if all the conditions separated by AND are TRUE.',
		insertText: 'AND',
		range,
	},
	{
		label: 'OR',
		kind: languages.CompletionItemKind.Operator,
		documentation:
			'The OR operator is used to filter records based on more than one condition, which displays a record if any of the conditions separated by OR is TRUE.',
		insertText: 'OR',
		range,
	},
	{
		label: 'NOT',
		kind: languages.CompletionItemKind.Operator,
		documentation: 'The NOT operator displays a record if the condition(s) is NOT TRUE.',
		insertText: 'NOT',
		range,
	},
	{
		label: 'ORDER BY',
		kind: languages.CompletionItemKind.Keyword,
		documentation:
			'The ORDER BY keyword is used to sort the result-set in ascending or descending order. The ORDER BY keyword sorts the records in ascending order by default. To sort the records in descending order, use the DESC keyword.',
		insertText: 'ORDER BY',
		range,
	},
	{
		label: 'INSERT INTO',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The INSERT INTO statement is used to insert new records in a table.',
		insertText: 'INSERT INTO',
		range,
	},
	{
		label: 'NULL',
		kind: languages.CompletionItemKind.Value,
		documentation: 'A field with a NULL value is a field with no value.',
		insertText: 'NULL',
		range,
	},
	{
		label: 'UPDATE',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The UPDATE statement is used to modify the existing records in a table.',
		insertText: 'UPDATE',
		range,
	},
	{
		label: 'DELETE',
		kind: languages.CompletionItemKind.Keyword,
		documentation: 'The DELETE statement is used to modify the existing records in a table.',
		insertText: 'DELETE',
		range,
	},
];

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
