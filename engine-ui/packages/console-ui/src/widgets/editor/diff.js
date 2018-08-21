import React from "react";
import PropTypes from 'prop-types';

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
import "./languages/dtsql/dtsql.contribution.js"

import "./style.scss";
import { defaultOptions } from './config';

class DiffEditor extends React.Component {

    constructor(props) {
        super(props);
        this.monacoDom = null;
        this.monacoInstance = null;
    }

    shouldComponentUpdate(nextProps, nextState) {
        // // 此处禁用render， 直接用editor实例更新编辑器
        return false;
    }

    componentDidMount() {
        this.initMonaco();
        if (typeof this.props.editorInstanceRef == "function") {
            this.props.editorInstanceRef(this._originalEditor)
        }
    }

    componentWillReceiveProps(nextProps) {
        const { sync, original={},modified={}, options={} } = nextProps;
        if (this.props.original&&this.props.original.value !== original.value && sync) {
            const editorText = !original.value ? '' : original.value;
            this.updateValueWithNoEvent(editorText);
        }
        if(this.props.modified&&this.props.modified.value !== modified.value){
            this._modifiedEditor.setValue(modified.value)
        }
        if (this.props.options !== options) {
            this.monacoInstance.updateOptions({...options,originalEditable:!options.readOnly})
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
            this._modifiedEditor.dispose();
            this._originalEditor.dispose();
            this.monacoInstance.dispose();
        }
    }

    initMonaco() {
        const { original={}, modified={}, language, options } = this.props;
        if (!this.monacoDom) {
            console.error("初始化dom节点出错");
            return;
        }

        window.MonacoEnvironment = {
            getWorkerUrl: function (moduleId, label) {
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
                return "./editor.worker.js";
            }
        };

        const editorOptions = Object.assign(defaultOptions, options, {
            originalEditable: options?!options.readOnly:true,//支持源可编辑
            renderIndicators: false,
        });

        this._originalModel = monaco.editor.createModel(original.value, language || "sql")
        this._modifiedModel = monaco.editor.createModel(modified.value, language || "sql")

        this.monacoInstance = monaco.editor.createDiffEditor(this.monacoDom, editorOptions);
        this.monacoInstance.setModel({
            original: this._originalModel,
            modified: this._modifiedModel
        })
        this._originalEditor = this.monacoInstance.getOriginalEditor();
        this._modifiedEditor = this.monacoInstance.getModifiedEditor();
        this._modifiedEditor.updateOptions({
            readOnly:true
        })
        if (this._originalEditor && original.cursorPosition) {
            this._originalEditor.setPosition(original.cursorPosition);
            this._originalEditor.focus();
            this._originalEditor.revealPosition(original.cursorPosition, monaco.editor.ScrollType.Immediate);
        }

        this.initEditor();
    }

    initEditor() {
        this.initTheme();
        this.initEditorEvent();
    }
    initTheme() {
        //hack 交换对比编辑器的位置
        monaco.editor.defineTheme('flippedDiffTheme', {
            base: 'vs',
            inherit: true,
            rules: [],
            colors: {
                'diffEditor.insertedTextBackground': '#ff000033',
                'diffEditor.removedTextBackground': '#28d22833'
            }
        });
        monaco.editor.setTheme("flippedDiffTheme");
    }
    updateValueWithNoEvent(value) {
        this._originalEditor.setValue(value);
    }

    initEditorEvent() {
        this._originalEditor.onDidChangeModelContent(event => {
            this.log("编辑器事件");
            const { onChange, value } = this.props;
            const newValue = this._originalEditor.getValue();
            if (onChange) {
                this.log("订阅事件触发");
                onChange(newValue, this._originalEditor);
            }
        });

        this._originalEditor.onDidBlurEditor(event => {
            this.log("编辑器事件 onDidBlur");
            const { onBlur, value } = this.props;
            if (onBlur) {
                const oldValue = this._originalEditor.getValue();
                onBlur(value, oldValue);
            }
        });

        this._originalEditor.onDidFocusEditor(event => {
            this.log("编辑器事件 onDidFocus");
            const { onFocus, value } = this.props;
            if (onFocus) {
                const oldValue = this._originalEditor.getValue();
                onFocus(value, oldValue);
            }
        });

        this._originalEditor.onDidChangeCursorSelection(event => {
            this.log("编辑器事件 onDidChangeCursorSelection");
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

    render() {
        const { className, style } = this.props;

        let renderClass = 'code-editor';
        renderClass = className ? `${renderClass} ${className}` : renderClass;

        let renderStyle = {
            position: 'relative',
            minHeight: "400px",
            // height: '100%',
            width: '100%',
            // marginTop:"20px"
        };

        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;

        return <div
            className={renderClass}
            style={renderStyle}
            ref={(domIns) => { this.monacoDom = domIns; }}
        />;
    }
}

DiffEditor.propTypes={
    /**
     * 该方法的入参为源文件Editor的引用
     */
    editorInstanceRef:PropTypes.function,
    /**
     * 源文件的属性对象
     * value:文件内容
     * cursorPosition:文件的指针位置
     */
    original:PropTypes.shape({
        value:PropTypes.string,
        cursorPosition:PropTypes.Object
    }),
    /**
     * 被对比文件的属性对象
     * value:文件内容
     */
    modified:PropTypes.shape({
        value:PropTypes.string
    }),
    /**
     * 源文件改变事件回调函数
     */
    onChange:PropTypes.function,
    /**
     * 源文件失去焦点回调函数
     */
    onBlur:PropTypes.function,
    /**
     * 源文件获得焦点回调函数
     */
    onFocus:PropTypes.function,
    /**
     * 文件指针改变事件回调函数
     */
    onCursorSelection:PropTypes.function,
    /**
     * 是否同步源文件内容
     */
    sync:PropTypes.bool,
    /**
     * 是否打印编辑器日志
     */
    isLog:PropTypes.bool
}
export default DiffEditor;
