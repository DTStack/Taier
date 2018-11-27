import React, { Component } from 'react';
import { Modal } from 'antd';

import Editor from 'widgets/code-editor';

import { propEditorOptions } from 'widgets/code-editor/config';

class EnvModal extends Component {
    state = {
        changeVal: ''
    };

    onChange = (old, value) => {
        this.setState({
            changeVal: value
        });
    };

    onOk = () => {
        const value = this.state.changeVal || this.props.value;
        this.props.onOk(value);
    };

    render () {
        const { value, visible, title, key, onCancel } = this.props;
        return (
            <Modal
                key={key}
                visible={visible}
                title={title}
                onOk={this.onOk}
                onCancel={onCancel}
                bodyStyle={{
                    height: 400,
                    padding: 0
                }}
            >
                <Editor
                    options={propEditorOptions}
                    value={value}
                    onChange={this.onChange}
                    sync
                />
            </Modal>
        );
    }
}

export default EnvModal;
