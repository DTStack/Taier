import * as React from 'react';

import { Modal, Select } from 'antd';

const Option = Select.Option;

class UpdateTaskOwnerModal extends React.Component<any, any> {
    render () {
        const {
            onOk, visible, onCancel,
            projectUsers, onSelect, defaultValue
        } = this.props;

        const userOptions = projectUsers && projectUsers.map(
            (item: any) => <Option
                key={`${item.userId}`}
                value={`${item.userId}`}
            >
                {item.user.userName}
            </Option>
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
                    {userOptions}
                </Select>
            </Modal>
        )
    }
}

export default UpdateTaskOwnerModal;
