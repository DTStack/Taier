const { editor, ...rest } = require('./monaco-editor');

module.exports = {
	Component: class {
		emit = jest.fn();
		subscribe = jest.fn();
		getState = () => this.state;
		setState = (state) => Object.assign(this.state, state);
	},
	FolderTreeService: class FolderTreeService {
		get = jest.fn();
	},
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
	explorer: {
		forceUpdate: jest.fn(),
	},
	editor: {
		updateActions: jest.fn(),
		updateGroup: jest.fn(),
		updateTab: jest.fn(),
		getDefaultActions: jest.fn(),
		getState: jest.fn(),
		getGroupIdByTab: jest.fn(),
		setActive: jest.fn(),
		isOpened: jest.fn(),
		open: jest.fn(),
		closeTab: jest.fn(),
		...editor,
	},
	folderTree: {
		get: jest.fn(),
		add: jest.fn(),
		getState: jest.fn(),
		update: jest.fn(),
		setActive: jest.fn(),
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
	colorTheme: {
		getColorThemeMode: jest.fn(),
		onChange: jest.fn(),
	},
	...rest,
};
