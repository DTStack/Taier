import * as React from 'react';

import { Row, Card } from 'antd';

import RelationGraph, { GRAPH_MODE } from '../../../../components/relationGraph';
import { IRelation, IRelationEntity } from '../../../../model/relation';

interface IProps {
    data: any;
};

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

class BasicRelationInfo extends React.Component<IProps, any> {
    state = {
        entities: [{
            id: 1,
            name: 'entity-1'
        }, {
            id: 2,
            name: 'entity-2'
        }, {
            id: 3,
            name: 'entity-3'
        }]
    }
    render () {
        const { entities } = this.state;
        return (
            <Row className="row-content">
                <h1 className="row-title">
                    关联实体
                </h1>
                <div style={{ height: 500, marginTop: 5 }}>
                    <Card bordered={false}>
                        <RelationGraph
                            mode={GRAPH_MODE.READ}
                            data={relationMockData.relationCollection}
                            entities={entities}
                        />
                    </Card>
                </div>
            </Row>
        )
    }
}

export default BasicRelationInfo
