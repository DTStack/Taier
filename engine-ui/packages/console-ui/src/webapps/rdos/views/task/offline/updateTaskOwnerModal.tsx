import * as React from 'react';

import { Modal, Select } from 'antd';

const Option = Select.Option;

class UpdateTaskOwnerModal extends React.Component<any, any> {
    render () {
        const {
            onOk, visible, onCancel,
            projectUsers, onSelect, defaultValue, currentOwner
        } = this.props;
        let co_exist: boolean = false;
        const userOptions = projectUsers && projectUsers.map(
            (item: any) => {
                co_exist = co_exist || (currentOwner && item.userId == currentOwner.id);
                return (<Option
                    key={`${item.userId}`}
                    value={`${item.userId}`}
                >
                    {item.user.userName}
                </Option>)
            }
        )
        return (
            <Modal
                title="选择责任人"
                visible={ visible }
                onOk={ onOk }
                onCancel={ onCancel }
            >
                <Select
                    style={{ width: '100%' }}
                    showSearch
                    defaultValue={defaultValue}
                    onSelect={ onSelect }
                    optionFilterProp="name"
                >
                    {currentOwner && !co_exist && <Option
                        key={`${currentOwner.id}`}
                        value={`${currentOwner.id}`}
                    >
                        {currentOwner.userName}
                    </Option>}
                    {userOptions}
                </Select>
            </Modal>
        )
    }
}

export default UpdateTaskOwnerModal;
