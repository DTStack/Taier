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

// import 'monaco-editor/esm/vs/editor/browser/controller/coreCommands.js';
// import 'monaco-editor/esm/vs/editor/contrib/find/findController.js';
// import 'monaco-editor/esm/vs/editor/contrib/folding/folding.js';
// import 'monaco-editor/esm/vs/editor/contrib/contextmenu/contextmenu.js';
// import 'monaco-editor/esm/vs/editor/contrib/smartSelect/smartSelect.js';

// import 'monaco-editor/esm/vs/editor/editor.all.js';
// import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
// import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
import * as monaco from 'monaco-editor';
import 'monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js';
import 'monaco-editor/esm/vs/basic-languages/python/python.contribution.js';

// monaco 当前版本并未集成最新basic-languages， 暂时shell单独引入
import './languages/shell/shell.contribution';
import './languages/dtsql/dtsql.contribution'

import './style.scss';
import { defaultOptions } from './config';

export interface DiffEditorProps {
    className?: string;
    style?: object;
    options?: object;
    theme?: any;
    language?: any;
    value?: any;
    /**
     * 该方法的入参为源文件Editor的引用
     */
    editorInstanceRef?: Function;
    /**
     * 源文件的属性对象
     * value:文件内容
     * cursorPosition:文件的指针位置
     */
    original?: { value: string; cursorPosition?: object };
    /**
     * 被对比文件的属性对象
     * value:文件内容
     */
    modified?: { value: string };
    /**
     * 源文件改变事件回调函数
     */
    onChange?: Function;
    /**
     * 源文件失去焦点回调函数
     */
    onBlur?: Function;
    /**
     * 源文件获得焦点回调函数
     */
    onFocus?: Function;
    /**
     * 文件指针改变事件回调函数
     */
    onCursorSelection?: Function;
    /**
     * 是否同步源文件内容
     */
    sync?: boolean;
    /**
     * 是否打印编辑器日志
     */
    isLog?: boolean;
}

class DiffEditor extends React.Component<DiffEditorProps, any> {
    constructor (props: any) {
        super(props);
    }
    monacoDom: any = null;
    monacoInstance: any = null;
    _originalEditor: any = null;
    _modifiedEditor: any = null;
    _originalModel: any = null;
    _modifiedModel: any = null;

    shouldComponentUpdate (nextProps: any, nextState: any) {
        // // 此处禁用render，直接用editor实例更新编辑器
        return false;
    }

    componentDidMount () {
        this.initMonaco();
        if (typeof this.props.editorInstanceRef === 'function') {
            this.props.editorInstanceRef(this._originalEditor)
        }
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { sync, original = {}, modified = {}, options = {}, theme } = nextProps;
        if (this.props.original && this.props.original.value !== original.value && sync) {
            const editorText = !original.value ? '' : original.value;
            this.updateValueWithNoEvent(editorText);
        }
        if (this.props.modified && this.props.modified.value !== modified.value) {
            this._modifiedEditor.setValue(modified.value || '')
        }
        if (this.props.options !== options) {
            this.monacoInstance.updateOptions({ ...options, originalEditable: !options.readOnly })
        }
        if (this.props.theme !== theme) {
            monaco.editor.setTheme(theme);
        }
    }

    componentWillUnmount () {
        this.destroyMonaco();
    }

    isValueExist (props: any) {
        const keys = Object.keys(props);
        if (keys.includes('value')) {
            return true;
        }
        return false;
    }

    log (args: any) {
        const { isLog } = this.props;
        isLog && console.log(...args);
    }

    destroyMonaco () {
        if (this.monacoInstance) {
            this._modifiedEditor.dispose();
            this._originalEditor.dispose();
            this.monacoInstance.dispose();
        }
    }

    initMonaco () {
        const { original = {}, modified = {}, language, options }: any = this.props;
        if (!this.monacoDom) {
            console.error('初始化dom节点出错');
            return;
        }

        const editorOptions = Object.assign({}, defaultOptions, options, {
            originalEditable: options ? !options.readOnly : true, // 支持源可编辑
            renderIndicators: false,
            scrollbar: {
                horizontal: 'visible'
            }
        });

        this._originalModel = monaco.editor.createModel(original.value, language || 'sql')
        this._modifiedModel = monaco.editor.createModel(modified.value, language || 'sql')

        this.monacoInstance = monaco.editor.createDiffEditor(this.monacoDom, editorOptions);
        this.monacoInstance.setModel({
            original: this._originalModel,
            modified: this._modifiedModel
        })
        this._originalEditor = this.monacoInstance.getOriginalEditor();
        this._modifiedEditor = this.monacoInstance.getModifiedEditor();
        this._modifiedEditor.updateOptions({
            readOnly: true
        })
        if (this._originalEditor && original.cursorPosition) {
            this._originalEditor.setPosition(original.cursorPosition);
            this._originalEditor.focus();
            this._originalEditor.revealPosition(original.cursorPosition, monaco.editor.ScrollType.Immediate);
        }

        this.initEditor();
    }

    initEditor () {
        this.initTheme();
        this.initEditorEvent();
    }
    initTheme () {
        // hack 交换对比编辑器的位置
        monaco.editor.defineTheme('flippedDiffTheme', {
            base: this.props.theme || 'vs',
            inherit: true,
            rules: [],
            colors: {
                'diffEditor.insertedTextBackground': '#ff000033',
                'diffEditor.removedTextBackground': '#28d22833'
            }
        });
        monaco.editor.setTheme('flippedDiffTheme');
    }

    updateValueWithNoEvent (value: any) {
        this._originalEditor.setValue(value);
    }

    initEditorEvent () {
        this._originalEditor.onDidChangeModelContent((event: any) => {
            this.log('编辑器事件');
            const { onChange } = this.props;
            const newValue = this._originalEditor.getValue();
            if (onChange) {
                this.log('订阅事件触发');
                onChange(newValue, this._originalEditor);
            }
        });

        this._originalEditor.onDidBlurEditor((event: any) => {
            this.log('编辑器事件 onDidBlur');
            const { onBlur, value } = this.props;
            if (onBlur) {
                const oldValue = this._originalEditor.getValue();
                onBlur(value, oldValue);
            }
        });

        this._originalEditor.onDidFocusEditor((event: any) => {
            this.log('编辑器事件 onDidFocus');
            const { onFocus, value } = this.props;
            if (onFocus) {
                const oldValue = this._originalEditor.getValue();
                onFocus(value, oldValue);
            }
        });

        this._originalEditor.onDidChangeCursorSelection((event: any) => {
            this.log('编辑器事件 onDidChangeCursorSelection');
            const { onCursorSelection } = this.props;
            const ranges = this._originalEditor.getSelections();
            const model = this._originalEditor.getModel();
            let selectionContent = '';
            for (let i = 0; i < ranges.length; i++) {
                selectionContent = selectionContent += model.getValueInRange(ranges[i]);
            }
            if (onCursorSelection) {
                onCursorSelection(selectionContent);
            }
        });
    }

    render () {
        const { className, style } = this.props;

        let renderClass = 'code-editor';
        renderClass = className ? `${renderClass} ${className}` : renderClass;

        let renderStyle: any = {
            position: 'relative',
            minHeight: '400px',
            // height: '100%',
            width: '100%',
            marginTop: '20px'
        };

        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;

        return <div
            className={renderClass}
            style={renderStyle}
            ref={(domIns: any) => { this.monacoDom = domIns; }}
        />;
    }
}
export default DiffEditor;
