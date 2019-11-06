import * as React from 'react'

import { defaultEditorOptions } from '../../../../widgets/code-editor/config'
import pureRender from 'utils/pureRender'

const codemirror = require('codemirror')

// require('codemirror/addon/fold/foldcode')
// require('codemirror/addon/fold/foldgutter')
// require('codemirror/addon/fold/brace-fold')

require('codemirror/mode/sql/sql')
require('codemirror/mode/python/python')
require('codemirror/mode/shell/shell')
require('codemirror/mode/javascript/javascript')
require('codemirror/mode/properties/properties')
require('codemirror/addon/display/placeholder')
require('codemirror/addon/edit/matchbrackets')

// require('codemirror/addon/lint/lint')
// require('../../assets/js/sql-lint')

@pureRender
class CodeEditor extends React.Component<any, any> {
    Editor: any;
    self: any;
    componentDidMount () {
        const ele = this.Editor
        const options = this.props.options || defaultEditorOptions
        const instance = this.getCodeMirrorIns()
        const {
            value, onChange, onFocus, cursor,
            focusOut, cursorActivity
        } = this.props

        if (!ele) return;
        this.self = instance.fromTextArea(ele, options);

        // 设置corsor位置
        if (cursor) this.self.doc.setCursor(cursor)

        this.self.on('change', (doc: any) => {
            if (onChange) {
                onChange(value, doc.getValue(), doc)
            }
        })
        this.self.on('focus', (doc: any) => {
            if (onFocus) {
                onFocus(value, doc.getValue())
            }
        })

        this.self.on('blur', (doc: any) => {
            if (focusOut) {
                focusOut(value, doc.getValue())
            }
        })

        this.self.on('cursorActivity', (doc: any) => {
            if (cursorActivity) {
                cursorActivity(value, doc)
            }
        })
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { value, sync, cursor, cursorAlwaysInEnd, options } = nextProps
        if (options) this.self.setOption('readOnly', options.readOnly)

        if (this.props.value !== value) {
            if (cursor) this.self.doc.setCursor(cursor)
            if (sync) {
                if (!value) this.self.setValue('')
                else this.self.setValue(value)
                if (cursorAlwaysInEnd) {
                    this.self.doc.setCursor(this.self.doc.lineCount(), null);
                }
            }
        }
    }

    getCodeMirrorIns () {
        return this.self || codemirror
    }

    render () {
        const { className, style } = this.props
        let renderClass = 'code-editor'
        renderClass = className
            ? `${renderClass} ${className}` : renderClass
        let renderStyle: any = {
            position: 'relative',
            minHeight: '400px'
        }
        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle

        return (
            <div
                className={renderClass}
                style={renderStyle}>
                <textarea
                    ref={(e: any) => { this.Editor = e }}
                    name="code"
                    placeholder={this.props.placeholder || ''}
                    defaultValue={this.props.value || ''}
                />
            </div>
        )
    }
}

export default CodeEditor
