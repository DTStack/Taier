import React from 'react';
import { Modal, Button, Spin } from 'antd';
import moment from 'moment';

import ajax from '../../api';
import { getContainer } from '../resourceManager/resModal';

class FnViewModal extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            loading: true,
            data: undefined,
        };
    }

    UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (nextProps.fnId !== this.props.fnId) {
            this.getFnDetail(nextProps.fnId);
        }
    }

    getFnDetail(fnId: any) {
        if (!fnId) return;

        ajax.getOfflineFn({
            functionId: fnId,
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data,
                });
            }
            this.setState({
                loading: false,
            });
        });
    }

    render() {
        const { visible, fnId, closeModal } = this.props;
        const { data, loading } = this.state;
        const title = data?.type === 2 ? '存储过程详情' : '函数详情';
        return (
            <div id="JS_fnView_modal">
                <Modal
                    title={title}
                    visible={visible}
                    onCancel={closeModal}
                    key={fnId}
                    width={550}
                    footer={[
                        <Button size="large" onClick={closeModal} key="cancel">
                            关闭
                        </Button>,
                    ]}
                    getContainer={() => getContainer('JS_fnView_modal')}
                >
                    {loading ? (
                        <Spin />
                    ) : data === null ? (
                        '系统异常'
                    ) : (
                        <table
                            className="ide-ui-table ide-ui-table-bordered bd-top bd-left"
                            style={{ width: '100%' }}
                        >
                            <tbody className="ide-ui-table-tbody">
                                <tr>
                                    <td {...{ width: '20%' }}>函数名称</td>
                                    <td>{data.name}</td>
                                </tr>
                                {data.className && (
                                    <tr>
                                        <td>类名</td>
                                        <td>{data.className}</td>
                                    </tr>
                                )}
                                {data.sqlText && (
                                    <tr>
                                        <td>SQL</td>
                                        <td>{data.sqlText || '/'}</td>
                                    </tr>
                                )}
                                <tr>
                                    <td>用途</td>
                                    <td>{data.purpose || '/'}</td>
                                </tr>
                                <tr>
                                    <td>命令格式</td>
                                    <td>{data.commandFormate || '/'}</td>
                                </tr>
                                <tr>
                                    <td>参数说明</td>
                                    <td>{data.paramDesc || '/'}</td>
                                </tr>
                                <tr>
                                    <td>创建</td>
                                    <td>
                                        {moment(data.gmtCreate).format(
                                            'YYYY-MM-DD hh:mm:ss'
                                        )}
                                    </td>
                                </tr>
                                <tr>
                                    <td>最后修改</td>
                                    <td>
                                        {moment(data.gmtModified).format(
                                            'YYYY-MM-DD hh:mm:ss'
                                        )}
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    )}
                </Modal>
            </div>
        );
    }
}

export default FnViewModal;
