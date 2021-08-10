module.exports = {
    env: {
        browser: true,
        es2021: true
    },
    extends: [
        'plugin:react/recommended',
        'standard'
    ],
    parser: '@typescript-eslint/parser',
    parserOptions: {
        ecmaFeatures: {
            jsx: true
        },
        ecmaVersion: 12
    },
    plugins: [
        'react',
        '@typescript-eslint'
    ],
    settings: {
        react: {
            version: 'detect'
        }
    },
    rules: {
        indent: [2, 4, { SwitchCase: 1 }],
        'react/react-in-jsx-scope': 'off',
        'no-use-before-define': 'off'
    }
}
