import {
    Modal, Select
} from 'antd';
import React, { PureComponent } from 'react';

const Option = Select.Option;

class SearchModal extends PureComponent {
    state = {
        selectValue: ''
    }
    componentDidUpdate (prevProps, prevState) {
        if (this.props.visible && !prevProps.visible) {
            this.setState({
                selectValue: ''
            });
        }
    }
    handleChange = (value) => {
        const { onChange } = this.props;
        this.setState({
            selectValue: value
        })
        onChange(value);
        console.log('onchange:', value);
    }
    handleSelect = (value, option) => {
        const { onSelect } = this.props;
        onSelect(value, option)
        console.log(value, option)
    }
    render () {
        const { selectValue } = this.state;
        const { visible, id, style, searchResult, onCancel, placeholder } = this.props;

        const options = searchResult && searchResult.map(d => {
            return <Option key={d.id} data={d.id} value={d.name}>{d.name}</Option>
        })

        const styleValue = Object.assign({
            width: '400px',
            height: '80px',
            top: '150px',
            left: '100px'
        }, style);
        return (
            <Modal
                closable={false}
                mask={false}
                style={styleValue}
                bodyStyle={{
                    padding: '10px'
                }}
                visible={visible}
                onCancel={onCancel}
                footer={null}
            >
                <Select
                    id={id}
                    mode="combobox"
                    showSearch
                    style={{ width: '100%' }}
                    placeholder={placeholder}
                    notFoundContent="没有发现相关内容"
                    defaultActiveFirstOption={false}
                    showArrow={false}
                    filterOption={false}
                    autoComplete="off"
                    value={selectValue}
                    onChange={this.handleChange}
                    onSelect={this.handleSelect}
                >
                    {options}
                </Select>
            </Modal>
        );
    }
}

export default SearchModal;
