import React from 'react';
/* eslint-disable */
import { Button, message, Card, Alert, Row } from 'antd';
import { cloneDeep } from 'lodash';
import { hashHistory } from 'react-router';
import {
    ENGINE_SOURCE_TYPE
} from '../../comm/const';
import api from '../../api';
import GoBack from 'main/components/go-back'
import CreateForm from './createForm';

class CreateWorkSpace extends React.Component {
    state = {
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
    form = React.createRef()
    componentDidMount () {
        this.getProjectList();
    }
    async getSupportEngineType () {
        let res = await api.getSupportEngineType();
        if (res.code === 1) {
            const engineData = res.data || [];
            engineData.map(item => {
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
    formChange = (values) => {
        console.log(values);
        this.setState({
            formData: {
                ...this.state.formData,
                ...values
            }
        })
    }
    exChangeReqParams = (values) => {
        const copyVal = cloneDeep(values);
        const enableHadoop = copyVal.enableHadoop;
        const enableLibrA = copyVal.enableLibrA;
        const hadoopParams = copyVal.hadoop || {}
        const libraParams = copyVal.libra || {}
        let projectEngineList = [];
        if (enableHadoop) {
            projectEngineList.push(hadoopParams)
        }
        if (enableLibrA) {
            projectEngineList.push(libraParams)
        }
        copyVal.projectEngineList = projectEngineList;
        const delAttr = (obj, attr) => {
            delete obj[attr]
        }
        delAttr(copyVal, 'hadoop');
        delAttr(copyVal, 'libra');
        delAttr(copyVal, 'enableHadoop');
        delAttr(copyVal, 'enableLibrA');
        return copyVal;
    }
    create = () => {
        this.form.validateFieldsAndScroll(null, async (err, values) => {
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
        const { formData, projectList, loading } = this.state;
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
                        ref={(ref) => { this.form = ref; }}
                        projectList={projectList}
                        {...formData}
                        onChange={this.formChange}
                    />
                    <Row style={{ marginTop: 30, textAlign: 'right' }}>
                        <Button onClick={this.create} style={{ width: 90, marginRight: 10 }}>取消</Button>
                        <Button loading={loading} style={{ width: 90 }} onClick={this.create} type='primary'>确定</Button>
                    </Row>
                </Card>
            </div>
        )
    }
}
export default CreateWorkSpace;
