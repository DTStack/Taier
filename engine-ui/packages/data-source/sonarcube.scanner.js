const scanner = require('sonarqube-scanner');
const VERSION = JSON.stringify(require('./package.json').version); // app version.

scanner(
  {
    // this example uses local instance of SQ
    serverUrl: 'http://172.16.100.198:9000/',
    token: 'b9872145bc6cd93a9b78c3b744e2196d3589867a',
    options: {
      'sonar.scm.disabled': 'true',
      'sonar.projectVersion': VERSION,
      'sonar.projectName': 'dt-data-source-front',
      'sonar.sources': './src',
      'sonar.exclusions': '**/__tests__/**',
      'sonar.tests': './src',
      'sonar.test.inclusions':
        './src/__tests__/**/*.test.tsx,./src/__tests__/**/*.test.ts',
      'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
      'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml',
    },
  },
  () => process.exit()
);
