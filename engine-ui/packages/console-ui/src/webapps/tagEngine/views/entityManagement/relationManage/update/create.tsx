import * as React from 'react';
import { Row, Button, Col } from 'antd';

import Breadcrumb from '../../../../components/breadcrumb';
import { tailFormItemLayout } from '../../../../comm/const';
import { IDataSource } from '../../../../model/dataSource';

import CreateRelationEntityForm from './form';
import RelationGraph, { IEntity, IEntityNode } from '../../../../components/relationGraph';

interface IState {
    dataSourceList: IDataSource[];
    entities: IEntity[];
    relationEntityData: IEntityNode[];
}

const breadcrumbNameMap = [{
    path: '/relationManage',
    name: '关系管理'
}, {
    path: '/relationManage/create',
    name: '新增关系'
}];

const initialEntityNode: IEntityNode = {
    id: -1,
    name: '',
    columns: [{
        id: -2,
        name: ''
    }],
    vertex: true,
    edge: false,
    position: {
        x: 0,
        y: 0
    }
}

const mockData: IEntityNode[] = [{
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
    position: {
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
    position: {
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
    id: 3, // - 必要
    name: 'edge', 
    vertex: true, // name - 必要
    edge: false, // 线 - 必要
    position: {
        x: 0,
        y: 0
    },
    source: { // - 必要
        id: 1,
        name: 'entity-1',
        rowIndex: 1 // - 必要
    },
    target: {// - 必要
        id: 2,
        name: 'entity-2',
        rowIndex: 2 // - 必要
    }
}, {
    id: 3, // - 必要
    name: 'edge', 
    vertex: false, // name - 必要
    edge: true, // 线 - 必要
    position: {
        x: 0,
        y: 0
    },
    source: { // - 必要
        id: 1,
        name: 'entity-1',
        rowIndex: 1 // - 必要
    },
    target: {// - 必要
        id: 2,
        name: 'entity-2',
        rowIndex: 2 // - 必要
    }
}];

class CreateRelation extends React.Component<any, IState> {
    state: IState = {
        dataSourceList: [],
        entities: [{
            id: 1,
            name: 'entity-1'
        }, {
            id: 2,
            name: 'entity-2'
        }, {
            id: 3,
            name: 'entity-3'
        }],
        relationEntityData: mockData// [initialEntityNode]
    }

    loadDataSource = () => {
    }

    save = () => {
    }

    onAddEntityNodeData = () => {
        const { relationEntityData } = this.state;
        const newState = relationEntityData.slice();
        newState.push(initialEntityNode);
        this.setState({
            relationEntityData: newState
        })
    }

    render () {
        const { dataSourceList, relationEntityData, entities } = this.state;
        return (
            <div className="c-createRelation">
                <Breadcrumb breadcrumbNameMap={breadcrumbNameMap} />
                <div className="inner-container bg-w">
                    <div className="c-createRelation__form">
                        <CreateRelationEntityForm
                            mode="create"
                            onCreateRelationEntity={this.onAddEntityNodeData}
                            dataSourceList={dataSourceList}
                        />
                    </div>
                    <div className="c-createRelation__graph" style={{ height: 600 }}>
                        <RelationGraph
                            attachClass="graph-bg"
                            data={relationEntityData}
                            entities={entities}
                        />
                    </div>
                    <Row style={{ marginTop: 20 }}>
                        <Col {...tailFormItemLayout.wrapperCol} className="txt-center">
                            <Button size="large" style={{ width: 200 }} type="primary">保存</Button>
                        </Col>
                    </Row>
                </div>
            </div>
        )
    }
}

export default CreateRelation;
