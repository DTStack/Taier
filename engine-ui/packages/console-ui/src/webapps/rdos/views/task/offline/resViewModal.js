import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Spin } from 'antd';

import utils from 'utils';
import { getContainer } from 'funcs';

import ajax from '../../../api';
import { ResType } from '../../../components/status';

import {
    modalAction
} from '../../../store/modules/offlineTask/actionType';

class ResViewModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            loading: true,
            data: undefined
        };
    }

    componentWillReceiveProps (nextProps) {
        if (nextProps.resId !== this.props.resId) {
            this.getResDetail(nextProps.resId);
        }
    }

    getResDetail (resId) {
        if (!resId) return;

        ajax.getOfflineRes({
            resourceId: resId
        })
            .then(res => {
                if (res.code === 1) {
                    this.setState({
                        loading: false,
                        data: res.data
                    });
                }
            })
    }

    render () {
        const { visible, resId, closeModal } = this.props;
        const { data, loading } = this.state;

        return <div id="JS_resView_modal">
            <Modal
                title="资源详情"
                visible={ visible }
                onCancel={ closeModal }
                key={ resId }
                footer={[
                    <Button size="large" onClick={ closeModal } key="cancel">取消</Button>
                ]}
                getContainer={() => getContainer('JS_resView_modal')}
            >
                { loading ? <Spin />
                    : data === null ? '系统异常'
                        : <table className="ant-table ant-table-bordered bd-top bd-left" style={{ width: '100%' }}>
                            <tbody className="ant-table-tbody">
                                <tr>
                                    <td width="15%">资源名称</td>
                                    <td>{ data.resourceName }</td>
                                </tr>
                                <tr>
                                    <td>资源描述</td>
                                    <td>{ data.resourceDesc }</td>
                                </tr>
                                <tr>
                                    <td>资源类型</td>
                                    <td> <ResType value={data.resourceType} /></td>
                                </tr>
                                <tr>
                                    <td>创建</td>
                                    <td>{ data.createUser.userName } 于 { utils.formatDateTime(data.gmtCreate) }</td>
                                </tr>
                                <tr>
                                    <td>修改时间</td>
                                    <td>{ utils.formatDateTime(data.gmtModified) }</td>
                                </tr>
                            </tbody>
                        </table>
                }
            </Modal>
        </div>
    }
}

export default connect(state => {
    const { resViewModal, resId } = state.offlineTask.modalShow;
    return {
        visible: resViewModal,
        resId: resId
    }
}, dispatch => {
    return {
        closeModal () {
            dispatch({
                type: modalAction.HIDE_RESVIEW_MODAL
            });
        }
    }
})(ResViewModal);
