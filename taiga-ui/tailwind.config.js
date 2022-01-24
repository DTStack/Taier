module.exports = {
	// mode: 'jit',
	// jit document: https://tailwindcss.com/docs/just-in-time-mode
	purge: ['./src/**/*.html', './src/**/*.tsx', './src/**/*.ts'],
	darkMode: false, // or 'media' or 'class'
	theme: {
		spacing: {
			'5px': '5px',
			'10px': '10px',
			'20px': '20px',
		},
		extend: {
			colors: {
				ccc: '#ccc',
				ddd: '#ddd',
			},
		},
	},
	variants: {
		extend: {},
	},
	plugins: [],
};
