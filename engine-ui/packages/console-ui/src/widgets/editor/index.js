import React from "react";

import 'monaco-editor/esm/vs/editor/browser/controller/coreCommands.js';
import 'monaco-editor/esm/vs/editor/contrib/find/findController.js';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import 'monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js';
// import 'monaco-editor/esm/vs/basic-languages/mysql/mysql.contribution.js';
// import "monaco-editor/esm/vs/basic-languages/python/python.contribution.js";


import "./style.scss";
import { defaultOptions } from './config';

class Editor extends React.Component {

    constructor(props) {
        super(props);
        this.monacoDom = null;
        this.monacoInstance = null;
        this.__props_update = false; //非输入触发标志
    }

    shouldComponentUpdate (nextProps, nextState) {
        console.log('shouldComponentUpdate')
        // if (this.props.option !== nextProps.option) {
        //     return true;
        // }
        return false;
    }

    componentDidMount() {
        console.log('init editor: ')
        this.initMonaco();
    }


    componentWillReceiveProps(nextProps) {
        const { sync, value } = nextProps;
        console.log('Editor nextProps:', nextProps)
        if ( this.props.value !== value) {
            this.log("props value更新");
            if (sync) {
                const editorText = !value ? '' : value;
                this.updateValueWithNoEvent(editorText);
            }
        }
    }

    componentWillUnmount() {
        this.destroyMonaco();
    }

    isValueExist(props) {
        const keys = Object.keys(props);
        if (keys.includes("value")) {
            return true;
        }
        return false;
    }

    log() {
        const { isLog } = this.props;
        isLog && console.log(...arguments);
    }

    destroyMonaco() {
        if (this.monacoInstance) {
            this.monacoInstance.dispose();
        }
    }

    initMonaco() {
        const { value, language, options } = this.props;
        // const initValue = value || defaultValue;

        if (!this.monacoDom) {
            console.error("初始化dom节点出错");
            return;
        }
        console.log('initMonaco', value)

        window.MonacoEnvironment = {
            getWorkerUrl: function(moduleId, label) {
                console.log('getWorkerUrl:', arguments);
                if (label === "json") {
                    return "./json.worker.js";
                }
                if (label === "css") {
                    return "./css.worker.js";
                }
                if (label === "html") {
                    return "./html.worker.js";
                }
                if (label === "typescript" || label === "javascript") {
                    return "./typescript.worker.js";
                }
                if (label === "sql") {
                    return "./sql.worker.js";
                }
                return "./editor.worker.js";
            }
        };

        const model = monaco.editor.createModel(
            value,
            language || "javascript"
        );

        // monaco.languages.registerCodeActionProvider(lang, {
        //     provideCodeActions: function(model, range, context, token) {
        //         console.log('token', token)
        //     }
        // });

        const editorOptions = Object.assign({}, defaultOptions, options, {
            model,
        });
        // console.log('model:', editorOptions)
        this.monacoInstance = monaco.editor.create(this.monacoDom, editorOptions);

        this.initEditor();
    }

    initEditor() {
        this.initEditorEvent();
    }

    updateValueWithNoEvent(value) {
        this.monacoInstance.setValue(value);
    }

    initEditorEvent() {
        this.monacoInstance.onDidChangeModelContent(event => {
            this.log("编辑器事件");
            const { onChange } = this.props;
            const newValue = this.monacoInstance.getValue();
            if (onChange) {
                this.log("订阅事件触发");
                onChange(newValue);
            }
        });

        this.monacoInstance.onDidBlurEditor(event => {
            this.log("编辑器事件 onDidBlur");
            const { onBlur, value } = this.props;
            if (onBlur) {
                const oldValue = this.monacoInstance.getValue();
                onBlur(value, oldValue);
            }
        });
    
        this.monacoInstance.onDidFocusEditor(event => {
            this.log("编辑器事件 onDidFocus");
            const { onFocus, value } = this.props;
            if (onFocus) {
                const oldValue = this.monacoInstance.getValue();
                onFocus(value, oldValue);
            }
        });

        this.monacoInstance.onDidChangeCursorSelection(event => {
            this.log("编辑器事件 onDidChangeCursorSelection");
            const { onCursorSelection } = this.props;
            const ranges = this.monacoInstance.getSelections();
            const model = this.monacoInstance.getModel();
            let selectionContent = '';
            for (let i = 0; i < ranges.length; i++) {
                selectionContent = selectionContent += model.getValueInRange(ranges[i]);
            }
            if (onCursorSelection) {
                onCursorSelection(selectionContent);
            }
        });
    }

    render() {
        const { className, style } = this.props;

        let renderClass = 'code-editor';
        renderClass = className ? `${renderClass} ${className}` : renderClass;

        let renderStyle = {
            position: 'relative',
            minHeight: "400px",
            height: '100%',
            width: '100%',
        };

        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;
        
        return <div 
            className={renderClass}
            style={renderStyle}
            ref={(domIns) => { this.monacoDom = domIns; }} 
        />;
    }
}
export default Editor;
