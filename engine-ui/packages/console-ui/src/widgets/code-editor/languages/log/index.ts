import 'codemirror/addon/mode/simple';
const codemirror = require('codemirror')

const startRegex: any = [
    {
        regex: /(\[.*?\])([ \t]*)(<error>)/,
        token: ['tag', null, 'error.strong'],
        sol: true,
        next: 'error'
    },
    {
        regex: /(\[.*?\])([ \t]*)(<info>)/,
        token: ['tag', null, 'bracket'],
        sol: true,
        next: 'info'
    },
    {
        regex: /(\[.*?\])([ \t]*)(<warning>)/,
        token: ['tag', null, 'comment'],
        sol: true,
        next: 'warning'
    }
]

codemirror.defineSimpleMode('dtlog', {
    start: [
        ...startRegex,
        {
            regex: /.*/,
            token: 'hr'
        }
    ],
    error: [
        ...startRegex,
        {
            regex: /.*/,
            token: 'error.strong'
        }
    ],
    info: [
        ...startRegex,
        {
            regex: /.*/,
            token: 'bracket'
        }
    ],
    warning: [
        ...startRegex,
        {
            regex: /.*/,
            token: 'comment'
        }
    ]
});
