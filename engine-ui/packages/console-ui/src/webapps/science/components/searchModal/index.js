import React from 'react';

import { Modal, AutoComplete, Input } from 'antd';

class SearchModal extends React.Component {
    state = {
        data: []
    }
    onCancel = () => {
        this.props.onCancel();
    }
    onChange = () => {
        const { onChange } = this.props;
        if (onChange) {
            onChange(this.changeValue)
        }
    }
    onSelect = (value) => {
        this.props.onSelect(value);
    }
    changeValue = (values) => {
        this.setState({
            data: values
        })
    }
    render () {
        const { data } = this.state;
        const { visible, title } = this.props;
        return (
            <Modal
                visible={visible}
                onCancel={this.onCancel}
                footer={null}
                title={title || '搜索并打开'}
            >
                <AutoComplete
                    dataSource={data}
                    style={{ width: '100%', height: '28px', margin: '8px 0px' }}
                    onSelect={this.onSelect}
                    onChange={this.onChange}
                >
                    <Input.Search />
                </AutoComplete>
            </Modal>
        )
    }
}
export default SearchModal;
