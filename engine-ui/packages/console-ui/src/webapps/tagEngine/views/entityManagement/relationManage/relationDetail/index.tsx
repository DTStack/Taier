import * as React from 'react'

import Breadcrumb from '../../../../components/breadcrumb';
import BasicInfo from './basicInfo';
import RelativeEntity from './relativeEntity';
import { IRelation } from '../../../../model/relation';

import API from '../../../../api/relation';

interface IState {
    data: IRelation;
}

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/detail',
    name: '关系详情'
}];

class RelationDetail extends React.Component<any, IState> {
    state: IState = {
        data: {}
    }
    componentDidMount () {
        this.fetchData();
    }

    fetchData = async () => {
        const { location } = this.props.router;
        const res = await API.getRelation({ relationId: location.query.relationId });
        if (res.code === 1) {
            this.setState({
                data: res.data
            })
        }
    }

    render () {
        const { data } = this.state;
        return (
            <div className="c-relationDetail">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <BasicInfo data={data}/>
                <RelativeEntity data={data}/>
            </div>
        )
    }
}

export default RelationDetail
