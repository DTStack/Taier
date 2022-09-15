module.exports = {
	editor: {
		create: jest.fn(),
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
