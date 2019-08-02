import * as React from 'react'

import 'codemirror/addon/merge/merge.css'
import './style.css';

const codemirror = require('codemirror')

require('codemirror/mode/sql/sql')
require('public/stream/js/merge')

class DiffEditor extends React.Component<any, any> {
    diffView: any;
    _self: any;
    componentDidMount () {
        const { value, compareTo } = this.props
        this.initUI(value, compareTo)
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const { value, compareTo, tableRefresh } = nextProps
        if (tableRefresh) {
            this._self.edit.doc.setValue(value)
            this._self.right.orig.doc.setValue(compareTo)
        }
        if (value && this.props.value !== value) {
            this._self.edit.doc.setValue(value)
        }
        if (compareTo && this.props.compareTo !== compareTo) {
            this._self.right.orig.doc.setValue(compareTo)
        }
    }

    initUI = (value: any, compareTo: any) => {
        const { onChange, readOnly } = this.props

        const instance = this.getCodeMirrorIns();

        const mv = instance.MergeView(this.diffView, {
            value: value,
            origLeft: null,
            orig: compareTo,
            lineNumbers: true,
            mode: 'text/x-sql',
            highlightDifferences: true,
            connect: null,
            collapseIdentical: false,
            readOnly: readOnly
        });

        mv.edit.doc.on('change', (doc: any) => {
            if (onChange) {
                onChange(value, doc.getValue())
            }
        })

        // this.resize(mv)
        mv.wrap.style.height = '500px';
        this._self = mv
    }

    mergeViewHeight (mergeView: any) {
        function editorHeight (editor: any) {
            if (!editor) return 0;
            return editor.getScrollInfo().height;
        }
        return Math.max(editorHeight(mergeView.leftOriginal()),
            editorHeight(mergeView.editor()),
            editorHeight(mergeView.rightOriginal()));
    }

    resize (mergeView: any) {
        var height = this.mergeViewHeight(mergeView);
        for (; ;) {
            if (mergeView.leftOriginal()) { mergeView.leftOriginal().setSize(null, height); }
            mergeView.editor().setSize(null, height);
            if (mergeView.rightOriginal()) { mergeView.rightOriginal().setSize(null, height); }
            var newHeight = this.mergeViewHeight(mergeView);
            if (newHeight >= height) break;
            else height = newHeight;
        }
        mergeView.wrap.style.height = height + 'px';
    }

    getCodeMirrorIns () {
        return this._self || codemirror
    }

    render () {
        const { className, style } = this.props
        let renderClass = 'code-editor merge-text'
        renderClass = className
            ? `${renderClass} ${className}` : renderClass
        let renderStyle: any = {
            position: 'relative',
            borderTop: '1px solid #e9e9e9',
            marginTop: '20px'
        }
        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle

        return (
            <div
                ref={(e: any) => { this.diffView = e }}
                className={renderClass}
                style={renderStyle} />
        )
    }
}

export default DiffEditor
