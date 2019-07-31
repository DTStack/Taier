import * as React from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Modal, Button, Spin, message } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';
import * as resourceActions from '../../actions/resourceActions';
import utils from 'utils';
import { getContainer } from 'funcs';
import ajax from '../../api/resource';
class ResViewModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            loading: true,
            data: undefined
        };
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (nextProps.resId !== this.props.resId && nextProps.visible) {
            this.getResDetail(nextProps.resId);
        }
    }

    getResDetail (resId: any) {
        if (!resId) return;

        ajax.getResourceById({
            resourceId: resId
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

    copyOk = () => {
        message.success('复制成功！');
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
                    <Button size="large" onClick={ closeModal } key="cancel">关闭</Button>
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
                                    <td>存储路径</td>
                                    <td>
                                        {data.url}
                                        <CopyToClipboard key="copy" text={data.url}
                                            onCopy={this.copyOk}>
                                            <a style={{ marginLeft: 4 }}>复制</a>
                                        </CopyToClipboard>
                                    </td>
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

export default connect(null, (dispatch: any) => {
    return {
        ...bindActionCreators(resourceActions, dispatch)
    }
})(ResViewModal);
