import * as React from 'react';
import { message } from 'antd';

import Base from './base';
import { IRelation } from '../../../../model/relation';
import Breadcrumb from '../../../../components/breadcrumb';
import { GRAPH_MODE } from '../../../../components/relationGraph';

import RelationAPI from '../../../../api/relation';

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/create',
    name: '新增关系'
}];

class CreateRelation extends React.Component<any, any> {
    onCreate = async (dataSource: IRelation) => {
        const res = await RelationAPI.createRelation(dataSource);
        if (res.code === 1) {
            message.success('添加关系成功！');
        }
    }

    render () {
        return (
            <div className="c-createRelation">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Base onOk={this.onCreate} mode={GRAPH_MODE.EDIT}/>
            </div>
        )
    }
}

export default CreateRelation;
