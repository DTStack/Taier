module.exports = {
    extends: [require.resolve('ko-lint-config/.eslintrc')],
    parserOptions: {},
    rules: {
        // Since there are majority of code have to use the not-null-assertion
        '@typescript-eslint/no-non-null-assertion': 'off',
        // Turn it to warn temporarily since there are some code have to use this comment to skip ts checker
        '@typescript-eslint/ban-ts-comment': 'warn',
        'react-hooks/exhaustive-deps': 'off',
    },
    overrides: [
        {
            files: ['**/*.test.{ts,tsx}'],
            rules: {
                '@typescript-eslint/ban-ts-comment': 'off',
            },
        },
    ],
};
