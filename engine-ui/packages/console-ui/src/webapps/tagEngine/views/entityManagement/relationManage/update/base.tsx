import * as React from 'react';
import { cloneDeep, get } from 'lodash';
import { Row, Button, Col } from 'antd';

import MxFactory from 'widgets/mxGraph';
import { updateComponentState } from 'funcs';

import { tailFormItemLayout } from '../../../../comm/const';
import { IDataSource } from '../../../../model/dataSource';

import CreateRelationEntityForm from './form';
import { API } from '../../../../api/apiMap';
import DataSourceAPI from '../../../../api/dataSource';
import RelationGraph, { GRAPH_MODE } from '../../../../components/relationGraph';
import { IRelation, IRelationEntity } from '../../../../model/relation';
import { IEntity } from '../../../../model/entity';

interface IState {
    dataSourceList: IDataSource[];
    entities: IEntity[];
    data: IRelation;
}

interface IProps {
    relationData?: IRelation;
    mode?: GRAPH_MODE;
    onOk?: (data?: any) => void;
}

const initialEntityNode: IRelationEntity = {
    id: null,
    name: '',
    columns: [{
        id: null,
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
} = Mx;

class RelationUpdateBase extends React.Component<IProps, IState> {
    state: IState = {
        dataSourceList: [],
        entities: [],
        data: {
            relationCollection: []
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
        const { relationData } = this.props;
        this.loadDataSource();
        if (relationData && relationData.dataSourceId) {
            this.loadEntities(relationData.dataSourceId);
        }
    }

    componentWillUnmount () {
        document.removeEventListener('change', this.onSelectChange);
    }

    loadDataSource = async (query?: string) => {
        const res = await DataSourceAPI.getTagDataSourceList({
            current: 1,
            search: query
        });
        if (res.code === 1) {
            this.setState({
                dataSourceList: get(res, 'data.contentList', [])
            })
        }
    }

    loadEntities = async (dataSourceId: string | number) => {
        const res = await API.getEntities({ dataSourceId });
        if (res.code === 1) {
            this.setState({
                entities: get(res, 'data.contentList', [])
            })
        }
    }

    onFormValuesChange = (values: IRelation) => {
        if (values.dataSourceId) {
            this.loadEntities(values.dataSourceId);
            // 数据源改变时，需要清理画布上的数据
            updateComponentState(this, {
                relationCollection: []
            })
        }
    }

    onPushEntityNodeData = () => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        if (!newState.relationCollection) {
            newState.relationCollection = [];
        }
        //  else {
        //     newState.relationCollection = [...data.relationCollection];
        // }

        // 通过数组长度，计算新节点默认位置
        const defaultPosition = newState.relationCollection.length * 80;
        initialEntityNode.geometry.x = defaultPosition;
        initialEntityNode.geometry.y = defaultPosition;
        newState.relationCollection.push(initialEntityNode);
        this.setState({
            data: newState
        })
    }

    onRelationEntityChange = async (relationEntityIndex: number, relationEntity: IRelationEntity) => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        let oldRelationEntity: IRelationEntity = newState.relationCollection[relationEntityIndex];
        if (relationEntity.id !== oldRelationEntity.id || !oldRelationEntity) {
            oldRelationEntity = Object.assign(oldRelationEntity, relationEntity);
            const res = await API.selectEntityAttrs({ entityId: relationEntity.id });
            if (res.code === 1) {
                // 关系实体变更时，需要重新加载该实体的维度列表信息
                // 初始化维度选项
                oldRelationEntity.columns = [{
                    id: -1,
                    name: null
                }];
                oldRelationEntity.columnOptions = get(res, 'data', []); // mockColumns;
                newState.relationCollection[relationEntityIndex] = oldRelationEntity;
                this.setState({
                    data: newState
                })
            }
        }
    }

    onPushRelationEntityCol = (relationEntityIndex: number) => {
        const { data } = this.state;
        const newState = cloneDeep(data);
        const relationEntity: IRelationEntity = newState.relationCollection[relationEntityIndex];
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
        let oldRelationEntityCol: IRelationEntity = newState.relationCollection[relationEntityIndex].columns[relationEntityColumnIndex];
        if (relationEntityCol.id !== oldRelationEntityCol.id || !oldRelationEntityCol) {
            newState.relationCollection[relationEntityIndex].columns[relationEntityColumnIndex] = Object.assign({}, oldRelationEntityCol, relationEntityCol);
            // eslint-disable-next-line no-undef
            console.log('onRelationEntityColumnChange:', relationEntityIndex, relationEntityColumnIndex, newState);
            this.setState({
                data: newState
            });
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
                const sourceTable = selectedOption.getAttribute('data-dataSourceTable');
                const entityName = selectedOption.getAttribute('data-entityName');
                const index = parseInt(e.target.getAttribute('data-index'), 10);
                ctx.onRelationEntityChange(index, { id: id, name: entityName, dataSourceTable: sourceTable });
            } else if (isRelationEntityColumnSelected) {
                const indexArr = e.target.getAttribute('data-index').split('-');
                const attrNameCN = selectedOption.getAttribute('data-entityAttrCn');
                const entityAttr = selectedOption.getAttribute('data-entityAttr');
                const entityIndex = parseInt(indexArr[0], 10);
                const columnIndex = parseInt(indexArr[1], 10);
                ctx.onRelationEntityColumnChange(entityIndex, columnIndex, { id: id, attrId: id, attrName: entityAttr, attrNameCN: attrNameCN });
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
            const cellData: IRelationEntity = cell.vertex ? Object.assign({}, cell.value) : { id: cell.id, geometry: {} };
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
                cellItem.source.rowIndex = Number(cell.value.getAttribute('sourceRow'));
                const sourceRow = cellItem.source.columnOptions[cellItem.source.rowIndex - 1];
                cellItem.source.attrId = sourceRow.id;
                cellItem.source.attrName = sourceRow.entityAttr;

                cellItem.target = getCellData(cell.target);
                cellItem.target.rowIndex = Number(cell.value.getAttribute('targetRow'));
                const targetRow = cellItem.target.columnOptions[cellItem.target.rowIndex - 1];
                cellItem.target.attrId = targetRow.id;
                cellItem.target.attrName = targetRow.entityAttr;
            }
            return cellItem;
        });
        return cellData;
    }

    updateRelationEntities = () => {
        const relationCollection = this.getGraphData();
        console.log('updateRelationEntities', relationCollection);
    }

    onOk = () => {
        const ctx = this;
        const form = this._formElem;
        const relationCollection = this.getGraphData();
        console.log('graphData:', this, relationCollection);

        form.validateFields(function (err: any, values: IRelation) {
            if (!err) {
                const relation: IRelation = form.getFieldsValue();
                relation.relationCollection = relationCollection;
                console.log('submit GraphData:', JSON.stringify(relation));
                ctx.props.onOk(relation);
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
                        ref={(inst: any) => this._formElem = inst}
                        onCreateRelationEntity={this.onPushEntityNodeData}
                        dataSourceList={dataSourceList}
                        loadDataSource={this.loadDataSource}
                        formData={data}
                        onFormValuesChange={this.onFormValuesChange}
                    />
                </div>
                <div className="c-createRelation__graph" style={{ height: 600 }}>
                    <RelationGraph<IRelationEntity>
                        entities={entities}
                        mode={mode}
                        attachClass="graph-bg"
                        data={data.relationCollection}
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
