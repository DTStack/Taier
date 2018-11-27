import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Spin } from 'antd';
import moment from 'moment';

import { getContainer } from 'funcs';
import ajax from '../../../api';

import {
    modalAction
} from '../../../store/modules/offlineTask/actionType';

class FnViewModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            loading: true,
            data: undefined
        };
    }

    componentWillReceiveProps (nextProps) {
        if (nextProps.fnId !== this.props.fnId) {
            this.getFnDetail(nextProps.fnId);
        }
    }

    getFnDetail (fnId) {
        if (!fnId) return;

        ajax.getOfflineFn({
            functionId: fnId
        })
            .then(res => {
                if (res.code === 1) {
                    this.setState({
                        data: res.data
                    });
                }
                this.setState({
                    loading: false
                })
            })
    }

    render () {
        const { visible, fnId, closeModal } = this.props;
        const { data, loading } = this.state;

        return <div id="JS_fnView_modal">
            <Modal
                title="函数详情"
                visible={ visible }
                onCancel={ closeModal }
                key={ fnId }
                footer={[
                    <Button size="large" onClick={ closeModal } key="cancel">取消</Button>
                ]}
                getContainer={() => getContainer('JS_fnView_modal')}
            >
                { loading ? <Spin />
                    : data === null ? '系统异常'
                        : <table className="ant-table ant-table-bordered bd-top bd-left" style={{ width: '100%' }}>
                            <tbody className="ant-table-tbody">
                                <tr>
                                    <td width="15%">函数名称</td>
                                    <td>{ data.name }</td>
                                </tr>
                                <tr>
                                    <td>类名</td>
                                    <td>{ data.className }</td>
                                </tr>
                                <tr>
                                    <td>用途</td>
                                    <td>{ data.purpose }</td>
                                </tr>
                                <tr>
                                    <td>命令格式</td>
                                    <td>{ data.commandFormate || '/' }</td>
                                </tr>
                                <tr>
                                    <td>参数说明</td>
                                    <td>{ data.paramDesc || '/' }</td>
                                </tr>
                                <tr>
                                    <td>创建</td>
                                    <td>{ data.createUser.userName } 于 { moment(data.gmtCreate).format('YYYY-MM-DD hh:mm:ss') }</td>
                                </tr>
                                <tr>
                                    <td>最后修改</td>
                                    <td>{ data.modifyUser.userName } 于 { moment(data.gmtModified).format('YYYY-MM-DD hh:mm:ss') }</td>
                                </tr>
                            </tbody>
                        </table>
                }
            </Modal>
        </div>
    }
}

export default connect(state => {
    const { fnViewModal, fnId } = state.offlineTask.modalShow;
    return {
        visible: fnViewModal,
        fnId: fnId
    }
}, dispatch => {
    return {
        closeModal () {
            dispatch({
                type: modalAction.HIDE_FNVIEW_MODAL
            });
        }
    }
})(FnViewModal);
