const { editor, ...rest } = require('./monaco-editor');

module.exports = {
	Component: class {
		subscribe = jest.fn();
	},
	FolderTreeService: jest.fn(),
	GlobalEvent: class {
		emit = jest.fn();
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
	folderTree: {
		get: jest.fn(),
		add: jest.fn(),
		getState: jest.fn(),
		update: jest.fn(),
	},
	layout: {
		getState: jest.fn(),
		togglePanelVisibility: jest.fn(),
	},
	connect: jest.fn(),
	TreeViewUtil: jest.fn(),
	FileTypes: {},
	TreeNodeModel: class {
		constructor(params) {
			Object.assign(this, params);
		}
	},
	...rest,
};
