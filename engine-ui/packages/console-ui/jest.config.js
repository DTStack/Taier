// jest.config.js
module.exports = {
    transformIgnorePatterns: [
        'node_modules/(?!(src)/)'
    ],
    testMatch: [
        '**/__tests__/**/(*.)+(spec|test).[jt]s?(x)',
        '**/test/**/(*.)+(spec|test).[jt]s?(x)'
    ]
};
