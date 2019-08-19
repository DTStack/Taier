import * as React from 'react';
import { Modal, Button } from 'antd';
import EditCell from './editCell';
class ProjectDetail extends React.PureComponent<any, any> {
    refsArr: { [propName: string]: EditCell } = {};
    /* 重置组件状态 */
    resetStatus = () => {
        for (const key in this.refsArr) {
            const element = this.refsArr[key];
            element.resetState();
        }
    }
    handleClose = () => {
        this.resetStatus()
        this.props.onCancel();
    }
    renderAttribute = (attrName: any, target: any, editable: any) => {
        const { checkProject } = this.props;
        return <tr>
            <th>{attrName}</th>
            {
                !editable
                    ? <th>{checkProject[target] || ''}</th>
                    : <EditCell
                        ref={(editcell: any) => this.refsArr[target] = editcell}
                        value={checkProject[target]} />
            }
        </tr>
    }
    render () {
        const { visible } = this.props;
        return (
            <Modal
                maskClosable={false}
                title="项目属性"
                visible={visible}
                wrapClassName='detail-modal'
                okText="确定"
                width={550}
                onCancel={this.handleClose}
                footer={
                    [
                        <Button key="back" size="large" onClick={this.handleClose}>取消</Button>
                    ]
                }
            >
                <table style={{ borderCollapse: 'collapse', width: '100%' }}>
                    <tbody>
                        {this.renderAttribute('项目名称', 'projectName', false)}
                        {this.renderAttribute('项目显示名', 'projectAliaName', true)}
                        {this.renderAttribute('项目描述', 'projectDesc', true)}
                        {this.renderAttribute('关联离线计算中的项目', 'guanlian', false)}
                        {this.renderAttribute('创建时间', 'gtmTime', false)}
                        {this.renderAttribute('创建人', 'creator', false)}
                        {this.renderAttribute('管理员', 'admin', false)}
                    </tbody>
                </table>
            </Modal>
        );
    }
}

export default ProjectDetail;
