module.exports = {
	extends: [require.resolve('@umijs/fabric/dist/eslint')],
	parserOptions: {},
	rules: {
		'no-use-before-define': 'off',
		'@typescript-eslint/no-use-before-define': 'off',
		'consistent-return': 'off',
		'react-hooks/exhaustive-deps': 'off',
	},
};
