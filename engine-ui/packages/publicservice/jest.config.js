const appConf = require('./mock/appConfig');

module.exports = {
  preset: 'ts-jest',
    setupFilesAfterEnv: ['./setupTests.ts'],
    globals: {
        APP_CONF: appConf
    },
    // testResultsProcessor: 'jest-sonar-reporter',
    transformIgnorePatterns: [
        `/node_modules/(?!(dt-common|dt-react-component|dt-react-codemirror-editor))` // Ignore modules without dt-common dir
    ],
    testPathIgnorePatterns: ['/node_modules/'],
    testMatch: [
        '**/__tests__/**/(*.)+(spec|test).[jt]s?(x)',
        '**/test/**/(*.)+(spec|test).[jt]s?(x)'
    ],
    moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx'],
    moduleNameMapper: {
        '\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$':
            '<rootDir>/mock/fileMock.js',
        '\\.(css|scss|less|/style)$': '<rootDir>/mock/styleMock.js',
        '@/(.*)$': '<rootDir>/src/$1',
        '^utils(.*)$': '<rootDir>/src/utils$1',
        '^consts(.*)$': '<rootDir>/src/consts$1',
        '^styles(.*)$': '<rootDir>/src/styles$1',
        '^api(.*)$': '<rootDir>/src/api$1'
    }
}
