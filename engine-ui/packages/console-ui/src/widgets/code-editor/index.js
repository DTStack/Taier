import React, { Component } from 'react'

import { defaultEditorOptions } from './config'
import pureRender from 'utils/pureRender'
// Codemirror
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/lint/lint.css'
import 'codemirror/addon/scroll/simplescrollbars.css'
import "./style.css"

const codemirror = require('codemirror')
import { getLinkMark } from "./utils"

// require('codemirror/addon/fold/foldcode')
// require('codemirror/addon/fold/foldgutter')
// require('codemirror/addon/fold/brace-fold')

require('codemirror/mode/textile/textile')
require('codemirror/mode/sql/sql')
require('codemirror/mode/python/python')
require('codemirror/mode/javascript/javascript')
require('codemirror/mode/properties/properties')
require('codemirror/addon/display/placeholder')
require("codemirror/addon/edit/matchbrackets")
require("codemirror/addon/scroll/simplescrollbars")

// require('codemirror/addon/lint/lint')
// require('../../assets/js/sql-lint')

@pureRender
class CodeEditor extends Component {

    componentDidMount() {
        const ctx = this
        const ele = this.Editor
        const options = this.props.options || defaultEditorOptions
        const instance = this.getCodeMirrorIns()
        const {
            value, onChange, onFocus, cursor,
            focusOut, cursorActivity, editorRef
        } = this.props

        if (!ele) return;
        this.self = instance.fromTextArea(ele, options);
        this.renderTextMark();
        // 设置corsor位置
        if (cursor) this.self.doc.setCursor(cursor)

        this.self.on('change', (doc) => {
            if (onChange) {
                onChange(value, doc.getValue(), doc)
            }
        })
        this.self.on('focus', (doc) => {
            if (onFocus) {
                onFocus(value, doc.getValue())
            }
        })

        this.self.on('blur', (doc) => {
            if (focusOut) {
                focusOut(value, doc.getValue())
            }
        })

        this.self.on('cursorActivity', (doc) => {
            if (cursorActivity) {
                cursorActivity(value, doc)
            }
        })
        if (editorRef) {
            editorRef(this.self);
        }
    }

    componentWillReceiveProps(nextProps) {
        const { value, sync, cursor, placeholder, cursorAlwaysInEnd, options = {} } = nextProps
        if (options) {
            this.self.setOption('readOnly', options.readOnly)
        }
        if (placeholder != this.props.placeholder) {
            this.self.setOption('placeholder', placeholder)
        }
        if (this.props.value !== value) {
            if (cursor) this.self.doc.setCursor(cursor)
            if (sync) {
                window.ted = this.self;
                const scrollInfo = this.self.getScrollInfo();
                /**
                 * 判断滚动条是不是在底部
                 */
                const isInBottom = (scrollInfo.top + scrollInfo.clientHeight) - scrollInfo.height > -10;
                console.log(isInBottom);
                if (!value) {
                    this.self.setValue('')
                }
                else {
                    this.self.setValue(value);
                }
                if (cursorAlwaysInEnd) {
                    this.self.doc.setCursor(line, null);
                } else if (!isInBottom) {
                    /**
                    * 不在底部并且不设置自动滚到底部，则滚到原来位置
                    */
                    Promise.resolve().then(() => {
                        this.self.scrollTo(scrollInfo.left, scrollInfo.top)
                    })
                } else if (isInBottom) {
                    /**
                     * 在底部，则自动到底部
                     */
                    Promise.resolve().then(() => {
                        this.self.scrollTo(scrollInfo.left, scrollInfo.height)
                    })
                }
            }
            this.renderTextMark();
        }
    }
    renderTextMark() {
        const marks = this.self.doc.getAllMarks();
        for (let mark of marks) {//重置marks
            mark.clear();
        }
        const value = this.self.getValue();
        const linkMarks = getLinkMark(value);
        for (let _i = 0; _i < linkMarks.length; _i++) {
            let mark = linkMarks[_i];
            this.self.doc.markText(
                this.self.doc.posFromIndex(mark.start),
                this.self.doc.posFromIndex(mark.end),
                { replacedWith: mark.node }
            )
        }
    }
    getCodeMirrorIns() {
        return this.self || codemirror
    }

    render() {
        const { className, style } = this.props
        let renderClass = 'code-editor'
        renderClass = className ?
            `${renderClass} ${className}` : renderClass
        let renderStyle = {
            position: 'relative',
            minHeight: "400px"
        }
        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle

        return (
            <div
                className={renderClass}
                style={renderStyle}>
                <textarea
                    ref={(e) => { this.Editor = e }}
                    name="code"
                    placeholder={this.props.placeholder || ''}
                    defaultValue={this.props.value || ''}
                />
            </div>
        )
    }
}

export default CodeEditor
