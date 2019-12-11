import * as React from 'react';
import { Card, message } from 'antd';
import { get } from 'lodash';
import { hashHistory } from 'react-router';
import { IDataSource } from '../../../../model/dataSource';

import UploadForm from './form';
import API from '../../../../api/group';
import Breadcrumb from '../../../../components/breadcrumb';

interface IProps {
    dataSourceList: IDataSource[];
    router: any;
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
        this.getGroupData();
    }

    getGroupData = async () => {
        const { router } = this.props;
        const res = await API.getGroup({ groupId: get(router, 'location.query.groupId', '') });
        if (res.code === 1) {
            this.setState({
                formData: res.data
            })
        }
    }

    update = async (values: any) => {
        const { formData } = this.state;
        const { router } = this.props;
        const query = router.location.query;
        console.log('update values of form: ', formData);
        const res = await API.createOrUpdateGroup(Object.assign(formData, values));
        if (res.code === 1) {
            message.success('修改群组成功！');
            hashHistory.push({
                pathname: `/groupAnalyse/detail`,
                query: query
            })
        } else {
            message.error('修改群组失败！');
        }
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
                    <UploadForm formData={formData} router={this.props.router} handSubmit={this.update} mode="edit" />
                </Card>
            </div>
        )
    }
}

export default GroupUploadEdit;
