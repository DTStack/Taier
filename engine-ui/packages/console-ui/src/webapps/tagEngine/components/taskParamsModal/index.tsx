import './taskParamsModal.scss';
import * as React from 'react';

import { Modal, Input, Icon, message } from 'antd';

class TaskParamsModal extends React.Component<any, any> {
    state: any = {
        editValue: null,
        editType: null,
        editKey: null,
        editRow: null
    }
    renderEdit () {
        const { editKey, editValue, editType } = this.state;
        return (
            <React.Fragment>
                {editType == 'textarea' ? (
                    <Input.TextArea value = { editValue } onChange = {this.changeEditValue} />
                ) : (<Input value = { editValue } onChange = {this.changeEditValue} />)}
                <a><Icon style={{ marginLeft: '5px' }} type="check" onClick={() => {
                    this.props.onEdit(editKey, editValue, this.resetEdit);
                }} /></a>
                <a><Icon style={{ marginLeft: '3px' }} type="close" onClick={this.resetEdit} /></a>
            </React.Fragment>
        )
    }
    resetEdit = () => {
        this.setState({
            editKey: null,
            editValue: null,
            editType: null,
            editRow: null
        })
    }
    edit (key: any, data: any) {
        this.setState({
            editKey: key,
            editValue: data.value,
            editType: data.editType,
            editRow: data
        })
    }
    changeEditValue = (e: any) => {
        const { editRow: { max } } = this.state;
        if (max && e.target.value && e.target.value.length > max) {
            message.warning(`输入不可超过${max}个字符！`);
            return false;
        }
        this.setState({
            editValue: e.target.value
        })
    }
    render () {
        const { editKey } = this.state;
        const { visible, title, data = [], onCancel } = this.props;
        return (
            <Modal
                footer={null}
                title={title}
                visible={visible}
                onCancel={onCancel}
            >
                <div className='c-taskParams'>
                    {data ? data.map((row: any) => {
                        const key = row.key || row.label;
                        const edit = row.edit;
                        const isEditing = editKey == key;
                        return (
                            <div key={key} className='c-taskParams__row'>
                                <div className='c-taskParams__row__label'>
                                    {row.label}
                                </div>
                                <div className='c-taskParams__row__value'>
                                    {isEditing ? (
                                        this.renderEdit()
                                    ) : (<React.Fragment>
                                        {row.value}
                                        {edit && (
                                            <a style={{ marginLeft: '5px' }} onClick={() => {
                                                this.edit(key, row);
                                            }}><Icon type='edit' /></a>
                                        )}
                                    </React.Fragment>)}
                                </div>

                            </div>
                        )
                    }) : null}
                </div>
            </Modal>
        )
    }
}
export default TaskParamsModal;
