import * as React from 'react';
import { message } from 'antd';

import Breadcrumb from '../../../../components/breadcrumb';
import { IRelation } from '../../../../model/relation';
import { GRAPH_MODE } from '../../../../components/relationGraph';

import Base from './base';
import RelationAPI from '../../../../api/relation';

interface IState {
    relationData: IRelation;
}

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/edit',
    name: '编辑关系'
}];

// const relationMockData: any = {"relationName":"测试abc","dataSourceId":"6","description":"","relationEntities":"","relationCollection":[{"id":1,"name":"实体1","columns":[{"id":1,"name":null,"attrId":1,"attrName":"col1"}],"vertex":true,"edge":false,"geometry":{"x":0,"y":0},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"},{"id":2,"name":"实体2","columns":[{"id":2,"name":null,"attrId":2,"attrName":"col2"}],"vertex":true,"edge":false,"geometry":{"x":260,"y":10},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"},{"id":3,"name":"实体3","columns":[{"id":3,"name":null,"attrId":3,"attrName":"col3"}],"vertex":true,"edge":false,"geometry":{"x":500,"y":110},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"},{"geometry":{"x":0,"y":0},"vertex":false,"edge":true,"source":{"id":1,"name":"实体1","columns":[{"id":1,"name":null,"attrId":1,"attrName":"col1"}],"vertex":true,"edge":false,"geometry":{"x":0,"y":0},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"},"target":{"id":2,"name":"实体2","columns":[{"id":2,"name":null,"attrId":2,"attrName":"col2"}],"vertex":true,"edge":false,"geometry":{"x":260,"y":10},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"}},{"geometry":{"x":0,"y":0},"vertex":false,"edge":true,"source":{"id":2,"name":"实体2","columns":[{"id":2,"name":null,"attrId":2,"attrName":"col2"}],"vertex":true,"edge":false,"geometry":{"x":260,"y":10},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"},"target":{"id":3,"name":"实体3","columns":[{"id":3,"name":null,"attrId":3,"attrName":"col3"}],"vertex":true,"edge":false,"geometry":{"x":500,"y":110},"columnOptions":[{"id":1,"entityAttr":"col1","entityAttrCn":"维度1"},{"id":2,"entityAttr":"col2","entityAttrCn":"维度2"},{"id":3,"entityAttr":"col3","entityAttrCn":"维度3"}],"rowIndex":1,"attrId":2,"attrName":"col2"}}]}

class EditRelation extends React.Component<any, IState> {
    state: IState = {
        relationData: {}
    }

    componentDidMount () {
        const { router } = this.props;
        const { relationId } = router.params;
        this.loadRelation(relationId);
    }

    loadRelation = async (relationId: number) => {
        const res = await RelationAPI.getRelation({ relationId });
        if (res.code === 1) {
            this.setState({
                relationData: res.data
            })
        }
    }

    onEdit = async (dataSource: IRelation) => {
        dataSource.id = this.state.relationData.id;
        const res = await RelationAPI.updateRelation(dataSource);
        if (res.code === 1) {
            message.success('编辑关系成功！');
        }
    }

    render () {
        const { relationData } = this.state;
        console.log('dataSource', JSON.stringify(relationData));
        return (
            <div className="c-createRelation">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Base onOk={this.onEdit} mode={GRAPH_MODE.EDIT} relationData={relationData}/>
            </div>
        )
    }
}

export default EditRelation;
