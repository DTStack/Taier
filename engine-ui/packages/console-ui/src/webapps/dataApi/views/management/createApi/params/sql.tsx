import * as React from 'react';
import { Card, Modal, Spin, Button, Checkbox } from 'antd';
import { connect } from 'react-redux';

import CodeEditor from 'widgets/editor';
class ApiSqlEditor extends React.Component<any, any> {
    state: any = {
        sql: '',
        loading: false,
        visible: false
    }
    // eslint-disable-next-line
    componentDidMount () {
        this.setState({
            visible: !this.props.disAbleTip
        })
    }
    // select LOCK_NAME from QRTZ_LOCKS
    showModal () {
        this.setState({
            visible: true
        })
    }
    renderTitle () {
        const { loading } = this.props;
        return (
            <span>API配置SQL语句
                <a style={{ marginLeft: '20px', fontSize: '12px' }} onClick={this.props.sqlFormat}>格式化语句</a>
                <a style={{ marginLeft: '8px', fontSize: '12px' }} onClick={this.showModal.bind(this)}>SQL编写提示</a>
                <a style={{ float: 'right', marginLeft: '8px', fontSize: '12px' }} onClick={() => {
                    if (loading) {
                        return;
                    }
                    this.props.sqlModeShowChange()
                }}>编辑参数</a>
                <span style={{ float: 'right', fontSize: '12px', color: '#888' }}>{loading && <Spin size="small" style={{ marginRight: '8px' }} />}代码</span>
            </span>
        )
    }
    render () {
        return (
            <div>
                <Card
                    noHovering
                    title={this.renderTitle()}
                >
                    <CodeEditor
                        style={{ height: '600px', marginTop: '1px' }}
                        onChange={this.props.sqlOnChange}
                        value={this.props.editor.sql}
                        // cursor={this.props.editor.cursor}
                        sync={this.props.editor.sync}
                    />
                    <Modal
                        bodyStyle={{ padding: '20px 13px 12px 20px' }}
                        title="SQL编写提示"
                        visible={this.state.visible}
                        width="520px"
                        footer={
                            (
                                <div>
                                    <Checkbox checked={this.props.disAbleTip} style={{ float: 'left', marginTop: '5px', marginLeft: '10px' }} onChange={this.props.disAbleTipChange}>不再提示</Checkbox>
                                    <Button type="primary" onClick={() => { this.setState({ visible: false }) }}>知道了</Button>
                                </div>
                            )
                        }
                        onCancel={() => { this.setState({ visible: false }) }}
                    >
                        <div className="sql-tip-box">
                            <p className="section">SQL示例语句:</p>
                            <div className="section">
                                <p className="title">SELECT</p>
                                <div className="content">
                                    <p>
                                        <span className="content-title">sales time,</span>
                                        <span className="content-text">SELECT查询的字段即为API返回参数。</span>
                                    </p>
                                    <p>
                                        <span className="content-title">addr as address, </span>
                                        <span className="content-text">如果定义了字段别名，则返回参数名称为字段别名。</span>
                                    </p>
                                    <p>
                                        <span className="content-title">sum(value) as total_amount</span>
                                        <span className="content-text"> 支持SQL中sum、count等函数。</span>
                                    </p>
                                </div>
                            </div>
                            <div className="section">
                                <p className="title">FROM</p>
                                <div className="content">
                                    <p>
                                        <span className="content-title">table_user</span>
                                    </p>
                                </div>
                            </div>
                            <div className="section">
                                <p className="title">WHERE</p>
                                <div className="content">
                                    <p>
                                        <span className="content-title">user_id = ${'{uid}'};</span>
                                        <span className="content-text">WHERE条件中的参数为API请求参数，参数格式必须为${'{参数名}'}。</span>
                                    </p>
                                </div>
                            </div>
                            <div className="section">
                                <p className="title-small">注意：</p>
                                <div className="content text margin_small">
                                    <p>1.只支持输入一条完整的SQL语句；</p>
                                    <p>2.不支持子语句查询；</p>
                                    <p>3.支持同一数据源下的两张表关联查询；</p>
                                    <p>4.SQL编写完成后，可对参数信息进行设置，填写参数说明、打开分页查询按钮等，参数设置完毕后可进入测试环节。</p>
                                </div>
                            </div>
                        </div>
                    </Modal>
                </Card>
            </div>
        )
    }
}

const mapStateToProps = (state: any) => {
    const { apiManage } = state
    return {
        isClickCode: apiManage.isClickCode
    }
}

export default connect(mapStateToProps, null)(ApiSqlEditor);
