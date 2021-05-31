import React, { useEffect, useRef, useState } from 'react';
import GraphEditor from './GraphEditor';
import { IModelDetail } from '../../types';
import { EnumNodeType, IRelationTree, EnumTableType } from './types';
import { mapJoinType } from './constants';
import { loop } from './utils';
import './style';
import _ from 'highlight.js/lib/languages/*';

/**
 *
 * @param columns
 * @param joinList 父节点的关联表信息
 * @returns
 */
export const getColumnsByTableAliasGenerator = (columns, joinList) => {
  /**
   * 根据父节点表别名生成展示的columns列表
   * @param tableAlis 父节点表别名
   */
  return (tableAlias) => {
    // 以tableAlias作为左表关联的关联关系
    const leftJoinList = joinList
      .filter((item) => item.leftTableAlias === tableAlias)
      .map((item) => item.joinPairs.map((item) => item.leftValue))
      .reduce((temp, current) => {
        return [...temp, ...current];
      }, []);
    // 以tableAlis作为右表关联的关联关系
    const rightJoinList = joinList
      .filter((item) => item.tableAlias === tableAlias)
      .map((item) => item.joinPairs.map((item) => item.rightValue))
      .reduce((temp, current) => {
        return [...temp, ...current];
      }, []);

    // 将关联键字段与选中的度量维度列表拼接生成展示的columnList
    // TODO: 需要确认：为勾选维度度量的分区字段是否需要显示
    const cols = columns.filter(
      (col) => (col.metric || col.dimension) && col.tableAlias === tableAlias
    );
    [...leftJoinList, ...rightJoinList].forEach((col) => {
      if (cols.findIndex((item) => item.columnName === col.columnName) === -1) {
        cols.push({
          ...col,
          _type: EnumTableType.RELATION,
        });
      }
    });
    return cols;
  };
};

export const relationViewTreeParser = (
  modelDetail: Partial<IModelDetail>
): IRelationTree => {
  const { columns, joinList } = modelDetail;
  const getColumnsByTableAlias = getColumnsByTableAliasGenerator(
    columns,
    joinList
  );
  const createTableNodeChildren = (
    parentNodeAlias: string,
    joinInfoList: any[]
  ) => {
    const list = joinList.filter(
      (item) => item.leftTableAlias === parentNodeAlias
    );
    if (list.length === 0) return [];
    list.map((joinItem) => ({
      joinType: joinItem.joinType,
      joinPairs: joinItem.joinPairs,
      tableAlias: joinItem.tableAlias,
    }));
    return list.map((item) => ({
      tableName: item.table,
      tableAlias: item.tableAlias,
      columns: getColumnsByTableAlias(item.tableAlias),
      joinInfo: joinInfoList.find(
        (joinInfoItem) => joinInfoItem.tableAlias === item.tableAlias
      ),
      children: createTableNodeChildren(
        item.tableAlias,
        joinList.filter((joinInfoItem) => ({
          JoinType: joinInfoItem.joinType,
          joinPairs: joinInfoItem.joinPairs,
          tableAlias: joinInfoItem.tableAlias,
        }))
      ),
      _tableType: 'relation',
    }));
  };

  const mainJoinList = modelDetail.joinList.map((item) => ({
    joinType: item.joinType,
    joinPairs: item.joinPairs,
    tableAlias: item.tableAlias,
  }));

  // 主表信息
  const mainTable = {
    tableName: modelDetail.tableName,
    tableAlias: 't0',
    columns: getColumnsByTableAlias('t0'),
    joinInfo: null,
    children: createTableNodeChildren('t0', mainJoinList),
    _tableType: EnumTableType.PRIMARY,
  };
  return mainTable;
};

interface IPropsRelationView {
  modelDetail: Partial<IModelDetail>;
}

const RelationView = (props: IPropsRelationView) => {
  const { modelDetail } = props;
  if (!modelDetail.id) return null;
  const [loading, setLoading] = useState(false);
  // detail数据转化成tree格式
  const tree = relationViewTreeParser(modelDetail);

  const refGraph = useRef(null);
  const rootCell = useRef(null);
  const refGraphEditor = useRef(null);
  const refMx = useRef(null);

  // 关联表渲染逻辑
  const tableRender = (graph) => {
    return (
      data: { tableName: string; columnList: any[]; _tableType: string },
      position: { x?: number; y?: number } = {}
    ) => {
      const { tableName, columnList, _tableType } = data;
      const { x = 0, y = 0 } = position;
      const _tableCellList = [];
      const cellWidth = 180;
      const cellHeight = 32;
      const parent = graph.getDefaultParent();
      const height = (columnList.length + 1) * cellHeight;
      const tableNameCellColor =
        _tableType === EnumTableType.PRIMARY ? '#3F87FF' : '#FFB310';
      const tableNameFontColor = '#FFFFFF';
      const colNameCellColor = '#FFFFFF';
      const colNameCellColorNoneModel = '#F2F9FF'; // 非模型字段颜色

      const parentCell = graph.insertVertex(
        parent,
        null,
        '',
        x,
        y,
        cellWidth,
        height,
        `strokeColor=${tableNameCellColor};`
      );

      // 表名称渲染
      const tableNameCell = graph.insertVertex(
        parentCell,
        null,
        tableName,
        0,
        0,
        cellWidth,
        cellHeight,
        `fillColor=${tableNameCellColor};fontColor=${tableNameFontColor};strokeColor=${tableNameCellColor};align=left;`
      );
      tableNameCell.nodeType = EnumNodeType.TABLE_NAME;
      tableNameCell.geometry.relative = true;
      _tableCellList.push(tableNameCell);
      columnList.forEach((column, index) => {
        const scrollTop = (index + 1) / (columnList.length + 1);
        const _colFillColor =
          column._type === EnumTableType.RELATION
            ? colNameCellColorNoneModel
            : colNameCellColor;
        const cell = graph.insertVertex(
          parentCell,
          null,
          column.columnName,
          0,
          scrollTop,
          cellWidth,
          cellHeight,
          `fillColor=${_colFillColor};strokeColor=#E8E8E8;align=left;`
        );
        cell.nodeType = EnumNodeType.COLUMN_NAME;
        cell._data = column;
        cell.geometry.relative = true;
        _tableCellList.push(cell);
      });
      // 渲染外边框
      const wrapper = graph.insertVertex(
        parentCell,
        null,
        null,
        0,
        0,
        cellWidth,
        height,
        `strokeColor=${tableNameCellColor};`
      );
      wrapper.geometry.relative = true;
      return _tableCellList;
    };
  };

  const getLabel = (cell) => {
    if (cell.edge === true) {
      return '<div style="background: #ffffff;">' + cell.value + '</div>';
    }
    switch (cell.nodeType) {
      case EnumNodeType.COLUMN_NAME:
        const _color = cell._data.partition ? '#3F87FF' : '#333333';
        return (
          '<div class="margin-left-12" style="color: ' +
          _color +
          ';">' +
          cell.value +
          '</div>'
        );
      case EnumNodeType.TABLE_NAME:
        return (
          '<div class="margin-left-12">' +
          '<span class="iconfont2 iconFilltianchong_biao"></span>' +
          '<span style="vertical-align: 2px; margin-left: 8px;">' +
          cell.value +
          '</span>' +
          '</div>'
        );
      default:
        return cell.value;
    }
  };

  const insertEdge = (parent: any, label: string, source: any, target: any) => {
    refGraph.current.insertEdge(parent, null, label, source, target);
  };

  const executeLayout = (parent: any, Layout: any, option: any = {}) => {
    const _layout = new Layout(refGraph.current);
    Object.keys(option).forEach((key) => {
      _layout[key] = option[key];
    });
    _layout.execute(parent);
  };

  const handleInit = (_grapth, mx) => {
    refGraph.current = _grapth;
    refMx.current = mx;
  };

  const update = (tree) => {
    refGraph.current.getLabel = getLabel;
    const model = refGraph.current.getModel();
    model.beginUpdate();
    // 清空画布
    model.clear();
    setLoading(true);
    refGraph.current.labelsVisible = true;
    try {
      rootCell.current = refGraph.current.getDefaultParent();
      const render = tableRender(refGraph.current);
      const map = new Map();
      loop(tree, (item) => {
        const list = render({
          tableName: item.tableName,
          columnList: item.columns,
          _tableType: item._tableType,
        });
        map.set(item.tableAlias, list);
        const { joinInfo } = item;
        if (joinInfo) {
          const joinPairs = joinInfo.joinPairs;
          joinPairs.map((joinItem) => {
            const leftTableColumnList = map.get(joinItem.leftValue.tableAlias);
            const rightTableColumnList = map.get(
              joinItem.rightValue.tableAlias
            );
            const cellLeftCol = leftTableColumnList.find(
              (item) => item.value === joinItem.leftValue.columnName
            );
            const cellRightcol = rightTableColumnList.find(
              (item) => item.value === joinItem.rightValue.columnName
            );
            insertEdge(
              rootCell.current,
              mapJoinType.get(item.joinInfo.joinType),
              cellLeftCol,
              cellRightcol
            );
          });
        }
      });
      const { mxHierarchicalLayout } = refMx.current;
      executeLayout(refGraph.current.getDefaultParent(), mxHierarchicalLayout, {
        orientation: 'west',
        disableEdgeStyle: false,
        interRankCellSpacing: 200,
        intraCellSpacing: 80,
      });
    } catch (err) {
      throw err;
    } finally {
      setLoading(false);
      model.endUpdate();
    }
  };

  useEffect(() => {
    update(tree);
  }, [modelDetail.id]);

  return (
    <div className="relation-view">
      <GraphEditor
        ref={(ref) => (refGraphEditor.current = ref)}
        rootCell={rootCell.current}
        loading={loading}
        name="name"
        onInit={handleInit}
      />
    </div>
  );
};

export default RelationView;
