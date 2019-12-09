import * as React from 'react';

import { Row, Card } from 'antd';

import RelationGraph, { GRAPH_MODE } from '../../../../components/relationGraph';
import { IRelation } from '../../../../model/relation';

interface IProps {
    data: IRelation;
};

class BasicRelationInfo extends React.Component<IProps, any> {
    state = {
        entities: []
    }
    render () {
        const { entities } = this.state;
        const { data } = this.props;
        return (
            <Row className="row-content">
                <h1 className="row-title">
                    关联实体
                </h1>
                <div style={{ height: 500, marginTop: 5 }}>
                    <Card bordered={false}>
                        <RelationGraph
                            mode={GRAPH_MODE.READ}
                            data={data.relationCollection}
                            attachClass="graph-bg"
                            entities={entities}
                        />
                    </Card>
                </div>
            </Row>
        )
    }
}

export default BasicRelationInfo;
