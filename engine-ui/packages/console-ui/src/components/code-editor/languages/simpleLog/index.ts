import 'codemirror/addon/mode/simple';
const codemirror = require('codemirror')

codemirror.defineSimpleMode('simpleLog', {
    start: [
        {
            regex: /^[=]+[^=]*[=]+/,
            token: 'strong'
        },
        {
            regex: /([^\w])([A-Z][\w]*)/,
            token: [null, 'string']
        },
        {
            regex: /(^[A-Z][\w]*)/,
            token: 'string'
        }
        // {
        //     regex: /([^\d])([0-9]+)/,
        //     token: [null, 'comment']
        // },
        // {
        //     regex: /(^[0-9]+)/,
        //     token: 'comment'
        // }
    ]
});
