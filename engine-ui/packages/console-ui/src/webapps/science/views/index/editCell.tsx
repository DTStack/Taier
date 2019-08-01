import * as React from 'react';
import { Input, Icon } from 'antd';

class EditCell extends React.PureComponent<any, any> {
    state: any = {
        value: this.props.value,
        editable: false
    }
    resetState = () => {
        this.setState({
            value: this.props.value,
            editable: false
        });
    }
    handleChange = (e: any) => {
        const value = e.target.value;
        this.setState({ value });
    }
    handleSave = () => {
        this.setState({ editable: false });
        if (this.props.onChange) {
            this.props.onChange(this.state.value);
        }
    }
    handleEdit = () => {
        this.setState({ editable: true });
    }
    render () {
        const { value, editable } = this.state;
        return (
            <th>
                {
                    editable
                        ? <div className="editable-cell-input-wrapper">
                            <Input
                                value={value}
                                onChange={this.handleChange}
                                onPressEnter={this.handleSave}
                                style={{ width: '80%' }}
                            />
                            <Icon
                                type="check"
                                className="editable-cell-icon-check"
                                onClick={this.handleSave}
                            />
                        </div>
                        : <div className="editable-cell-text-wrapper">
                            <span>{value || '' }</span>
                            <Icon
                                type="edit"
                                className="editable-cell-icon"
                                onClick={this.handleEdit}
                            />
                        </div>
                }
            </th>
        );
    }
}

export default EditCell;
