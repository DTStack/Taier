import * as React from 'react';
import { message } from 'antd';

import Breadcrumb from '../../../../components/breadcrumb';
import { IRelation, IRelationEntity } from '../../../../model/relation';

import Base from './base';
import RelationAPI from '../../../../api/relation';

interface IState {
    dataSource: IRelation;
}

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/edit',
    name: '编辑关系'
}];

const mockData: IRelationEntity[] = [{
    id: 1,
    name: 'entity1',
    columns: [{
        id: 2,
        name: 'column-1'
    }, {
        id: 3,
        name: 'column-2'
    }, {
        id: 4,
        name: 'column-3'
    }],
    vertex: true,
    edge: false,
    geometry: {
        x: 0,
        y: 0
    },
    columnOptions: [{
        id: 2,
        name: 'column-1'
    }, {
        id: 3,
        name: 'column-2'
    }, {
        id: 4,
        name: 'column-3'
    }]
}, {
    id: 2,
    name: 'entity2',
    columns: [{
        id: 8,
        name: 'column-1'
    }, {
        id: 10,
        name: 'column-2'
    }, {
        id: 9,
        name: 'column-3'
    }],
    vertex: true,
    edge: false,
    geometry: {
        x: 0,
        y: 0
    },
    columnOptions: [{
        id: 8,
        name: 'column-8'
    }, {
        id: 9,
        name: 'column-9'
    }, {
        id: 10,
        name: 'column-10'
    }]
}, {
    id: 3,
    name: 'edge',
    vertex: false,
    edge: true,
    geometry: {
        x: 0,
        y: 0
    },
    source: {
        id: 1,
        name: 'entity-1',
        rowIndex: 1
    },
    target: {
        id: 2,
        name: 'entity-2',
        rowIndex: 2
    }
}];

const relationMockData: IRelation = {
    id: 1,
    relationName: 'relation1',
    relationDesc: 'This is a relation description.',
    relationCollection: mockData
}

class EditRelation extends React.Component<any, IState> {
    state: IState = {
        dataSource: relationMockData
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
                dataSource: res.data
            })
        }
    }

    onEdit = async (dataSource: IRelation) => {
        const res = await RelationAPI.updateRelation(dataSource);
        if (res.code === 1) {
            message.success('编辑关系成功！');
        }
    }

    render () {
        const { dataSource } = this.state;
        console.log('dataSource', JSON.stringify(dataSource));
        return (
            <div className="c-createRelation">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <Base onOk={this.onEdit} mode="create" relationData={dataSource}/>
            </div>
        )
    }
}

export default EditRelation;
