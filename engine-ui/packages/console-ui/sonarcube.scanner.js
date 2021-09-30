const scanner = require('sonarqube-scanner');
const VERSION = JSON.stringify(require('./package.json').version); // app version.
const NAME = JSON.stringify(require('./package.json').name);
scanner(
	{
		// this example uses local instance of SQ
		serverUrl: 'http://172.16.100.198:9000/',
		token: '',
		options: {
			'sonar.projectVersion': VERSION,
			'sonar.projectName': NAME,
			'sonar.sources': './src',
			'sonar.exclusions': '**/__tests__/**',
			'sonar.tests': './src',
			'sonar.test.inclusions': './src/__tests__/**/*.test.tsx,./src/__tests__/**/*.test.ts',
			'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
			'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml',
		},
	},
	() => {
		// callback is required
	},
);
