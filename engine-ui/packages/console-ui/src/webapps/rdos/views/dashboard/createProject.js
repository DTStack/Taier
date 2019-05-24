import React from 'react';
import { Button, message, Card, Alert, Row } from 'antd';
import { hashHistory } from 'react-router';

import api from '../../api';
import GoBack from 'main/components/go-back'
import CreateForm from './createForm';
import { PROJECT_CREATE_MODEL } from '../../comm/const';

class CreateWorkSpace extends React.Component {
    state = {
        projectList: [],
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
    create = () => {
        this.form.validateFieldsAndScroll(null, async (err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                let res = await api.createProject({
                    ...values,
                    createModel: PROJECT_CREATE_MODEL.IMPORT
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
