module.exports = {
    env: {
        browser: true,
        es2021: true,
        jest: true,
    },
    extends: ['plugin:react/recommended', 'standard', 'prettier'],
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaFeatures: {
            jsx: true,
        },
        ecmaVersion: 12,
    },
    plugins: ['react', '@typescript-eslint'],
    settings: {
        react: {
            version: 'detect',
        },
    },
    ignorePatterns: ['node_modules', 'src/assets'],
    rules: {
        indent: [2, 4, { SwitchCase: 1 }],
        'comma-dangle': ['error', 'never'],
        'react/react-in-jsx-scope': 'off',
        'no-use-before-define': 'off',
        'react/display-name': 0,
        'multiline-ternary': 0,
        'comma-dangle': 0,
        'no-dupe-keys': 0,
        eqeqeq: 0,
    },
};
