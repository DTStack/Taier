import * as React from 'react';
import { Card } from 'antd';

import { IDataSource } from '../../../../model/dataSource';

import Breadcrumb from '../../../../components/breadcrumb';
import UploadForm from './form';

interface IProps {
    dataSourceList: IDataSource[];
};

const breadcrumbNameMap = [{
    path: '/groupAnalyse',
    name: '群组管理'
}, {
    path: '/groupAnalyse/upload',
    name: '新增群组'
}];

class GroupUploadEdit extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
    }

    private _form: any;

    componentDidMount () {

    }

    create = (formData: any) => {
        console.log('update values of form: ', formData);
    }

    render () {
        return (
            <div>
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Card
                    noHovering
                    bordered={false}
                    className="noBorderBottom"
                >
                    <UploadForm handSubmit={this.create} mode="edit" ref={(e: any) => { this._form = e; }} />
                </Card>
            </div>
        )
    }
}

export default GroupUploadEdit;
