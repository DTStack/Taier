import React from "react";

// import 'monaco-editor/esm/vs/editor/browser/controller/coreCommands.js';
// import 'monaco-editor/esm/vs/editor/contrib/find/findController.js';
// import 'monaco-editor/esm/vs/editor/contrib/folding/folding.js';
// import 'monaco-editor/esm/vs/editor/contrib/contextmenu/contextmenu.js';
// import 'monaco-editor/esm/vs/editor/contrib/smartSelect/smartSelect.js';

// import 'monaco-editor/esm/vs/editor/editor.all.js';
// import * as monaco from 'monaco-editor/esm/vs/editor/editor.api.js';
import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
// import 'monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js';
// import "monaco-editor/esm/vs/basic-languages/python/python.contribution.js";

// monaco 当前版本并未集成最新basic-languages， 暂时shell单独引入
import "./languages/shell/shell.contribution.js";
import * as dtsql from "./languages/dtsql/dtsql.contribution.js"
import "./languages/dt-flink/dtflink.contribution.js"
import "./languages/dtlog/dtlog.contribution.js"

import "./style.scss";
import whiteTheme from "./theme/whiteTheme";
import { defaultOptions } from './config';

const provideCompletionItemsMap = {
    dtsql: {
        /**
         * 注册自定义补全函数
         */
        register: dtsql.registeCompleteItemsProvider,
        /**
         * 释放自定义补全函数
         */
        dispose: dtsql.disposeProvider,
        /**
         * value改变事件注册函数
         */
        onChange: dtsql.onChange
    }
}
/**
 * 该函数delaytime时间内顶多执行一次func（最后一次），如果freshTime时间内没有执行，则强制执行一次。
 * @param {function} func 
 */
function delayFunctionWrap(func){
    /**
     * 最小执行间隔，每隔一段时间强制执行一次函数
     * 这里不能太小，因为太小会导致大的解析任务没执行完阻塞。
     */
    let freshTime=3000;
    /**
     * 函数延迟时间
     */
    let delayTime=500;

    let outTime;
    let _timeClock;
    return function(){
        const arg=arguments;
        _timeClock&&clearTimeout(_timeClock);
        //这边设置在一定时间内，必须执行一次函数
        if(outTime){
            let now=new Date();
            if(now-outTime>freshTime){
                func(...arg);
            }
        }else{
            outTime=new Date();
        }
        _timeClock=setTimeout(()=>{
            outTime=null;
            func(...arg);
        },delayTime)
    }
}
class Editor extends React.Component {

    constructor(props) {
        super(props);
        this.monacoDom = null;
        this.monacoInstance = null;
        this._linkId=null;
    }

    
    shouldComponentUpdate(nextProps, nextState) {
        // // 此处禁用render， 直接用editor实例更新编辑器
        return false;
    }

    componentDidMount() {
        this.initMonaco();
        if (typeof this.props.editorInstanceRef == "function") {
            this.props.editorInstanceRef(this.monacoInstance)
        }
    }
    /**
     * 补全代理函数，来执行用户自定义补全方法。
     */
    providerProxy = (completeItems, resolve, customCompletionItemsCreater, ext) => {
        const { customCompleteProvider } = this.props;
        if (customCompleteProvider) {
            customCompleteProvider(completeItems, resolve, customCompletionItemsCreater, ext);
        } else {
            resolve(completeItems)
        }
    }

    initProviderProxy() {
        const keyAndValues = Object.entries(provideCompletionItemsMap);
        for (let [type, language] of keyAndValues) {
            /**
             * 每个函数的补全函数都由该组件统一代理
             */
            language.register(this.providerProxy);
        }
    }
    disposeProviderProxy() {
        const keyAndValues = Object.entries(provideCompletionItemsMap);
        for (let [type, language] of keyAndValues) {
            language.dispose();
        }
    }
    componentWillReceiveProps(nextProps) {
        const { sync, value, theme, languageConfig, language, download } = nextProps;
        if (this.props.value !== value && sync) {
            /**
             * value更新， 并且含有sync同步标记，则更新编辑器值
             */
            const editorText = !value ? '' : value;
            this.updateValueWithNoEvent(editorText);
        }
        if (languageConfig !== this.props.languageConfig) {
            this.updateMonarch(languageConfig, language)
        }
        if (this.props.language !== nextProps.language) {
            monaco.editor.setModelLanguage(this.monacoInstance.getModel(),nextProps.language)
        }
        if (this.props.options !== nextProps.options) {
            this.monacoInstance.updateOptions(nextProps.options)
        }

        if (this.props.theme !== theme) {
            monaco.editor.setTheme(theme)
        }
        // if(this.props.download!==download){
        //     this.initLink(download);
        // }
    }

    componentWillUnmount() {
        this.destroyMonaco();
        this.disposeProviderProxy();
    }
    /**
     * 提供下载链接。ps:不是很好用，屏蔽了
     * @param {string} link 
     */
    initLink(link){
        this.monacoInstance.changeViewZones(
            (changeAccessor)=>{
                if(this._linkId){
                    changeAccessor.removeZone(this._linkId);
                }
                let boxNode=document.createElement("div");
                let domNode=document.createElement("a");
                domNode.innerHTML="完整下载链接";
                domNode.className="dt-monaco-link";
                domNode.setAttribute("href",link);
                domNode.setAttribute("download",'');
                boxNode.appendChild(domNode);
                this._linkId=changeAccessor.addZone({
                    afterLineNumber: 0,
					heightInLines: 1,
					domNode: boxNode
                })
            }   
        )
    }
    updateMonarch(config, language) {
        if (config && language) {
            monaco.languages.setMonarchTokensProvider(language, config);
        }
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
        if (!this.monacoDom) {
            console.error("初始化dom节点出错");
            return;
        }


        const editorOptions = Object.assign({},defaultOptions, options, {
            value,
            language: language || "sql"
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
        this.initTheme();
        this.initEditorEvent();
        this.initProviderProxy();
        // this.initLink();
    }
    initTheme() {
        monaco.editor.defineTheme("white", whiteTheme);
        this.props.theme && monaco.editor.setTheme(this.props.theme);
    }
    updateValueWithNoEvent(value) {
        this.monacoInstance.setValue(value);
    }
    languageValueOnChange(callback) {
        const newValue = this.monacoInstance.getValue();
        const languageId = this.monacoInstance.getModel().getModeId();
        if (provideCompletionItemsMap[languageId] && provideCompletionItemsMap[languageId].onChange) {
            provideCompletionItemsMap[languageId].onChange(newValue, this.monacoInstance, callback);
        }
    }

    delayLanguageValueOnChange=delayFunctionWrap(this.languageValueOnChange.bind(this))

    initEditorEvent() {
        this.languageValueOnChange(this.props.onSyntaxChange);
        this.monacoInstance.onDidChangeModelContent(event => {
            this.log("编辑器事件");
            const { onChange, value, onSyntaxChange } = this.props;
            const newValue = this.monacoInstance.getValue();
            //考虑到语法解析比较耗时，所以把它放到一个带有调用延迟的函数中，并且提供一个可供订阅的onSyntaxChange函数
            this.delayLanguageValueOnChange(onSyntaxChange);
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
        const { className, style, editorInstance } = this.props;

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
