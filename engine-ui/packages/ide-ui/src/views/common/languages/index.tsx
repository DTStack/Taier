import { debounce } from 'lodash';
import molecule from '@dtinsight/molecule';
import {
    IEditorTab,
    IExtension,
    IProblemsItem,
    MarkerSeverity,
} from '@dtinsight/molecule/esm/model';
import 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution';
import { LanguageService } from 'monaco-sql-languages/out/esm/languageService';

interface ValidMessage {
    endCol: number;
    endLine: number;
    message: string;
    startCol: number;
    startLine: number;
}

const languageService = new LanguageService();

function convertMsgToProblemItem(
    tab: IEditorTab,
    code: string,
    msgs: ValidMessage[] = []
): IProblemsItem {
    const rootId = Number(tab.id);
    const rootName = `任务: ${tab.name || ''}`;
    const languageProblems: IProblemsItem = {
        id: rootId,
        name: rootName,
        value: {
            code: rootName,
            message: '',
            startLineNumber: 0,
            startColumn: 1,
            endLineNumber: 0,
            endColumn: 1,
            status: MarkerSeverity.Hint,
        },
        children: [],
    };

    languageProblems.children = msgs.map((msg, index: number) => {
        return {
            id: rootId + index,
            name: code || '',
            value: {
                code: '',
                message: msg.message,
                startLineNumber: Number(msg.startLine),
                startColumn: Number(msg.startCol),
                endLineNumber: Number(msg.endLine),
                endColumn: Number(msg.endLine),
                status: MarkerSeverity.Error,
            },
            children: [],
        };
    });

    return languageProblems;
}

function analyseProblems(tab: IEditorTab) {
    if (tab.data) {
        const sql = tab.data.value || '';

        languageService
            .valid(tab.data.language || 'sql', sql)
            .then((res: ValidMessage[]) => {
                if (res.length) {
                    const problems = convertMsgToProblemItem(tab, sql, res);
                    molecule.problems.add(problems);
                } else {
                    const rootId = Number(tab.id);
                    const problems = molecule.problems.getState().data;
                    const isExisted = problems.find((pro) => pro.id === rootId);
                    if (isExisted) {
                        molecule.problems.remove(rootId);
                    }
                }
            });
    }
}

function registerWorkers() {
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

export class ExtendsSparkSQL implements IExtension {
    activate(extensionCtx: molecule.IExtensionService): void {
        registerWorkers();
        molecule.editor.onUpdateTab(debounce(analyseProblems, 600));
        molecule.editor.onOpenTab(analyseProblems);

        molecule.editor.onCloseTab((tabId) => {
            const { data } = molecule.problems.getState();
            const isExist = data.find((item) => item.id === Number(tabId));
            if (isExist) {
            molecule.problems.remove(Number(tabId));
            }
        });
    }
}
