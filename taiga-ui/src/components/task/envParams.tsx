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

import React, { useEffect, useRef } from 'react';
import { MonacoEditor } from '@dtinsight/molecule/esm/components';
import { editor as monacoEditor, Uri } from '@dtinsight/molecule/esm/monaco';
import type { IEditor, IEditorTab } from '@dtinsight/molecule/esm/model';
import type { editor } from '@dtinsight/molecule/esm/monaco';
import {
	CREATE_DATASOURCE_PREFIX,
	CREATE_TASK_PREFIX,
	EDIT_FOLDER_PREFIX,
	EDIT_TASK_PREFIX,
	ENV_PARAMS,
} from '@/constant';

const getUniqPath = (path: string) => {
	return Uri.parse(`file://tab/${path}`);
};

interface IEnvParams extends Pick<IEditor, 'current'> {
	onChange?: (tab: IEditorTab, value: string) => void;
}

export default function EnvParams({ current, onChange }: IEnvParams) {
	const editorIns = useRef<editor.IStandaloneCodeEditor>();

	useEffect(() => {
		if (current && typeof current.tab?.id === 'number') {
			const model =
				monacoEditor.getModel(getUniqPath(current.tab?.data.path)) ||
				monacoEditor.createModel(
					current.tab?.data.taskParams || '',
					'ini',
					getUniqPath(current.tab?.data.path),
				);

			editorIns.current?.setModel(model);
		}
	}, [current?.id && current.tab?.id]);

	const INVALID_TASK = [
		EDIT_TASK_PREFIX,
		EDIT_FOLDER_PREFIX,
		CREATE_TASK_PREFIX,
		CREATE_DATASOURCE_PREFIX,
	];

	if (!current || !current.activeTab || INVALID_TASK.includes(current.activeTab.toString())) {
		return (
			<div
				style={{
					marginTop: 10,
					textAlign: 'center',
				}}
			>
				无法获取环境参数
			</div>
		);
	}

	return (
		<MonacoEditor
			options={{
				value: '',
				language: 'ini',
				automaticLayout: true,
				minimap: {
					enabled: false,
				},
			}}
			path={ENV_PARAMS}
			editorInstanceRef={(editorInstance) => {
				// This assignment will trigger moleculeCtx update, and subNodes update
				editorIns.current = editorInstance;

				editorInstance.onDidChangeModelContent(() => {
					const currentValue = editorIns.current?.getModel()?.getValue() || '';

					onChange?.(current.tab!, currentValue);
				});

				const model =
					monacoEditor.getModel(getUniqPath(current.tab?.data.path)) ||
					monacoEditor.createModel(
						current.tab?.data.taskParams || '',
						'ini',
						getUniqPath(current.tab?.data.path),
					);

				editorInstance.setModel(model);
			}}
		/>
	);
}
