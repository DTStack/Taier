module.exports = {
	editor: {
		create: jest.fn(),
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
};
