import * as React from 'react';
import { Card } from 'antd';

import { IDataSource } from '../../../../model/dataSource';

import Breadcrumb from '../../../../components/breadcrumb';
import UploadForm from './form';

interface IProps {
    dataSourceList: IDataSource[];
};

interface IState {
    formData: any;
}

const breadcrumbNameMap = [{
    path: '/groupAnalyse',
    name: '群组管理'
}, {
    path: '/groupAnalyse/upload/edit',
    name: '编辑群组'
}];

class GroupUploadEdit extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
    }

    state: IState = {
        formData: {}
    }

    componentDidMount () {

    }

    getEditSource = () => {
    }

    update = (formData: any) => {
        console.log('update values of form: ', formData);
    }

    render () {
        const { formData } = this.state;
        return (
            <div>
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <UploadForm formData={formData} handSubmit={this.update} mode="edit"/>
                </Card>
            </div>
        )
    }
}

export default GroupUploadEdit;
