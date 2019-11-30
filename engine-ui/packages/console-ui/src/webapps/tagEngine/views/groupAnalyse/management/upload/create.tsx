import * as React from 'react';
import { Card, message } from 'antd';

import { IDataSource } from '../../../../model/dataSource';

import Breadcrumb from '../../../../components/breadcrumb';
import UploadForm from './form';
import API from '../../../../api/group';

interface IProps {
    dataSourceList: IDataSource[];
    router?: any;
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

    create = async (formData: any) => {
        console.log('update values of form: ', formData);
        const res = await API.createOrUpdateGroup(formData);
        if (res.code === 1) {
            message.success('创建群组成功！');
            this.props.router.push('/groupAnalyse');
        } else {
            message.error('创建群组失败！');
        }
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
                    <UploadForm router={this.props.router} handSubmit={this.create} mode="create"/>
                </Card>
            </div>
        )
    }
}

export default GroupUploadEdit;
