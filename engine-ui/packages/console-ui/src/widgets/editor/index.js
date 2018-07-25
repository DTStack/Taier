import React from "react";

// import 'monaco-editor/esm/vs/editor/browser/controller/coreCommands.js';
// import 'monaco-editor/esm/vs/editor/contrib/find/findController.js';
// import 'monaco-editor/esm/vs/editor/contrib/folding/folding.js';
// import 'monaco-editor/esm/vs/editor/contrib/contextmenu/contextmenu.js';
// import 'monaco-editor/esm/vs/editor/contrib/smartSelect/smartSelect.js';

// import 'monaco-editor/esm/vs/editor/editor.all.js';
// import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
import 'monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js';
import "monaco-editor/esm/vs/basic-languages/python/python.contribution.js";

// monaco 当前版本并未集成最新basic-languages， 暂时shell单独引入
import "./languages/shell/shell.contribution.js";

import "./style.scss";
import { defaultOptions } from './config';

class Editor extends React.Component {

    constructor(props) {
        super(props);
        this.monacoDom = null;
        this.monacoInstance = null;
    }

    shouldComponentUpdate (nextProps, nextState) {
        // // 此处禁用render， 直接用editor实例更新编辑器
        return false;
    }

    componentDidMount() {
        this.initMonaco();
    }

    componentWillReceiveProps(nextProps) {
        const { sync, value, theme } = nextProps;

        if ( this.props.value !== value && sync) {
            const editorText = !value ? '' : value;
            this.updateValueWithNoEvent(editorText);
        }
        if (this.props.options !== nextProps.options) {
            this.monacoInstance.updateOptions(nextProps.options)
        }

        if (this.props.theme !== theme) {
            monaco.editor.setTheme(theme)
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
        const { value, language, options, cursorPosition } = this.props;
        console.log('initMonaco:', this.props)
        if (!this.monacoDom) {
            console.error("初始化dom节点出错");
            return;
        }

        window.MonacoEnvironment = {
            getWorkerUrl: function(moduleId, label) {
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

        const editorOptions = Object.assign(defaultOptions, options , {
            value,
            language: language || "sql",
        });

        this.monacoInstance = monaco.editor.create(this.monacoDom, editorOptions);

        if (this.monacoInstance && cursorPosition) {
            this.monacoInstance.setPosition(cursorPosition);
            this.monacoInstance.focus();
            this.monacoInstance.revealPosition(cursorPosition, monaco.editor.ScrollType.Immediate);
        }

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
            const { onChange, value } = this.props;
            const newValue = this.monacoInstance.getValue();
            if (onChange) {
                this.log("订阅事件触发");
                onChange(newValue, this.monacoInstance);
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
