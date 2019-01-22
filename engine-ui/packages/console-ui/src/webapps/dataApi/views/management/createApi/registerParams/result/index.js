import React from 'react';

import { Tooltip, Icon } from 'antd';

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
                    title={<span>
                        正常返回示例
                        <Tooltip title="若定义正常返回示例，用户申请API被授权后，可在API详情中查看，帮助用户更好的完成API调用。">
                            <Icon style={{ marginLeft: '5px' }} type="question-circle-o" />
                        </Tooltip>
                    </span>}
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
                    title={<span>
                        错误返回示例
                        <Tooltip title="若定义错误返回示例，用户申请API被授权后，可在API详情中查看，帮助用户更好的完成API调用。">
                            <Icon style={{ marginLeft: '5px' }} type="question-circle-o" />
                        </Tooltip>
                    </span>}
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
