import React from 'react';
import {
    Modal, Input, message
} from 'antd';

import utils from 'utils'
import HelpDoc, { relativeStyle } from '../../../../helpDoc';
export default class ConstModal extends React.Component {
    state = {
        constValue: ''
    }

    onChange = (e) => {
        this.setState({
            constValue: e.target.value
        })
    }

    submit = () => {
        const { onOk } = this.props
        const constValue = utils.trim(this.state.constValue)
        if (constValue !== '') {
            const constObj = {
                type: 'string',
                key: constValue,
                value: constValue
            }
            if (onOk) {
                onOk(constObj);
                this.close();
            }
        } else {
            message.error('常量字段不可为空！')
        }
    }

    close = () => {
        const { onCancel } = this.props
        this.setState({ constValue: '' }, () => {
            if (onCancel) onCancel()
        })
    }

    render () {
        const { constValue } = this.state
        const { visible, onCancel } = this.props

        return (
            <Modal
                title="添加常量"
                onOk={this.submit}
                onCancel={this.close}
                visible={visible}>
                <Input
                    value={constValue}
                    onChange={this.onChange}
                    placeholder="请输入常量值"
                />
                <p style={{ marginTop: '10px' }}>1.输入的常量值将会被英文单引号包括，如'abc'、'123'等；</p>
                <p>2.可以配合调度参数<HelpDoc style={relativeStyle} doc="customSystemParams" /> 使用，如 ${`{bdp.system.bizdate}`}
                等；</p>
                <p>3.如果您输入的值无法解析，则类型显示为'未识别'；</p>
            </Modal>
        )
    }
}
