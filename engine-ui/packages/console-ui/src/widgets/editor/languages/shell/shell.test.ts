/* ---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *-------------------------------------------------------------------------------------------- */
'use strict';
import { testTokenization } from '../test/testRunner';
testTokenization('shell', [
    // Keywords
    [{
        line: 'if while',
        tokens: [
            { startIndex: 0, type: 'keyword.shell' },
            { startIndex: 2, type: 'white.shell' },
            { startIndex: 3, type: 'keyword.shell' }
        ]
    }],
    // Predefined & attribute
    [{
        line: 'ps -aux | grep code',
        tokens: [
            { startIndex: 0, type: 'type.identifier.shell' },
            { startIndex: 2, type: 'white.shell' },
            { startIndex: 3, type: 'attribute.name.shell' },
            { startIndex: 7, type: 'white.shell' },
            { startIndex: 8, type: 'delimiter.shell' },
            { startIndex: 9, type: 'white.shell' },
            { startIndex: 10, type: 'type.identifier.shell' },
            { startIndex: 14, type: 'white.shell' },
            { startIndex: 15, type: '' }
        ]
    }],
    [{
        line: '# comment',
        tokens: [
            { startIndex: 0, type: 'comment.shell' }
        ]
    }, {
        line: 'cd tree',
        tokens: [
            { startIndex: 0, type: 'type.identifier.shell' },
            { startIndex: 2, type: 'white.shell' },
            { startIndex: 3, type: '' }
        ]
    }],
    // Shebang
    [{
        line: '#!/bin/env bash',
        tokens: [
            { startIndex: 0, type: 'metatag.shell' }
        ]
    }],
    // Comments
    [{
        line: '#',
        tokens: [
            { startIndex: 0, type: 'comment.shell' }
        ]
    }],
    [{
        line: '# a comment',
        tokens: [
            { startIndex: 0, type: 'comment.shell' }
        ]
    }],
    [{
        line: '    # a comment',
        tokens: [
            { startIndex: 0, type: 'white.shell' },
            { startIndex: 4, type: 'comment.shell' }
        ]
    }],
    // numbers
    [{
        line: '0',
        tokens: [
            { startIndex: 0, type: 'number.shell' }
        ]
    }],
    [{
        line: '0.0',
        tokens: [
            { startIndex: 0, type: 'number.float.shell' }
        ]
    }],
    [{
        line: '0x123',
        tokens: [
            { startIndex: 0, type: 'number.hex.shell' }
        ]
    }],
    [{
        line: '23.5',
        tokens: [
            { startIndex: 0, type: 'number.float.shell' }
        ]
    }],
    [{
        line: '23.5e3',
        tokens: [
            { startIndex: 0, type: 'number.float.shell' }
        ]
    }],
    [{
        line: '23.5E3',
        tokens: [
            { startIndex: 0, type: 'number.float.shell' }
        ]
    }],
    [{
        line: '1.72e-3',
        tokens: [
            { startIndex: 0, type: 'number.float.shell' }
        ]
    }],
    [{
        line: '0+0',
        tokens: [
            { startIndex: 0, type: 'number.shell' },
            { startIndex: 1, type: 'delimiter.shell' },
            { startIndex: 2, type: 'number.shell' }
        ]
    }],
    [{
        line: '100+10',
        tokens: [
            { startIndex: 0, type: 'number.shell' },
            { startIndex: 3, type: 'delimiter.shell' },
            { startIndex: 4, type: 'number.shell' }
        ]
    }],
    [{
        line: '0 + 0',
        tokens: [
            { startIndex: 0, type: 'number.shell' },
            { startIndex: 1, type: 'white.shell' },
            { startIndex: 2, type: 'delimiter.shell' },
            { startIndex: 3, type: 'white.shell' },
            { startIndex: 4, type: 'number.shell' }
        ]
    }],
    // Strings
    [{
        line: "'test string'",
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }],
    [{
        line: '"test string"',
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }],
    [{
        line: "'test",
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }, {
        line: '',
        tokens: []
    }, {
        line: "string'",
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }],
    [{
        line: '"test',
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }, {
        line: '',
        tokens: []
    }, {
        line: 'string"',
        tokens: [
            { startIndex: 0, type: 'string.shell' }
        ]
    }],
    // Parameters
    [{
        line: '$1',
        tokens: [
            { startIndex: 0, type: 'variable.predefined.shell' }
        ]
    }],
    [{
        line: '$a',
        tokens: [
            { startIndex: 0, type: 'variable.shell' }
        ]
    }],
    [{
        line: '${string:position}',
        tokens: [
            { startIndex: 0, type: 'variable.shell' },
            { startIndex: 8, type: 'delimiter.shell' },
            { startIndex: 9, type: 'variable.shell' }
        ]
    }],
    [{
        line: '$(pwd)',
        tokens: [
            { startIndex: 0, type: 'variable.shell' }
        ]
    }],
    [{
        line: 'echo $hello | less',
        tokens: [
            { startIndex: 0, type: 'type.identifier.shell' },
            { startIndex: 4, type: 'white.shell' },
            { startIndex: 5, type: 'variable.shell' },
            { startIndex: 11, type: 'white.shell' },
            { startIndex: 12, type: 'delimiter.shell' },
            { startIndex: 13, type: 'white.shell' },
            { startIndex: 14, type: '' }
        ]
    }],
    // HereDoc
    [{
        line: '<< word',
        tokens: [
            { startIndex: 0, type: 'constants.shell' },
            { startIndex: 2, type: 'white.shell' },
            { startIndex: 3, type: 'string.heredoc.shell' }
        ]
    }],
    [{
        line: '<<- "word"',
        tokens: [
            { startIndex: 0, type: 'constants.shell' },
            { startIndex: 3, type: 'white.shell' },
            { startIndex: 4, type: 'string.heredoc.delimiter.shell' },
            { startIndex: 5, type: 'string.heredoc.shell' },
            { startIndex: 9, type: 'string.heredoc.delimiter.shell' }
        ]
    }],
    [{
        line: '<<< word',
        tokens: [
            { startIndex: 0, type: 'constants.shell' },
            { startIndex: 3, type: 'white.shell' },
            { startIndex: 4, type: 'string.heredoc.shell' }
        ]
    }]
]);
