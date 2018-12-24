import React from 'react';
import { Button, message, Card, Alert, Row, Col } from 'antd';
import { hashHistory } from 'react-router';

import api from '../../api';
import GoBack from 'main/components/go-back'
import MetaImportForm, { metaFormLayout } from './metaImportForm';
import { PROJECT_CREATE_MODEL } from '../../comm/const';

class MetaDataImport extends React.Component {
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
        return (
            <div className='c-metaImport l-metaImport'>
                <div className='l-metaImport__header'>
                    <GoBack
                        type="textButton"
                    />
                    <span className='c-metaImport__header__title'>接入已有项目</span>
                </div>
                <Card
                    noHovering
                >
                    <Alert
                        className='l-metaImport__wanring'
                        message="注意：本功能会将已有Hive表导入本平台进行管理，Hadoop内的数据本身不会移动或改变，在导入进行过程中，请勿新建表或执行其他表结构变更操作"
                        type="warning"
                        showIcon
                        closable
                    />
                    <MetaImportForm
                        ref={(ref) => { this.form = ref; }}
                        projectList={projectList}
                        {...formData}
                        onChange={this.formChange}
                    />
                    <Row>
                        <Col {...metaFormLayout.labelCol}></Col>
                        <Col {...metaFormLayout.wrapperCol}>
                            <Button loading={loading} onClick={this.create} type='primary'>导入</Button>
                        </Col>
                    </Row>
                </Card>
            </div>
        )
    }
}
export default MetaDataImport;
