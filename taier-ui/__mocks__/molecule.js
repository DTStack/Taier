const { editor, ...rest } = require('./monaco-editor');

module.exports = {
	Component: class {
		subscribe = jest.fn();
	},
	FolderTreeService: jest.fn(),
	GlobalEvent: class {
		subscribe = jest.fn();
	},
	panel: {
		getState: jest.fn(),
		setState: jest.fn(),
		setActive: jest.fn(),
		add: jest.fn(),
		update: jest.fn(),
		getPanel: jest.fn(),
	},
	editor: {
		updateActions: jest.fn(),
		...editor,
	},
	layout: {
		getState: jest.fn(),
		togglePanelVisibility: jest.fn(),
	},
	connect: jest.fn(),
	TreeViewUtil: jest.fn(),
	...rest,
};
