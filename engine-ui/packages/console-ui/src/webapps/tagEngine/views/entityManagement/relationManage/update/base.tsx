import * as React from 'react';
import { Row, Button, Col } from 'antd';

import { tailFormItemLayout } from '../../../../comm/const';
import { IDataSource } from '../../../../model/dataSource';

import CreateRelationEntityForm from './form';
import { API } from '../../../../api/apiMap';
import DataSourceAPI from '../../../../api/dataSource';
import RelationGraph from '../../../../components/relationGraph';
import { IRelation, IRelationEntity } from '../../../../model/relation';
import { IEntity } from '../../../../model/entity';

interface IState {
    dataSourceList: IDataSource[];
    entities: IEntity[];
    relationEntities: IRelationEntity[];
}

interface IProps {
    relationData: IRelation;
    mode?: 'create' | 'edit';
    onOk?: (data?: any) => void;
}

const initialEntityNode: IRelationEntity = {
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
class RelationUpdateBase extends React.Component<IProps, IState> {
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
        relationEntities: []
    }

    private _formElem: any;

    static getDerivedStateFromProps (props: IProps, state: IState) {
        const { relationData } = props;
        if (relationData.relationEntities) {
            return {
                relationEntities: relationData.relationEntities
            }
        }
    }

    componentDidMount () {
        this.loadDataSource();
        this.loadEntities();
    }

    loadDataSource = async () => {
        const res = await DataSourceAPI.getStreamDataSourceList();
        console.log(res)
    }

    loadEntities = async () => {
        const res = await API.getEntities();
        console.log(res)
    }

    onAddEntityNodeData = () => {
        const { relationData } = this.props;
        const newState = relationData.relationEntities.slice();
        newState.push(initialEntityNode);
        // this.setState({
        //     relationEntityData: newState
        // })
        // this.props.cha
    }

    onRelationEntitiesChange = () => {
        const { relationEntities } = this.state;
        console.log(relationEntities)
    }

    onOk = () => {
        const { form } = this._formElem.props;
        const { relationEntities } = this.state;

        form.validateFields(function (err: any, values: IRelation) {
            if (!err) {
                const relation: IRelation = form.getFieldsValue();
                relation.relationEntities = relationEntities;
                this.props.onOk(relation);
            }
        });
    }

    render () {
        const { mode, relationData } = this.props;
        const { dataSourceList, entities } = this.state;
        return (
            <div className="inner-container bg-w">
                <div className="c-createRelation__form">
                    <CreateRelationEntityForm
                        mode={mode}
                        ref={(e: any) => this._formElem = e}
                        onCreateRelationEntity={this.onAddEntityNodeData}
                        dataSourceList={dataSourceList}
                        formData={relationData}
                    />
                </div>
                <div className="c-createRelation__graph" style={{ height: 600 }}>
                    <RelationGraph<IRelationEntity>
                        attachClass="graph-bg"
                        data={relationData.relationEntities}
                        entities={entities}
                    />
                </div>
                <Row style={{ marginTop: 20 }}>
                    <Col {...tailFormItemLayout.wrapperCol} className="txt-center">
                        <Button size="large" style={{ width: 200 }} onClick={this.onOk} type="primary">保存</Button>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default RelationUpdateBase;
