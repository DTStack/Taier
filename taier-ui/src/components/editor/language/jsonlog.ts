import * as monaco from '@dtinsight/molecule/esm/monaco';

const jsonlog = 'jsonlog';

// register json log for view json data with log informations
monaco.languages.register({ id: jsonlog });
monaco.languages.setMonarchTokensProvider(jsonlog, {
    tokenizer: {
        root: [
            // split
            [/^===========.*/, { token: 'comment' }],
            [/[;,.]/, 'delimiter'],

            // string
            [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],

            // numbers
            [/\d*\.\d+([eE][-+]?\d+)?/, 'number.float'],
            [/0[xX][0-9a-fA-F]+/, 'number.hex'],
            [/\d+.*/, 'number'],

            // boolean
            [/false|true/, 'keyword'],

            // chinese character
            [/[\u4e00-\u9fa5]+/, 'keyword'],

            // error message
            [/<error.*/, 'error-token'],
            [/<warning.*/, 'warn-token'],
            [/<info.*/, 'info-token'],
            [/\[[a-zA-Z 0-9:]+\]/, 'comment'],
        ],
        string: [
            [/[^\\"]+/, 'string'],
            [/\\./, 'string.escape.invalid'],
            [/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }],
        ],
    },
});
