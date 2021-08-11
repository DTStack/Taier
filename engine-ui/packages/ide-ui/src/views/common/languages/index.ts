import 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution';
(window as any).MonacoEnvironment = {
    getWorkerUrl: function (moduleId: string, label: string) {
        switch (label) {
            case 'sparksql': {
                return './sparksql.worker.js'
            }
            default: {
                return './editor.worker.js'
            }
        }
    }
}
