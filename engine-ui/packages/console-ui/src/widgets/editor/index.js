import React from "react";

import 'monaco-editor/esm/vs/editor/browser/controller/coreCommands.js';
import 'monaco-editor/esm/vs/editor/contrib/find/findController.js';
import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import 'monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js';
import './style.scss';

class Editor extends React.Component {

    constructor(props) {
        super(props);
        this.monacoDom = React.createRef();
        this.monacoInstance = null;
        this.__props_update = false;//非输入触发标志
    }
    log(){
        const {isLog} = this.props;
        isLog&&console.log(...arguments)
    }
    componentDidMount() {
        this.initMonaco();
        
    }
    isValueExist(props) {
        const keys = Object.keys(props);
        if (keys.includes("value")) {
            return true;
        }
        return false;
    }
    componentWillReceiveProps(nextProps) {

        if (
            (this.isValueExist(nextProps) && nextProps.value != this.monacoInstance.getValue())
            ||
            nextProps.value != this.props.value
        ) {
            this.log("props更新")
            this.updateValueWithNoEvent(nextProps.value);
        }
    }
    componentWillUnmount() {
        this.destroyMonaco();
    }
    destroyMonaco() {
        if (this.monacoInstance) {
            this.monacoInstance.dispose();
        }
    }
    initMonaco() {
        const { value, defaultValue, language } = this.props;
        const initValue = value || defaultValue;
        if (!this.monacoDom) {
            console.error("初始化dom节点出错");
            return;
        }
        window.MonacoEnvironment = {
            getWorkerUrl: function (moduleId, label) {
                console.log(arguments);
                if (label === 'json') {
                    return './json.worker.js';
                }
                if (label === 'css') {
                    return './css.worker.js';
                }
                if (label === 'html') {
                    return './html.worker.js';
                }
                if (label === 'typescript' || label === 'javascript') {
                    return './typescript.worker.js';
                }
                if(label==="sql"){
                    return "./sql.worker.js";
                }
                return './editor.worker.js';
            }
        }
        const model=monaco.editor.createModel(initValue,language||"javascript");
        this.monacoInstance = monaco.editor.create(this.monacoDom.current, {
            model:model
        })
        this.initEditor();
    }
    initEditor() {
        this.initEditorEvent();
    }
    updateValueWithNoEvent(value){
        this.__props_update=true;
        this.monacoInstance.setValue(value);
        this.__props_update=false;
    }
    initEditorEvent() {
        this.monacoInstance.onDidChangeModelContent((event) => {
            this.log("编辑器事件")
            const { onChange } = this.props;
            const newValue=this.monacoInstance.getValue();
            //假如是双向绑定，并且是输入触发的，则禁止更新
            if(this.isValueExist(this.props)&&!this.__props_update){
                this.log("双向绑定禁止更新")
                this.updateValueWithNoEvent(this.props.value);
            }
            if (onChange && !this.__props_update) {
                this.log("订阅事件触发")
                onChange(newValue, event);
            }

        })
    }
    render() {
        return (
            <div ref={this.monacoDom} className="monaco-view">
            </div>
        )
    }
}
export default Editor;