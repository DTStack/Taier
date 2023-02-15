module.exports = {
    editor: {
        create: jest.fn(),
        setModelLanguage: jest.fn(),
        editorInstance: {
            getSelections: jest
                .fn()
                .mockImplementationOnce(() => undefined)
                .mockImplementationOnce(() => [
                    {
                        startLineNumber: 0,
                        endLineNumber: 0,
                    },
                ])
                .mockImplementation(() => [
                    {
                        startLineNumber: 0,
                        endLineNumber: 0,
                        startColumn: 0,
                        endColumn: 1,
                    },
                ]),
            getModel: jest.fn(() => ({
                getValueInRange: jest.fn(() => 'show tables;'),
            })),
        },
        ScrollType: {
            Smooth: 0,
            Immediate: 1,
        },
    },
    languages: {
        register: jest.fn(),
        setMonarchTokensProvider: jest.fn(),
        registerCompletionItemProvider: jest.fn(),
        CompletionItemInsertTextRule: {
            InsertAsSnippet: 0,
        },
        CompletionItemKind: {
            Keyword: 0,
        },
    },
    KeyMod: {
        CtrlCmd: 0,
        Shift: 1,
    },
    KeyCode: {
        KeyA: 31,
        KeyF: 36,
        KeyK: 41,
        KeyP: 46,
        KeyZ: 56,
        Comma: 82,
    },
};
