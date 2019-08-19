import * as React from 'react';
/* eslint-disable */
import { Button, message, Card, Alert, Row } from 'antd';
import { cloneDeep } from 'lodash';
import { hashHistory } from 'react-router';
import {
    ENGINE_SOURCE_TYPE, ENGINE_TYPE
} from '../../comm/const';
import api from '../../api';
import GoBack from 'main/components/go-back';
import Cancel from 'widgets/go-back'
import CreateForm from './createForm';

class CreateWorkSpace extends React.Component<any, any> {
    state: any = {
        hasHadoop: false,
        hasLibra: false,
        projectList: {},
        loading: false,
        formData: {
            projectName: undefined,
            projectAlias: undefined,
            catalogueId: undefined,
            lifecycle: 9999,
            projectDesc: undefined
        }
    }
    form: any = React.createRef()
    componentDidMount () {
        this.getSupportEngineType()
        this.getProjectList();
    }
    async getSupportEngineType () {
        let res = await api.getSupportEngineType();
        if (res.code === 1) {
            const engineData = res.data || [];
            engineData.map((item: any) => {
                if (item == ENGINE_SOURCE_TYPE.HADOOP) {
                    this.setState({
                        hasHadoop: true
                    })
                }
                if (item == ENGINE_SOURCE_TYPE.LIBRA) {
                    this.setState({
                        hasLibra: true
                    })
                }
            })
        }
    }
    async getProjectList () {
        let res = await api.getRetainDBList();
        if (res.code == 1) {
            this.setState({
                projectList: res.data
            });
        }
    }
    formChange = (values: any) => {
        console.log(values);
        this.setState({
            formData: {
                ...this.state.formData,
                ...values
            }
        })
    }
    exChangeReqParams = (values: any) => {
        console.log(values);
        const copyVal = cloneDeep(values);
        const enableHadoop = copyVal.enableHadoop;
        const enableLibrA = copyVal.enableLibrA;
        const isAllowDownload = copyVal.isAllowDownload;
        const hadoopParams = copyVal.hadoop && copyVal.hadoop[""] || {}
        const libraParams = copyVal.libra && copyVal.libra[""] || {}
        let projectEngineList: any = [];
        if (enableHadoop) {
            projectEngineList.push(Object.assign(hadoopParams, { engineType: ENGINE_SOURCE_TYPE.HADOOP }))
        }
        if (enableLibrA) {
            projectEngineList.push(Object.assign(libraParams, { engineType: ENGINE_SOURCE_TYPE.LIBRA }))
        }
        copyVal.projectEngineList = projectEngineList;
        copyVal.isAllowDownload = isAllowDownload ? 1 : 0 // true为1，false为0
        const delAttr = (obj: any, attr: any) => {
            delete obj[attr]
        }
        delAttr(copyVal, 'hadoop');
        delAttr(copyVal, 'libra');
        delAttr(copyVal, 'enableHadoop');
        delAttr(copyVal, 'enableLibrA');
        console.log(copyVal)
        return copyVal;
    }
    create = () => {
        this.form.validateFieldsAndScroll(null, async (err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                let res = await api.createProject({
                    ...this.exChangeReqParams(values)
                });
                if (res.code == 1) {
                    message.success('创建项目成功！');
                    hashHistory.push('/');
                }
                this.setState({
                    loading: false
                })
            }
        })
    }
    render () {
        const { formData, projectList, loading, hasHadoop, hasLibra } = this.state;
        const title = (
            <div>
                <GoBack
                    type="textButton"
                />
                <span className='c-createWorkspace__header__title'>基本信息</span>
            </div>
        )
        return (
            <div className='c-createWorkspace l-createWorkspace m-card'>
                <Card
                    title={title}
                    extra={false}
                    noHovering
                    bordered={false}
                >
                    <Alert
                        className='l-createWorkspace__wanring'
                        message="一个项目可以对接一个或多个计算引擎，相当于对接不同引擎的database / schema，新建成功后不可删除"
                        type="warning"
                        showIcon
                        closable
                    />
                    <CreateForm
                        ref={(ref: any) => { this.form = ref; }}
                        projectList={projectList}
                        {...formData}
                        hasHadoop={hasHadoop}
                        hasLibra={hasLibra}
                        onChange={this.formChange}
                    />
                    <Row style={{ marginTop: 30, textAlign: 'right' }}>
                        <Cancel style={{ width: 90, marginRight: 10 }}  title='取消' />
                        <Button loading={loading} style={{ width: 90 }} onClick={this.create} type='primary'>确定</Button>
                    </Row>
                </Card>
            </div>
        )
    }
}
export default CreateWorkSpace;
