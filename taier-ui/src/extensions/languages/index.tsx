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
import { LanguageIdEnum, setupLanguageFeatures } from 'monaco-sql-languages';
import { LanguageService } from 'monaco-sql-languages/out/esm/languageService';

import { isValidLanguage } from '@/utils/is';

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
    if (tab.data && tab.data.language && isValidLanguage(tab.data.language)) {
        const sql = tab.data.value || '';
        languageService.valid(tab.data.language, sql).then((res: ValidMessage[]) => {
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
        // Close code analyse for MYSQL
        setupLanguageFeatures({
            languageId: LanguageIdEnum.MYSQL,
            diagnostics: false,
        });

        // Close code analyse for PGSQL
        setupLanguageFeatures({
            languageId: LanguageIdEnum.PG,
            diagnostics: false,
        });

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
