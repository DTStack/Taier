import molecule from 'molecule';
import { IExtension } from 'molecule/esm/model';
import 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution';
export class ExtendsSparkSQL implements IExtension {
    activate(extensionCtx: molecule.IExtensionService): void {
        (window as any).MonacoEnvironment = {
            getWorkerUrl: function (moduleId: string, label: string) {
                switch (label) {
                    case 'sparksql': {
                        return './sparksql.worker.js';
                    }
                    case 'flinksql': {
                        return './flinksql.worker.js';
                    }
                    case 'hivesql': {
                        return './hivesql.worker.js';
                    }
                    case 'mysql': {
                        return './mysql.worker.js';
                    }
                    case 'plsql': {
                        return './plsql.worker.js';
                    }
                    case 'sql': {
                        return './sql.worker.js';
                    }
                    default: {
                        return './editor.worker.js';
                    }
                }
            },
        };
    }
}
