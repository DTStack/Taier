import React from 'react';

import { Modal, Transfer } from 'antd';

class EditMultipleTableModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            selectKeys: props.selectKeys
        }
    }
    render () {
        const { selectKeys } = this.state;
        const { visible, onCancel, onOk } = this.props;
        return (
            <Modal
                width={600}
                visible={visible}
                title='编辑分组表'
                onCancel={onCancel}
                onOk={() => {
                    onOk(selectKeys);
                }}
            >
                <TransferEdit onChange={(keys) => {
                    this.setState({
                        selectKeys: keys
                    })
                }} {...this.props} selectKeys={selectKeys} />
            </Modal>
        )
    }
}
class TransferEdit extends React.Component {
    render () {
        const { tableList, selectKeys, onChange } = this.props;
        return (
            <Transfer
                className='c-dt__transfer'
                dataSource={tableList.map((table) => {
                    return {
                        key: table,
                        title: table
                    }
                })}
                showSearch
                targetKeys={selectKeys}
                onChange={onChange}
                render={item => item.title}
                titles={['张表', '张表']}
            />
        )
    }
}
export default EditMultipleTableModal;
