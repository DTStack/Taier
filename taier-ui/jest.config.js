module.exports = {
	setupFiles: ['<rootDir>/scripts/jest-setup.js'],
	coveragePathIgnorePatterns: [],
	moduleNameMapper: {
		'@/(.*)': '<rootDir>/src/$1',
		'^@dtinsight/molecule(/?)(.*)$': '<rootDir>/__mocks__/molecule.js',
		'^monaco-editor$': '<rootDir>/__mocks__/monaco-editor.js',
	},
};
