module.exports = {
	transform: {
		'^.+\\.(t|j)sx?$': ['@swc/jest'],
	},
	setupFiles: ['<rootDir>/scripts/jest-setup.js'],
	modulePathIgnorePatterns: ['<rootDir>/src/.umi/'],
	moduleNameMapper: {
		'@/(.*)': '<rootDir>/src/$1',
		'^@dtinsight/molecule(/?)(.*)$': '<rootDir>/__mocks__/molecule.js',
		'\\.(css|less|sass|scss|stylus)$': require.resolve('identity-obj-proxy'),
	},
};
