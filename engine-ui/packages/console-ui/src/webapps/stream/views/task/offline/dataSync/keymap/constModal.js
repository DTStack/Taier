import React from 'react';
import {
    Modal, Input, message,
} from 'antd';

import utils from 'utils'

export default class ConstModal extends React.Component {

    state = {
        constValue: '',
    }

    onChange = (e) => {
        this.setState({
            constValue: e.target.value
        })
    }

    submit = () => {
        const { onOk, onCancel } = this.props
        const constValue = utils.trim(this.state.constValue)
        if (constValue !== '') {
            const constObj = {
                type: 'string',
                key: constValue,
                value: constValue
            }
            if (onOk) {
                onOk(constObj) 
                onCancel()
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

    render() {
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
            </Modal>
        )
    }
}