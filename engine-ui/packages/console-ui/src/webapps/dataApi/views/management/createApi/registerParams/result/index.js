import React from 'react';

import Card from '../card';
import Editor from 'widgets/editor';

const editorStyle = {
    height: '218px',
    minHeight: '218px',
    border: '1px solid #DDDDDD'
}
class RegisterResult extends React.Component {
    state = {
        sync: true
    }
    editorChange (type, value) {
        if (type == 'normal') {
            this.props.updateData({
                successValue: value
            })
        } else if (type == 'error') {
            this.props.updateData({
                errorValue: value
            })
        }
        this.setState({
            sync: false
        })
    }
    render () {
        const { sync } = this.state;
        const { data } = this.props;
        const { successValue, errorValue } = data;
        return (
            <React.Fragment>
                <Card
                    title='正常返回示例'
                >
                    <Editor
                        sync={sync}
                        onChange={this.editorChange.bind(this, 'normal')}
                        language='json'
                        style={editorStyle}
                        options={{ minimap: { enabled: false } }}
                        value={successValue}
                    />
                </Card>
                <Card
                    title='错误返回示例'
                    style={{ marginTop: '32px' }}
                >
                    <Editor
                        sync={sync}
                        onChange={this.editorChange.bind(this, 'error')}
                        language='json'
                        style={editorStyle}
                        options={{ minimap: { enabled: false } }}
                        value={errorValue}
                    />
                </Card>
            </React.Fragment>
        )
    }
}
export default RegisterResult;
