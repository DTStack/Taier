import * as React from 'react';
import { cloneDeep, get } from 'lodash';
import { Row, Button, Col } from 'antd';

import MxFactory from 'widgets/mxGraph';

import { tailFormItemLayout } from '../../../../comm/const';
import { IDataSource } from '../../../../model/dataSource';

import CreateRelationEntityForm from './form';
import EntityAPI from '../../../../api/entity';
import DataSourceAPI from '../../../../api/dataSource';
import RelationGraph from '../../../../components/relationGraph';
import { IRelation, IRelationEntity } from '../../../../model/relation';
import { IEntity } from '../../../../model/entity';

interface IState {
    dataSourceList: IDataSource[];
    entities: IEntity[];
    data: IRelation;
}

interface IProps {
    relationData?: IRelation;
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
    geometry: {
        x: 0,
        y: 0
    }
}

const Mx = MxFactory.create();
const {
    mxEvent,
    mxPopupMenu
    // mxConstants,
    // mxEventObject,
    // mxCell,
    // mxGeometry,
    // mxUtils
} = Mx;

const mockColumns = [{
    id: 2,
    name: 'column-1'
}, {
    id: 3,
    name: 'column-2'
}, {
    id: 4,
    name: 'column-3'
}];

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
        data: {
            relationEntities: []
        }
    }

    private _formElem: any;
    private _graphInstance: any;

    static getDerivedStateFromProps (props: IProps, state: IState) {
        const { relationData } = props;
        if (relationData && relationData !== state.data) {
            return {
                data: relationData
            }
        }
    }

    componentDidMount () {
        this.loadDataSource();
        this.loadEntities();
    }

    componentWillUnmount () {
        document.removeEventListener('change', this.onSelectChange);
    }

    loadDataSource = async () => {
        const res = await DataSourceAPI.getStreamDataSourceList();
        if (res.code === 1) {
            this.setState({
                dataSourceList: res.data
            })
        }
    }

    loadEntities = async () => {
        const res = await EntityAPI.getEntities();
        if (res.code === 1) {
            this.setState({
                entities: res.data
            })
        }
    }

    onPushEntityNodeData = () => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        if (!newState.relationEntities) {
            newState.relationEntities = [];
        };
        // 通过数组长度，计算新节点默认位置
        const defaultPosition = newState.relationEntities.length * 80;
        initialEntityNode.geometry.x = defaultPosition;
        initialEntityNode.geometry.y = defaultPosition;
        newState.relationEntities.push(initialEntityNode);
        this.setState({
            data: newState
        })
    }

    onRelationEntityChange = async (relationEntityIndex: number, relationEntity: IRelationEntity) => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        let oldRelationEntity: IRelationEntity = newState.relationEntities[relationEntityIndex];
        if (relationEntity.id !== oldRelationEntity.id || !oldRelationEntity) {
            oldRelationEntity = Object.assign(oldRelationEntity, relationEntity);
            // 关系实体变更时，需要重新加载该实体的维度列表信息
            // const res = await EntityAPI.getEntities({ id: relationEntity.id });
            // if (res.code === 0) {
            // }
            // 初始化维度选项
            oldRelationEntity.columns = [{
                id: -1,
                name: null
            }];
            oldRelationEntity.columnOptions = mockColumns;// res.data || [];
            newState.relationEntities[relationEntityIndex] = oldRelationEntity;
            this.setState({
                data: newState
            })
        }
    }

    onPushRelationEntityCol = (relationEntityIndex: number) => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        const relationEntity: IRelationEntity = newState.relationEntities[relationEntityIndex];
        if (!relationEntity.columns) {
            relationEntity.columns = [];
        }
        relationEntity.columns.push({
            id: -1,
            name: null
        })
        this.setState({
            data: newState
        })
    }

    onRelationEntityColumnChange = async (relationEntityIndex: number, relationEntityColumnIndex: number, relationEntityCol: IRelationEntity) => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        let oldRelationEntityCol: IRelationEntity = newState.relationEntities[relationEntityIndex].columns[relationEntityColumnIndex];
        if (relationEntityCol.id !== oldRelationEntityCol.id || !oldRelationEntityCol) {
            newState.relationEntities[relationEntityIndex].columns[relationEntityColumnIndex] = Object.assign({}, oldRelationEntityCol, relationEntityCol);
            // eslint-disable-next-line no-undef
            console.log('onRelationEntityColumnChange:', relationEntityIndex, relationEntityColumnIndex, newState);
            // this.setState({
            //     data: newState
            // })
            this.state.data = newState;
        }
    }

    onRelationEntitiesChange = () => {
        const { data } = this.state;
        console.log('onRelationEntitiesChange', data);
    }

    onSelectChange = (e: any) => {
        const ctx = this;
        console.log('onSelectChange:', e);
        const isSelect = e.target.nodeName === 'SELECT';
        if (isSelect) {
            const claName = e.target.className;
            const selectedOption = e.target.children[e.target.selectedIndex];
            if (selectedOption.getAttribute('data-default')) return;

            const isRelationEntitySelected = claName.indexOf('relationEntity__select') > -1;
            const isRelationEntityColumnSelected = claName.indexOf('relationEntityColumn__tr') > -1;
            const id = parseInt(e.target.value, 10);

            if (isRelationEntitySelected) {
                const index = parseInt(e.target.getAttribute('data-index'), 10);
                ctx.onRelationEntityChange(index, { id: id, name: selectedOption.textContent });
            } else if (isRelationEntityColumnSelected) {
                const indexArr = e.target.getAttribute('data-index').split('-');
                const entityIndex = parseInt(indexArr[0], 10);
                const columnIndex = parseInt(indexArr[1], 10);

                ctx.onRelationEntityColumnChange(entityIndex, columnIndex, { id: id, name: selectedOption.textContent });
            }
        }
    }

    onGraphEvent = (graph: any) => {
        const ctx = this;
        if (graph) {
            ctx._graphInstance = graph;
            // 监听 select change 事件
            document.addEventListener('change', ctx.onSelectChange);

            graph.addListener(mxEvent.CLICK, function (sender: any, evt: any) {
                const cell = evt.getProperty('cell');
                const event = evt.getProperty('event');
                const triggerAddCol = event.target.nodeName === 'BUTTON' && get(event, 'target.className', '').indexOf('btn-add-col') > -1;
                if (triggerAddCol) {
                    const itemIndex: number = cell.index; // 此处 cell.id 为每个关系实体在数组中的下标
                    ctx.onPushRelationEntityCol(itemIndex);
                }
            }, true);

            graph.addListener(mxEvent.MOVE_CELLS, function (sender: any, evt: any) {
                ctx.updateRelationEntities();
            }, true);
            graph.addListener(mxEvent.CELL_CONNECTED, function (sender: any, evt: any) {
                // 一次连接会触发两次该事件，通过判断是否是source来区分
                if (!evt.getProperty('source')) {
                    setTimeout(() => {
                        /**
                         * 这里延迟执行是因为这个事件监听的时候edge还只是一个previewState
                         * 而需要的style来保存位置信息的style在真正生成的时候才有值
                         * 故延迟执行，确保edge的style有值了
                         *  */
                        ctx.updateRelationEntities();
                    }, 500);
                }
            }, true);
        }
    }

    onGraphMenu = (graph: any) => {
        const ctx = this;
        console.log('ctx:', ctx);
        if (graph) {
            var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;
            mxPopupMenu.prototype.showMenu = function () {
                var cells = graph.getSelectionCells()
                if (cells.length > 0) {
                    mxPopupMenuShowMenu.apply(this, arguments);
                } else return false
            };
            graph.popupMenuHandler.autoExpand = true
            graph.popupMenuHandler.factoryMethod = function (menu: any, cell: any, evt: any) {
                if (!cell) return;
                // const currentNode = cell.data || {};
                if (cell.vertex) {
                    menu.addItem('删除实体', null, function () {
                        graph.removeCells([cell])
                    }, null, null, true) // 正常状态
                } else {
                    menu.addItem('删除依赖关系', null, function () {
                        graph.removeCells([cell])
                    }, null, null, true) // 正常状态
                }
            }
        }
    }

    getGraphData = (): IRelationEntity[] => {
        const graph = this._graphInstance;
        const rootCell = graph.getDefaultParent();
        const cells = graph.getChildCells(rootCell);
        const getCellData = (cell: any): IRelationEntity => {
            const cellData: IRelationEntity = cell.vertex ? cell.value : { geometry: {} };
            cellData.id = cell.id;
            cellData.geometry.x = cell.geometry.x;
            cellData.geometry.y = cell.geometry.y;
            cellData.vertex = cell.vertex;
            cellData.edge = cell.edge;

            return cellData;
        }

        const cellData = cells.map(cell => {
            let cellItem: IRelationEntity = getCellData(cell);
            if (cell.edge) {
                cellItem = getCellData(cell)
                cellItem.source = getCellData(cell.source);
                cellItem.source.rowIndex = cell.value.getAttribute('sourceRow');
                cellItem.target = getCellData(cell.target);
                cellItem.target.rowIndex = cell.value.getAttribute('targetRow');
            } else {
            }
            return cellItem;
        });
        return cellData;
    }

    updateRelationEntities = () => {
        const relationEntities = this.getGraphData();
        console.log('updateRelationEntities', relationEntities);
        // 此处故意不使用setState 去更新 relationEntities，暂时保存relation数据，避免触发重新渲染
        // eslint-disable-next-line react/no-direct-mutation-state
        this.state.data.relationEntities = relationEntities;
    }

    onOk = () => {
        const form = this._formElem;
        const { data } = this.state;
        const relationEntities = this.getGraphData();
        console.log('graphData:', this, relationEntities);

        form.validateFields(function (err: any, values: IRelation) {
            if (!err) {
                const relation: IRelation = form.getFieldsValue();
                relation.relationEntities = data.relationEntities;
                this.props.onOk(relation);
            }
        });
    }

    render () {
        const { mode } = this.props;
        const { dataSourceList, entities } = this.state;
        const { data } = this.state;
        return (
            <div className="inner-container bg-w" style={{ height: 'auto' }}>
                <div className="c-createRelation__form">
                    <CreateRelationEntityForm
                        mode={mode}
                        ref={(e: any) => this._formElem = e}
                        onCreateRelationEntity={this.onPushEntityNodeData}
                        dataSourceList={dataSourceList}
                        formData={data}
                    />
                </div>
                <div className="c-createRelation__graph" style={{ height: 600 }}>
                    <RelationGraph<IRelationEntity>
                        entities={entities}
                        attachClass="graph-bg"
                        data={data.relationEntities}
                        registerEvent={this.onGraphEvent}
                        registerContextMenu={this.onGraphMenu}
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
