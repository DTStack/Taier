/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import molecule from '@dtinsight/molecule';
import type { UniqueId } from '@dtinsight/molecule/esm/common/types';
import type { IEditorTab, IExtension, IProblemsItem } from '@dtinsight/molecule/esm/model';
import { MarkerSeverity } from '@dtinsight/molecule/esm/model';
import { debounce } from 'lodash';
import { LanguageService } from 'monaco-sql-languages/out/esm/languageService';
import 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution';
import 'monaco-sql-languages/out/esm/hivesql/hivesql.contribution';
import 'monaco-sql-languages/out/esm/sql/sql.contribution';
import 'monaco-sql-languages/out/esm/mysql/mysql.contribution';
import 'monaco-sql-languages/out/esm/flinksql/flinksql.contribution';

import { TASK_LANGUAGE } from '@/constant';

interface ValidMessage {
    endCol: number;
    endLine: number;
    message: string;
    startCol: number;
    startLine: number;
}

const languageService = new LanguageService();

function convertMsgToProblemItem(tab: IEditorTab, msgs: ValidMessage[] = []): IProblemsItem {
    const rootId = Number(tab.id);
    const rootName = `任务: ${tab.name || ''}`;
    const languageProblems: IProblemsItem = {
        id: rootId,
        name: rootName,
        isLeaf: false,
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
            id: `${rootId}-${index}`,
            name: '',
            isLeaf: true,
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
    if (tab.data && tab.data.language) {
        const NOT_ANAYLSE_LANGUAGE: string[] = [TASK_LANGUAGE.JSON];
        if (NOT_ANAYLSE_LANGUAGE.includes(tab.data.language)) return;
        const sql = tab.data.value || '';

        languageService.valid(tab.data.language || TASK_LANGUAGE.SQL, sql).then((res: ValidMessage[]) => {
            if (res.length) {
                const problems = convertMsgToProblemItem(tab, res);
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
        getWorkerUrl(moduleId: string, label: TASK_LANGUAGE) {
            switch (label) {
                case TASK_LANGUAGE.SPARKSQL: {
                    return './sparksql.worker.js';
                }
                case TASK_LANGUAGE.FLINKSQL: {
                    return './flinksql.worker.js';
                }
                case TASK_LANGUAGE.HIVESQL: {
                    return './hivesql.worker.js';
                }
                case TASK_LANGUAGE.MYSQL: {
                    return './mysql.worker.js';
                }
                case TASK_LANGUAGE.PLSQL: {
                    return './plsql.worker.js';
                }
                case TASK_LANGUAGE.JSON: {
                    return './json.worker.js';
                }
                case TASK_LANGUAGE.SQL: {
                    return './sql.worker.js';
                }
                case TASK_LANGUAGE.PYTHON:
                case TASK_LANGUAGE.SHELL:
                default: {
                    return './editor.worker.js';
                }
            }
        },
    };
}

/**
 * This is for loading language.work for parsing the sql language
 */
export class ExtendsSparkSQL implements IExtension {
    id: UniqueId = 'sparkSql';
    name = 'sparkSql';
    dispose(): void {
        throw new Error('Method not implemented.');
    }
    activate(): void {
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

        molecule.editor.onCloseAll(() => {
            molecule.problems.setState({
                data: [],
            });
        });
    }
}
