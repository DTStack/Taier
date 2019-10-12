import * as React from 'react';
import { Modal, Button, Spin } from 'antd';
import moment from 'moment';

import { getContainer } from 'funcs';
import { FUNC_TYPE_TEXT } from '../../../../comm/const'

import ajax from '../../../../api';

class FnViewModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: true,
            data: undefined
        };
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const defaultData = nextProps.defaultData
        if (defaultData && defaultData.id !== this.props.defaultData.id) {
            this.getFnDetail(defaultData);
        }
    }

    getFnDetail (fn: any) {
        if (!fn) return;

        ajax.getFunc({
            funcId: fn.id
        })
            .then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        loading: false,
                        data: res.data
                    });
                }
            })
    }

    render () {
        const { visible, fnId, handCancel } = this.props;
        const { data, loading } = this.state;
        const widthFix = {
            width: '15%'
        }
        return <div id="JS_func_detail">

            <Modal
                title="函数详情"
                visible={ visible }
                onCancel={ handCancel }
                key={ fnId }
                footer={[
                    <Button size="large" onClick={ handCancel } key="cancel">关闭</Button>
                ]}
                getContainer={() => getContainer('JS_func_detail')}
            >
                { loading ? <Spin/>
                    : data === null ? '系统异常'
                        : <table className="ant-table ant-table-bordered bd-top bd-left" style={{ width: '100%' }}>
                            <tbody className="ant-table-tbody">
                                <tr>
                                    <td
                                        // width="15%"
                                        {...widthFix}
                                    >函数名称</td>
                                    <td>{ data.name }</td>
                                </tr>
                                <tr>
                                    <td>类型</td>
                                    <td>{ FUNC_TYPE_TEXT[data.udfType as keyof typeof FUNC_TYPE_TEXT]}</td>
                                </tr>
                                <tr>
                                    <td>用途</td>
                                    <td>{ data.purpose }</td>
                                </tr>
                                <tr>
                                    <td>命令格式</td>
                                    <td>{ data.commandFormat || '/' }</td>
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

export default FnViewModal;
