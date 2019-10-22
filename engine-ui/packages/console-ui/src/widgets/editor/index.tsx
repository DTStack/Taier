import * as React from 'react';
// import * as monaco from 'monaco-editor/esm/vs/editor/edcore.main.js';
import * as monaco from 'monaco-editor';

// monaco 当前版本并未集成最新basic-languages， 暂时shell单独引入
import './languages/shell/shell.contribution';
import * as dtsql from './languages/dtsql/dtsql.contribution'
import * as dtflink from './languages/dt-flink/dtflink.contribution'
import './languages/dtlog/dtlog.contribution'

import './style.scss';
import whiteTheme from './theme/whiteTheme';
import { defaultOptions } from './config';
import { jsonEqual, delayFunctionWrap } from './utils';

/**
 * 要注册的语言补全功能所需要实现的接口
 * register 注册补全
 * dispose 取消注册
 * onChange value改变事件
 */
const provideCompletionItemsMap: any = {
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
    },
    dtflink: {
        /**
         * 注册自定义补全函数
         */
        register: dtflink.registeCompleteItemsProvider,
        /**
         * 释放自定义补全函数
         */
        dispose: dtflink.disposeProvider,
        onChange: dtflink.onChange
    }
}
class Editor extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }
    /**
     * monaco需要的渲染节点
     */
    monacoDom: any = null;
    /**
     * monaco实例
     */
    monacoInstance: monaco.editor.IStandaloneCodeEditor = null;
    /**
     * monaco渲染外部链接对象的销毁用ID
     */
    _linkId: any = null;

    shouldComponentUpdate (nextProps: any, nextState: any) {
        // 此处禁用render， 直接用editor实例更新编辑器
        return false;
    }

    componentDidMount () {
        this.initMonaco();
        if (typeof this.props.editorInstanceRef === 'function') {
            this.props.editorInstanceRef(this.monacoInstance)
        }
    }
    /**
     * 补全代理函数，来执行用户自定义补全方法。
     */
    providerProxy = (completeItems: any, resolve: any, customCompletionItemsCreater: any, ext: any) => {
        const { customCompleteProvider } = this.props;
        if (customCompleteProvider) {
            customCompleteProvider(completeItems, resolve, customCompletionItemsCreater, ext);
        } else {
            resolve(completeItems)
        }
    }

    initProviderProxy () {
        const keyAndValues = Object.entries(provideCompletionItemsMap);
        for (let [, language] of keyAndValues) {
            /**
             * 每个函数的补全函数都由该组件统一代理
             */
            (language as any).register(this.providerProxy, this.monacoInstance);
        }
    }
    disposeProviderProxy () {
        const keyAndValues = Object.entries(provideCompletionItemsMap);
        for (let [, language] of keyAndValues) {
            (language as any).dispose(this.monacoInstance);
        }
    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { sync, value, theme, languageConfig, language } = nextProps;
        if (this.props.value !== value && sync) {
            /**
             * value更新， 并且含有sync同步标记，则更新编辑器值
             */
            const editorText = !value ? '' : value;
            this.updateValueWithNoEvent(editorText);
        }
        if (languageConfig !== this.props.languageConfig) {
            if (!jsonEqual(languageConfig, this.props.languageConfig)) {
                this.updateMonarch(languageConfig, language)
            }
        }
        if (this.props.language !== nextProps.language) {
            monaco.editor.setModelLanguage(this.monacoInstance.getModel(), nextProps.language)
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

    componentWillUnmount () {
        this.disposeProviderProxy();
        this.destroyMonaco();
    }
    /**
     * 提供下载链接。ps:不是很好用，屏蔽了
     * @param {string} link
     */
    initLink (link: any) {
        this.monacoInstance.changeViewZones(
            (changeAccessor: any) => {
                if (this._linkId) {
                    changeAccessor.removeZone(this._linkId);
                }
                let boxNode = document.createElement('div');
                let domNode = document.createElement('a');
                domNode.innerHTML = '完整下载链接';
                domNode.className = 'dt-monaco-link';
                domNode.setAttribute('href', link);
                domNode.setAttribute('download', '');
                boxNode.appendChild(domNode);
                this._linkId = changeAccessor.addZone({
                    afterLineNumber: 0,
                    heightInLines: 1,
                    domNode: boxNode
                })
            }
        )
    }
    updateMonarch (config: any, language: any) {
        if (config && language) {
            if (config) { monaco.languages.setMonarchTokensProvider(language, config); }
        }
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
            this.monacoInstance.dispose();
        }
    }

    initMonaco () {
        const { value, language, options, cursorPosition } = this.props;
        if (!this.monacoDom) {
            console.error('初始化dom节点出错');
            return;
        }

        const editorOptions = Object.assign({}, defaultOptions, options, {
            value,
            language: language || 'sql'
        });

        this.monacoInstance = monaco.editor.create(this.monacoDom, editorOptions);

        if (this.monacoInstance && cursorPosition) {
            this.monacoInstance.setPosition(cursorPosition);
            this.monacoInstance.focus();
            this.monacoInstance.revealPosition(cursorPosition, monaco.editor.ScrollType.Immediate);
        }

        this.initEditor();
    }

    initEditor () {
        this.initTheme();
        this.initEditorEvent();
        this.initProviderProxy();
        // this.initLink();
    }
    initTheme () {
        monaco.editor.defineTheme('white', whiteTheme);
        this.props.theme && monaco.editor.setTheme(this.props.theme);
    }
    updateValueWithNoEvent (value: any) {
        this.monacoInstance.setValue(value);
    }
    languageValueOnChange (callback: any) {
        if (this.props.disabledSyntaxCheck) {
            return;
        }
        const newValue = this.monacoInstance.getValue();
        const languageId = this.monacoInstance.getModel().getModeId();
        if (provideCompletionItemsMap[languageId] && provideCompletionItemsMap[languageId].onChange) {
            provideCompletionItemsMap[languageId].onChange(newValue, this.monacoInstance, callback);
        }
    }

    delayLanguageValueOnChange: any = delayFunctionWrap(this.languageValueOnChange.bind(this))

    initEditorEvent () {
        this.languageValueOnChange(this.props.onSyntaxChange);
        this.monacoInstance.onDidChangeModelContent((event: any) => {
            this.log('编辑器事件');
            const { onChange, onSyntaxChange } = this.props;
            const newValue = this.monacoInstance.getValue();
            // 考虑到语法解析比较耗时，所以把它放到一个带有调用延迟的函数中，并且提供一个可供订阅的onSyntaxChange函数
            this.delayLanguageValueOnChange(onSyntaxChange);
            if (onChange) {
                this.log('订阅事件触发');
                onChange(newValue, this.monacoInstance);
            }
        });

        this.monacoInstance.onDidBlurEditor(() => {
            this.log('编辑器事件 onDidBlur');
            const { onBlur, value } = this.props;
            if (onBlur) {
                const oldValue = this.monacoInstance.getValue();
                onBlur(value, oldValue);
            }
        });

        this.monacoInstance.onDidFocusEditor(() => {
            this.log('编辑器事件 onDidFocus');
            const { onFocus, value } = this.props;
            if (onFocus) {
                const oldValue = this.monacoInstance.getValue();
                onFocus(value, oldValue);
            }
        });

        this.monacoInstance.onDidChangeCursorSelection((event: any) => {
            this.log('编辑器事件 onDidChangeCursorSelection');
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
        /**
         * 改变contextMenu的定位为fixed，避免容器内overflow:hidden属性截断contextMenu
         */
        this.monacoInstance.onContextMenu((e: any) => {
            this.log('编辑器事件 onContextMenu');
            const contextMenuElement = this.monacoInstance.getDomNode().querySelector<HTMLElement>('.monaco-menu-container');

            if (contextMenuElement) {
                const posY = (e.event.posy + contextMenuElement.clientHeight) > window.innerHeight
                    ? e.event.posy - contextMenuElement.clientHeight
                    : e.event.posy;

                const posX = (e.event.posx + contextMenuElement.clientWidth) > window.innerWidth
                    ? e.event.posx - contextMenuElement.clientWidth
                    : e.event.posx;

                contextMenuElement.style.position = 'fixed';
                contextMenuElement.style.top = Math.max(0, Math.floor(posY)) + 'px';
                contextMenuElement.style.left = Math.max(0, Math.floor(posX)) + 'px';
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
            height: '100%',
            width: '100%'
        };

        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;

        return <div
            className={renderClass}
            style={renderStyle}
            ref={(domIns: any) => { this.monacoDom = domIns; }}
        />;
    }
}
export default Editor;
