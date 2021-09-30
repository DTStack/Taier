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

import * as React from 'react';
import CodeMirrorEditor from 'dt-react-codemirror-editor';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { workbenchActions } from '../../../controller/dataSync/offlineAction';
import * as editorActions from '../../../controller/dataSync/workbench';
import 'dt-react-codemirror-editor/lib/codemirror/style.css';

@(connect(
	(state: any) => {
		const { workbench } = state.dataSync;
		const { currentTab, tabs } = workbench;
		const currentTabData = tabs.filter((tab: any) => {
			return tab.id === currentTab;
		})[0];
		return {
			editor: state.editor,
			currentTab,
			currentTabData,
		};
	},
	(dispatch: any) => {
		const taskAc = workbenchActions(dispatch);
		const editorAc = bindActionCreators(editorActions, dispatch);
		const actions = Object.assign(editorAc, taskAc);
		return actions;
	},
) as any)
class Markdown extends React.Component<any> {
	componentDidMount() {
		// const currentNode = this.props.currentTabData;
		// if (currentNode) {
		//     this.props.getTab(currentNode.id); // 初始化console所需的数据结构
		// }
	}

	render() {
		const { currentTabData, editor } = this.props;
		const currentTab = currentTabData?.id;
		const consoleData = editor.console;

		const data =
			consoleData && consoleData[currentTab] ? consoleData[currentTab] : { results: [] };
		const defaultValue = data && data.log;
		const defaultEditorOptions: any = {
			mode: 'dtlog',
			lint: true,
			indentWithTabs: true,
			smartIndent: true,
			lineNumbers: false,
			autofocus: false,
			lineWrapping: true,
			readOnly: true,
		};

		return (
			<div className="mo_code_mirror">
				<CodeMirrorEditor
					style={{ minHeight: 'auto', height: '100%' }}
					value={defaultValue}
					options={{ ...defaultEditorOptions }}
					sync={true}
				/>
			</div>
		);
	}
}

export default Markdown;
