module.exports = {
	setupFiles: ['<rootDir>/scripts/jest-setup.js'],
	modulePathIgnorePatterns: ['<rootDir>/src/.umi/'],
	moduleNameMapper: {
		'@/(.*)': '<rootDir>/src/$1',
		'^@dtinsight/molecule(/?)(.*)$': '<rootDir>/__mocks__/molecule.js',
	},
};
