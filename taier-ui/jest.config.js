module.exports = {
    transform: {
        '^.+\\.(t|j)sx?$': ['@swc/jest'],
    },
    setupFilesAfterEnv: ['<rootDir>/src/tests/jest-setup.js'],
    testEnvironment: "jsdom",
    modulePathIgnorePatterns: ['<rootDir>/src/.umi/'],
    moduleNameMapper: {
        '@/(.*)': '<rootDir>/src/$1',
        '^@dtinsight/molecule(/?)(.*)$': '<rootDir>/__mocks__/molecule.js',
        '\\.(css|less|sass|scss|stylus)$': require.resolve('identity-obj-proxy'),
    },
};
