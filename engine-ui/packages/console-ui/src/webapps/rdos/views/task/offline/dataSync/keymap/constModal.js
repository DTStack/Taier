import React from 'react';
import {
    Modal, Input, message,
} from 'antd';

import utils from 'utils'
import HelpDoc, { relativeStyle } from '../../../../helpDoc';
import { disposeProvider } from 'widgets/editor/languages/dtsql/simpleComplete';
export default class ConstModal extends React.Component {

    state = {
        constValue: '',
        constName: '',
    }

    onChange = (e) => {
        this.setState({
            constValue: e.target.value
        })
    }

    onNameChange = (e) => {
        this.setState({
            constName: e.target.value
        })
    }

    submit = () => {
        const { onOk } = this.props
        const constValue = utils.trim(this.state.constValue)
        const constName = utils.trim(this.state.constName)


        if (constName === '') {
            message.error('常量名称不可为空！');
            return;
        }

        if (constValue === '') {
            message.error('常量值不可为空！');
            return;
        }
        const constObj = {
            type: 'string',
            key: constName,
            value: constValue
        }
        if (onOk) {
            onOk(constObj);
            this.close();
        }
    }

    close = () => {
        const { onCancel } = this.props
        this.setState({ constValue: '', constName: '' }, () => {
            if (onCancel) onCancel()
        })
    }

    render() {
        const { constValue, constName } = this.state
        const { visible } = this.props
        
        return (
            <Modal
                title="添加常量"
                onOk={this.submit}
                onCancel={this.close}
                visible={visible}>
                <Input
                    value={constName}
                    onChange={this.onNameChange}
                    placeholder="请输入常量名称"
                />
                <Input
                    style={{ marginTop: '10px' }}
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