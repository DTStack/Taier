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

import { useMemo } from 'react';
import CodeMirrorEditor from 'dt-react-codemirror-editor';
import molecule from '@dtinsight/molecule';
import { connect } from '@dtinsight/molecule/esm/react';
import type { ITaskResultStates } from '@/services/taskResultService';
import taskResultService from '@/services/taskResultService';
import 'dt-react-codemirror-editor/lib/codemirror/style.css';
import './index.scss';

interface IMarkdownProps {
	results: ITaskResultStates;
	editor: molecule.model.IEditor;
}

const defaultEditorOptions = {
	mode: 'dtlog',
	lint: true,
	indentWithTabs: true,
	smartIndent: true,
	lineNumbers: false,
	autofocus: false,
	lineWrapping: true,
	readOnly: true,
};

export default connect(
	{ results: taskResultService, editor: molecule.editor },
	({ editor, results: { logs } }: IMarkdownProps) => {
		const { current } = editor;
		const currentTabId = useMemo(() => {
			return current?.tab?.id;
		}, [current]);

		if (!currentTabId) {
			return (
				<div
					style={{
						marginTop: 10,
						textAlign: 'center',
					}}
				>
					无法获取任务日志
				</div>
			);
		}
		const defaultValue = logs[currentTabId!];

		return (
			<div className="mo_code_mirror">
				<CodeMirrorEditor
					style={{ minHeight: 'auto', height: '100%' }}
					value={defaultValue}
					options={defaultEditorOptions}
					sync={true}
				/>
			</div>
		);
	},
);
