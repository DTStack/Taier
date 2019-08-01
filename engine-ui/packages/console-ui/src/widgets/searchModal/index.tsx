import * as React from 'react';
import {
    Modal, Select
} from 'antd';

const Option = Select.Option;

class SearchModal extends React.PureComponent<any, any> {
    state: any = {
        selectValue: ''
    }
    componentDidUpdate (prevProps: any, prevState: any) {
        if (this.props.visible && !prevProps.visible) {
            this.setState({
                selectValue: ''
            });
        }
    }
    handleChange = (value: any) => {
        const { onChange } = this.props;
        this.setState({
            selectValue: value
        })
        onChange(value);
        console.log('onchange:', value);
    }
    handleSelect = (value: any, option: any) => {
        const { onSelect } = this.props;
        onSelect(value, option)
        console.log(value, option)
    }
    render () {
        const { selectValue } = this.state;
        const { visible, id, style, searchResult, onCancel, placeholder } = this.props;

        const options = searchResult && searchResult.map((d: any) => {
            return <Option key={d.id} data={d.id} {...{ data: d.id }} value={d.name}>{d.name}</Option>
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
                    {...{ id: id, showArrow: false, filterOption: false, autoComplete: 'off' }}
                >
                    {options}
                </Select>
            </Modal>
        );
    }
}

export default SearchModal;
