import * as React from 'react'

import Breadcrumb from '../../../../components/breadcrumb';
import BasicInfo from './basicInfo';
import RelativeEntity from './relativeEntity';

interface IState {
    pageNo: number;
    pageSize: number;
    total: number;
    dataSource: any[];
    searchVal: string;
    loading: boolean;
    desc: boolean;
    sorterField: string;
}

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/detail',
    name: '关系详情'
}];

class RelationDetail extends React.Component<any, IState> {
    render () {
        return (
            <div className="c-relationDetail">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <BasicInfo data={{ name: 'test' }}/>
                <RelativeEntity data={[]}/>
            </div>
        )
    }
}

export default RelationDetail
