const appConf = require('./mock/appConfig');

// jest.config.js
module.exports = {
    preset: 'ts-jest',
    globals: {
        'APP_CONF': appConf
    },
    transformIgnorePatterns: [
        'node_modules/[^/]+?/(?!(es|node_modules)/)' // Ignore modules without es dir
    ],
    testPathIgnorePatterns: ['/node_modules/'],
    testMatch: [
        '**/__tests__/**/(*.)+(spec|test).[jt]s?(x)',
        '**/test/**/(*.)+(spec|test).[jt]s?(x)'
    ],
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
    moduleNameMapper: {
        '\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$': '<rootDir>/mock/fileMock.js',
        '\\.(css|scss|less)$': '<rootDir>/mock/styleMock.js'
    }
};
