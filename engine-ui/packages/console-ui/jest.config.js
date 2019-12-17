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
        '\\.(css|scss|less)$': '<rootDir>/mock/styleMock.js',

        '^utils(.*)$': '<rootDir>/src/utils$1',
        '^widgets(.*)$': '<rootDir>/src/widgets$1',
        '^consts(.*)$': '<rootDir>/src/consts$1',
        '^funcs(.*)$': '<rootDir>/src/funcs$1',
        '^config(.*)$': '<rootDir>/src/config$1',
        '^database(.*)$': '<rootDir>/src/database$1',

        '^main(.*)$': '<rootDir>/src/webapps/main$1',
        '^rdos(.*)$': '<rootDir>/src/webapps/rdos$1',
        '^stream(.*)$': '<rootDir>/src/webapps/stream$1',
        '^dataQuality(.*)$': '<rootDir>/src/webapps/dataQuality$1',
        '^dataApi(.*)$': '<rootDir>/src/webapps/dataApi$1',
        '^dataLabel(.*)$': '<rootDir>/src/webapps/dataLabel$1',
        '^analytics(.*)$': '<rootDir>/src/webapps/analytics$1'
    }
};
