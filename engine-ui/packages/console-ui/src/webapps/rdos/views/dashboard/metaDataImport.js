import React from 'react';
import { Button, message } from 'antd';
import { hashHistory } from 'react-router';

import api from '../../api';
import MetaImportForm from './metaImportForm';
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
                <MetaImportForm
                    ref={(ref) => { this.form = ref; }}
                    projectList={projectList}
                    {...formData}
                    onChange={this.formChange}
                />
                <Button loading={loading} onClick={this.create} type='primary'>导入</Button>
            </div>
        )
    }
}
export default MetaDataImport;
