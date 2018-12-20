import React from 'react';

import api from '../../api';
import MetaImportForm from './metaImportForm';

class MetaDataImport extends React.Component {
    state = {
        projectList: [],
        formData: {
            projectName: undefined,
            projectAlias: undefined,
            catalogueId: undefined,
            lifecycle: 9999,
            projectDesc: undefined
        }
    }
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
    render () {
        const { formData, projectList } = this.state;
        return (
            <div className='c-metaImport l-metaImport'>
                <MetaImportForm
                    projectList={projectList}
                    {...formData}
                    onChange={this.formChange}
                />
            </div>
        )
    }
}
export default MetaDataImport;
