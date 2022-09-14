module.exports = {
	Component: class {
		subscribe = jest.fn();
	},
	FolderTreeService: jest.fn(),
	GlobalEvent: class {
		subscribe = jest.fn();
	},
	connect: jest.fn(),
	...require('./monaco-editor'),
};
