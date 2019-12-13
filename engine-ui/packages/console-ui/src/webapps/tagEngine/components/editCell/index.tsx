import * as React from 'react';
import { Input } from 'antd';
import EllipsisText from '../ellipsisText';

import './style.scss';
interface PropsInterface {
    value: string | number | string[];
    keyField: string;
    isView?: boolean;
    onHandleEdit: Function;
    isCurrentEdit?: boolean;
}
interface StateInterface {
    isEdit: boolean;
    editValue: string | number | string[];
}

export default class EditCell extends React.PureComponent<PropsInterface, StateInterface> {
    state: StateInterface = {
        isEdit: false,
        editValue: ''
    };
    onEdit = () => {
        const { value } = this.props;
        this.setState({
            isEdit: true,
            editValue: value
        });
    };
    onChangeEdit = (e: any) => {
        const value = e.target.value;
        this.setState({
            editValue: value ? value.slice(0, 20) : ''
        });
    };
    onOkEdit = () => {
        const { editValue } = this.state;
        const { keyField } = this.props;
        this.props.onHandleEdit(keyField, editValue);
        this.onCancelEdit();
    };
    onCancelEdit = () => {
        this.setState({
            editValue: '',
            isEdit: false
        });
    };
    componentDidMount () { }
    render () {
        const { isEdit, editValue } = this.state;
        const { value, isView } = this.props;
        return (
            <div className="edit-Cell">
                {isEdit ? (
                    <div className="edit_input_row">
                        <Input
                            value={editValue}
                            className="input"
                            style={{ width: 150, lineHeight: 24, height: 24 }}
                            onChange={this.onChangeEdit}
                        />
                        <a onClick={this.onOkEdit}>完成</a>
                        <a onClick={this.onCancelEdit}>取消</a>
                    </div>
                ) : (
                    <React.Fragment>
                        <EllipsisText value={value} />
                        {
                            !isView && <a onClick={this.onEdit}>修改</a>
                        }
                    </React.Fragment>
                )}
            </div>
        );
    }
}
