import * as React from 'react';
import { connect } from 'react-redux';
import {
    Card, Form,
    Alert, Button, Row, Col, Spin
} from 'antd';
import GoBack from 'main/components/go-back';
import Api from '../../api';
import _ from 'lodash';
import CatalogueSelect from '../../components/catalogueSelect';
import LifeCycleSelect from '../../components/lifeCycleSelect';
import { hashHistory } from 'react-router'

const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 3 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 10 }
    }
}
let timer: any = null;
let finishSync: any = false;

class HiveSync extends React.Component<any, any> {
    state: any = {
        loading: false,
        syncLoading: true,
        tableList: []
    }

    componentDidMount () {
        this.getTableList();
        this.getSyncStutas();
    }
    componentDidUpdate () {
        const { projectId } = this.props;
        if (projectId != this.props.routeParams.projectId) {
            hashHistory.replace('/database/offLineData');
        }
    }
    getSyncStutas = () => {
        timer = setInterval(async () => {
            finishSync && clearInterval(timer)
            const res = await Api.checkDealStatus();
            console.log(res, finishSync);
            if (!finishSync) {
                this.setState({
                    syncLoading: !(res.data === 1)
                });
                this.getTableList();
            }
            if (res.data === 1) {
                finishSync = true
            }
        }, 1500)
    }

    getTableList = async () => {
        try {
            this.setState({
                loading: true
            });
            const res = await Api.compareIntrinsicTable({
                projectId: this.props.routeParams.projectId
            });
            const tableList = _.chain(res.data.dropTablesName)
                .map((item) => {
                    return item + '（源库已删除）'
                })
                .concat(res.data.addTablesName || [])
                .value()
            console.log(res, tableList);
            this.setState({
                tableList
            });
        } finally {
            this.setState({
                loading: false
            });
        }
    }
    syncMetaData = () => {
        const { form, routeParams } = this.props;

        form.validateFields(async (_err: any, values: any) => {
            if (!_err) {
                try {
                    this.setState({
                        syncLoading: true
                    });
                    // console.log(values, routeParams);
                    const res = await Api.dealIntrinsicTable({
                        projectId: routeParams.projectId,
                        lifecycle: values.lifecycle,
                        catalogueId: values.catalogueId
                    });
                    console.log(res)
                    // this.getTableList();
                    hashHistory.go(-1);
                } finally {
                    this.setState({
                        syncLoading: false
                    });
                }
            }
        })
    }

    componentWillUnmount () {
        clearInterval(timer);
        finishSync = false;
    }

    render () {
        const { routeParams, form } = this.props;
        const { getFieldDecorator } = form;
        const { tableList, syncLoading, loading } = this.state;
        console.log(this.state, this.props)
        return (
            <div className="m-card shadow">
                <Card
                    title={<div><GoBack /> {routeParams.projectName}</div>}
                    extra={false}
                    noHovering
                    bordered={false}
                >
                    <Alert message={'同步源数据库中的表信息，可在本产品中进行查询和管理，仅列出本产品中未包括的表信息'} type="info" showIcon />
                    <Row>
                        <Col span={1}/>
                        <Col span={21}>
                            <Spin spinning={ loading || syncLoading } style={{ width: '500px' }}>
                                <div
                                    style={{
                                        height: '460px',
                                        width: '500px',
                                        border: '1px solid #ccc',
                                        margin: '10px 0px',
                                        display: 'flex',
                                        flexDirection: 'column'
                                    }}
                                >
                                    <div
                                        style={{
                                            paddingLeft: '5px',
                                            background: '#f0f0f0',
                                            height: '36px',
                                            lineHeight: '36px',
                                            borderBottom: '1px solid #ccc',
                                            flex: 'none'
                                        }}
                                    >
                                        表名称
                                    </div>
                                    <div
                                        style={{
                                            flex: 1,
                                            overflow: 'auto',
                                            padding: '10px 5px',
                                            position: 'relative'
                                        }}
                                    >
                                        {
                                            tableList.length === 0
                                                ? (
                                                    <div
                                                        style={{
                                                            position: 'absolute',
                                                            top: 0,
                                                            left: 0,
                                                            right: 0,
                                                            bottom: 0,
                                                            display: 'flex',
                                                            alignItems: 'center',
                                                            justifyContent: 'center',
                                                            color: '#ccc'
                                                        }}
                                                    >
                                                        暂无数据
                                                    </div>
                                                )
                                                : null
                                        }
                                        {
                                            _.map(tableList, (item) => {
                                                return (
                                                    <div
                                                        style={{
                                                            // marginBottom: '5px'
                                                        }}
                                                    >
                                                        {item}
                                                    </div>
                                                )
                                            })
                                        }
                                    </div>
                                </div>
                            </Spin>
                        </Col>
                    </Row>
                    <Form>
                        <FormItem
                            label='所属类目'
                            style={{
                                margin: '20px 0 20px'
                            }}
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`catalogueId`, {
                                rules: [{
                                    required: true,
                                    message: '请选择所属类目'
                                }]
                            })(
                                <CatalogueSelect
                                    style={{ maxWidth: '400px' }}
                                    showSearch
                                    placeholder="请选择所属类目"
                                />
                            )}
                        </FormItem>
                        <FormItem
                            label='生命周期'
                            {...formItemLayout}
                        >
                            {getFieldDecorator(`lifecycle`, {
                                rules: [{
                                    required: true,
                                    message: '生命周期不可为空'
                                }]
                            })(
                                <LifeCycleSelect width={100} inputWidth={175} />
                            )}
                        </FormItem>
                        <Row>
                            <Col span={2}></Col>
                            <Col span={22}>
                                <Button
                                    type="primary"
                                    loading={syncLoading}
                                    onClick={this.syncMetaData}
                                    style={{ width: 100, marginBottom: '40px' }}
                                >
                                    同步元数据
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </div>
        )
    }
}
const HiveSyncWrapper = Form.create()(HiveSync);

export default connect(state => {
    return {
        projectId: _.get(state, 'project.id')
    }
})(HiveSyncWrapper);
