import React, { Component, PropTypes } from "react";
import monaco from 'monaco-editor';


class IDEEditor extends Component {

    componentDidMount() {
        monaco.editor.create(this.Editor, {
            value: "function hello() {\n\talert('Hello world!');\n}",
            language: "javascript"
        });
    }

    render() {
        const { className, style } = this.props;
        let renderClass = "ide-editor";
        renderClass = className ? `${renderClass} ${className}` : renderClass;
        let renderStyle = {
            position: "relative",
            minHeight: "400px"
        };
        renderStyle = style ? Object.assign(renderStyle, style) : renderStyle;

        return (
            <div className={renderClass} style={renderStyle}>
                <textarea
                    ref={e => {
                        this.Editor = e;
                    }}
                    name="code"
                    placeholder={this.props.placeholder || ""}
                    defaultValue={this.props.value || ""}
                />
            </div>
        );
    }
}

export default IDEEditor;
