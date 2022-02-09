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

import { defaultEditorOptions } from './config';
import pureRender from '@/utils/pureRender';

// Codemirror
import codemirror from 'codemirror';
import './languages/log';
import './languages/simpleLog';
import 'codemirror/lib/codemirror.css';
import 'codemirror/addon/lint/lint.css';
import 'codemirror/addon/scroll/simplescrollbars.css';
import './style.css';
import { getLinkMark, getLogMark } from './utils';

declare var window: any;

// require('codemirror/addon/fold/foldcode')
// require('codemirror/addon/fold/foldgutter')
// require('codemirror/addon/fold/brace-fold')

require('codemirror/mode/sql/sql');
require('codemirror/mode/python/python');
require('codemirror/mode/javascript/javascript');
require('codemirror/mode/properties/properties');
require('codemirror/addon/display/placeholder');
require('codemirror/addon/edit/matchbrackets');
require('codemirror/addon/scroll/simplescrollbars');

// require('codemirror/addon/lint/lint')
// require('../../assets/js/sql-lint')

@pureRender
class CodeEditor extends React.Component<any, any> {
	Editor: any;
	self: any;
	componentDidMount() {
		const ele = this.Editor;
		const options = this.props.options || defaultEditorOptions;
		const instance = this.getCodeMirrorIns();
		const {
			value,
			onChange,
			onFocus,
			cursor,
			focusOut,
			cursorActivity,
			editorRef,
		} = this.props;

		if (!ele) return;
		this.self = instance.fromTextArea(ele, options);
		this.renderTextMark();
		// 设置corsor位置
		if (cursor) this.self.doc.setCursor(cursor);

		this.self.on('change', (doc: any) => {
			if (onChange) {
				onChange(value, doc.getValue(), doc);
			}
		});
		this.self.on('focus', (doc: any) => {
			if (onFocus) {
				onFocus(value, doc.getValue());
			}
		});

		this.self.on('blur', (doc: any) => {
			if (focusOut) {
				focusOut(value, doc.getValue());
			}
		});

		this.self.on('cursorActivity', (doc: any) => {
			if (cursorActivity) {
				cursorActivity(value, doc);
			}
		});
		if (editorRef) {
			editorRef(this.self);
		}
	}
	// eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
		const {
			value,
			sync,
			cursor,
			placeholder,
			cursorAlwaysInEnd,
			options = {},
		} = nextProps;
		if (options) {
			this.self.setOption('readOnly', options.readOnly);
		}
		if (placeholder != this.props.placeholder) {
			this.self.setOption('placeholder', placeholder);
		}
		if (this.props.value !== value) {
			if (cursor) this.self.doc.setCursor(cursor);
			if (sync) {
				window.ted = this.self;
				const scrollInfo = this.self.getScrollInfo();
				/**
				 * 判断滚动条是不是在底部
				 */
				const isInBottom =
					scrollInfo.top +
						scrollInfo.clientHeight -
						scrollInfo.height >
					-10;
				console.log(isInBottom);
				if (!value) {
					this.self.setValue('');
				} else {
					this.self.setValue(value);
				}
				if (cursorAlwaysInEnd) {
					this.self.doc.setCursor(this.self.doc.lineCount(), null);
				} else if (!isInBottom) {
					/**
					 * 不在底部并且不设置自动滚到底部，则滚到原来位置
					 */
					this.self.scrollTo(scrollInfo.left, scrollInfo.top);
				} else if (isInBottom) {
					/**
					 * 在底部，则自动到底部
					 * 需要等setValue这个动作结束之后，再获取内容的高度。
					 */
					Promise.resolve().then(() => {
						let nowScrollInfo = this.self.getScrollInfo();
						this.self.scrollTo(
							nowScrollInfo.left,
							nowScrollInfo.height,
						);
					});
				}
			}
			this.renderTextMark();
		}
	}
	renderTextMark() {
		const marks = this.self.doc.getAllMarks();
		for (let mark of marks) {
			// 重置marks
			mark.clear();
		}
		const value = this.self.getValue();
		const linkMarks: any = []
			.concat(getLinkMark(value))
			.concat(getLogMark(value));
		for (let _i = 0; _i < linkMarks.length; _i++) {
			let mark = linkMarks[_i];
			this.self.doc.markText(
				this.self.doc.posFromIndex(mark.start),
				this.self.doc.posFromIndex(mark.end),
				{ replacedWith: mark.node },
			);
		}
	}
	getCodeMirrorIns() {
		return this.self || codemirror;
	}

	render() {
		const { className, style } = this.props;
		let renderClass = 'code-editor';
		renderClass = className ? `${renderClass} ${className}` : renderClass;
		let renderStyle: any = {
			position: 'relative',
			minHeight: '400px',
		};
		renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;

		return (
			<div className={renderClass} style={renderStyle}>
				<textarea
					ref={(e: any) => {
						this.Editor = e;
					}}
					name="code"
					placeholder={this.props.placeholder || ''}
					defaultValue={this.props.value || ''}
				/>
			</div>
		);
	}
}

export default CodeEditor;
