module.exports = {
    extends: [require.resolve('ko-lint-config/.stylelintrc')],
    rules: {
        // Although hexadecimal is traditionally written in uppercase, but we choose lower only because of quicker to read
        'color-hex-case': 'lower',
        // Prefer more empty line between style because of quicker to read
        'rule-empty-line-before': [
            'always',
            {
                except: ['after-single-line-comment', 'first-nested'],
            },
        ],
        'custom-property-pattern': '^([a-zA-Z]*)(-[a-zA-Z]+)*$',
    },
};
