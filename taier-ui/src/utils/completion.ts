import { languages } from '@dtinsight/molecule/esm/monaco';

interface ICompletionProps {
    keyword: string;
    description: string;
}

let keywords: ICompletionProps[] = [];
let functions: ICompletionProps[] = [];
export const Keywords = async (range: languages.CompletionItem['range']): Promise<languages.CompletionItem[]> => {
    if (!keywords.length || !functions.length) {
        return Promise.all([
            fetch('./assets/keywords.json', { method: 'get' }).then<ICompletionProps[]>((res) => res.json()),
            fetch('./assets/functions.json', { method: 'get' }).then<ICompletionProps[]>((res) => res.json()),
        ])
            .then(([keywordList, functionList]) => {
                keywords = keywordList;
                functions = functionList;
            })
            .then(() => {
                return [
                    ...keywords.map((keyword) => ({
                        label: keyword.keyword,
                        kind: languages.CompletionItemKind.Keyword,
                        documentation: keyword.description,
                        insertText: keyword.keyword,
                        range,
                    })),
                    ...functions.map((fn) => ({
                        label: fn.keyword,
                        kind: languages.CompletionItemKind.Function,
                        documentation: fn.description,
                        insertText: fn.keyword,
                        range,
                    })),
                    {
                        label: 'NULL',
                        kind: languages.CompletionItemKind.Value,
                        documentation: 'A field with a NULL value is a field with no value.',
                        insertText: 'NULL',
                        range,
                    },
                ];
            });
    }

    return [
        ...keywords.map((keyword) => ({
            label: keyword.keyword,
            kind: languages.CompletionItemKind.Keyword,
            documentation: keyword.description,
            insertText: keyword.keyword,
            range,
        })),
        ...functions.map((fn) => ({
            label: fn.keyword,
            kind: languages.CompletionItemKind.Function,
            documentation: fn.description,
            insertText: fn.keyword,
            range,
        })),
        {
            label: 'NULL',
            kind: languages.CompletionItemKind.Value,
            documentation: 'A field with a NULL value is a field with no value.',
            insertText: 'NULL',
            range,
        },
    ];
};

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
