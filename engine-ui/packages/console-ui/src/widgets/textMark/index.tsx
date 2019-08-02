import * as React from 'react';

class TextMark extends React.Component<any, any> {
    renderMark (text = '', markText = '') {
        const markTextIndex = text.indexOf(markText);
        if (markTextIndex !== -1) {
            return <span>
                {text.substring(0, markTextIndex)}
                <mark>{markText}</mark>
                {text.substring(markTextIndex + markText.length, text.length)}
            </span>
        } else {
            return text;
        }
    }

    render () {
        const { text, markText, ...others } = this.props;
        return (
            <span {...others}>
                {this.renderMark(text, markText)}
            </span>
        )
    }
}

export default TextMark;
