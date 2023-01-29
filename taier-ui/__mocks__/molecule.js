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
	Header: ({ title, toolbar }) => (
		<div data-testid="mockHeader">
			{title}
			<div data-testid="mockHeaderToolbar">{toolbar}</div>
		</div>
	),
	Content: ({ children }) => <div data-testid="mockContent">{children}</div>,
	FolderTree: jest.fn(),
	connect: jest.fn((_, children) => children),
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
	notification: {
		getState: jest.fn(),
		add: jest.fn(),
	},
	component: {
		Scrollbar: ({ children }) => <div data-testid="Scrollbar">{children}</div>,
		Icon: ({ type }) => <svg data-testid="Icon" type={type} />,
		ActionBar: ({ data = [] }) => (
			<div data-testid="ActionBar">
				{data.map((i) => (
					<div key={i.id} id={i.id}>
						{i.name}
					</div>
				))}
			</div>
		),
	},
	sidebar: {
		setActive: jest.fn(),
	},
	activityBar: {
		setActive: jest.fn(),
	},
	...rest,
};
