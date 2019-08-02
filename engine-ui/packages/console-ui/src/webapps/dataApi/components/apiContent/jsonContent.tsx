import * as React from 'react';

class JsonContent extends React.Component<any, any> {
    renderJson (json: any) {
        if (!json) {
            return '暂无样例'
        }
        if (typeof json == 'object') {
            return JSON.stringify(json, null, '    \r');
        } else {
            return json;
        }
    }
    render () {
        let { className, json, style, others } = this.props;
        json = this.renderJson(json);
        return (
            <pre className={`c-json-content ${className || ''}`} {...others} style={style}>
                {json}
            </pre>
        )
    }
}
export default JsonContent;
