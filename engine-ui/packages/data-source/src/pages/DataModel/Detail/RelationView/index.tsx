/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect, useRef, useState } from 'react';
import GraphEditor from './GraphEditor';
import { IModelDetail } from '../../types';
import { EnumNodeType, IRelationTree, EnumTableType } from './types';
import { mapJoinType } from './constants';
import { loop, styleStringGenerator } from './utils';
import './style';
import _ from 'highlight.js/lib/languages/*';
const TABLE_ICON =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAXUlEQVQ4T9WSOw7AIAxD7XuV05d7GcGAQmEIpAsZI/vJ+RDBYtCPDpD0AnicwEwyVa0FyGluMpLN++sIPUGlSxoSfXtTAmu4FLBzAatdnvHSHdhXdowwv3J4iaeAAlCJahETJR7XAAAAAElFTkSuQmCC';

const mxStyleString = styleStringGenerator('=');
const domStyleString = styleStringGenerator(':');

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
  const refCenterCell = useRef(null);

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
        mxStyleString({
          strokeColor: tableNameCellColor,
        })
      );

      if (_tableType === EnumTableType.PRIMARY)
        refCenterCell.current = parentCell;

      // 表名称渲染
      const tableNameCell = graph.insertVertex(
        parentCell,
        null,
        tableName,
        0,
        0,
        cellWidth,
        cellHeight,
        mxStyleString({
          fillColor: tableNameCellColor,
          fontColor: tableNameFontColor,
          strokeColor: tableNameCellColor,
          align: 'left',
        })
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
          mxStyleString({
            fillColor: _colFillColor,
            strokeColor: '#E8E8E8',
            align: 'left',
          })
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
        mxStyleString({
          strokeColor: tableNameCellColor,
        })
      );
      wrapper.geometry.relative = true;
      return _tableCellList;
    };
  };

  const getLabel = (cell) => {
    if (cell.edge === true) {
      const sourceCell = cell.source;
      const targetCell = cell.target;
      const sourceParent = sourceCell.parent;
      const targetParent = targetCell.parent;
      const offsetY =
        sourceParent.geometry.height * sourceCell.geometry.y +
        sourceParent.geometry.y -
        (targetParent.geometry.height * targetCell.geometry.y +
          targetParent.geometry.y);
      const offsetX =
        sourceParent.geometry.width +
        sourceParent.geometry.x -
        targetParent.geometry.x;

      console.log(offsetX, offsetY);
      cell.geometry.offset = {
        x: -offsetX / 2 - 22,
        y: -offsetY / 2,
      };
      return `<div style="${domStyleString({
        background: '#ffffff',
        padding: '0 2px',
      })}">${cell.value}</div>`;
    }
    switch (cell.nodeType) {
      case EnumNodeType.COLUMN_NAME:
        const _color = cell._data.partition ? '#3F87FF' : '#333333';
        return `<div style="${domStyleString({
          color: _color,
          'margin-left': '12px',
        })}">${cell.value}</div>`;
      case EnumNodeType.TABLE_NAME:
        return `<div style="${domStyleString({
          'margin-left': '12px',
        })}"><img src="${TABLE_ICON}" style="width: 16px; height: 16px; vertical-align: middle;"/><span style="${domStyleString(
          {
            'vertical-align': '-2px',
            'margin-left': '8px',
            color: '#ffffff',
          }
        )}">${cell.value}</span></div>`;
      default:
        return cell.value;
    }
  };

  const insertEdge = (parent: any, label: string, source: any, target: any) => {
    refGraph.current.insertEdge(parent, null, label, source, target);
  };

  const alignCenter = (graph: any) => {
    refGraph.current.fit();
    setTimeout(() => {
      const sc = refGraph.current.getView().getScale();
      refGraph.current.zoomTo(sc * 0.8);
      refGraph.current.center(false, false, 0.5, 0.5);
    }, 20);
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
    rootCell.current = refGraph.current.getDefaultParent();
  };

  // const refTime = useRef(0);
  // const checkTime = () => {
  //   const now = new Date().valueOf();
  //   if (now - refTime.current > 5) {
  //     refTime.current = new Date('9999-09-09').valueOf();
  //     setLoading(false)
  //   }
  // }

  const update = (tree) => {
    // refTime.current = new Date().valueOf();
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
        // checkTime();
        const list = render({
          tableName: item.tableName,
          columnList: item.columns,
          _tableType: item._tableType,
        });
        map.set(item.tableAlias, list);
        const { joinInfo } = item;
        if (joinInfo) {
          const joinPairs = joinInfo.joinPairs;
          joinPairs.forEach((joinItem) => {
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
        interRankCellSpacing: 80,
        intraCellSpacing: 40,
      });
      alignCenter(refGraph.current);
    } catch (err) {
      throw err;
    } finally {
      setLoading(false);
      model.endUpdate();
    }
  };

  useEffect(() => {
    update(tree);
  }, [modelDetail]);

  return (
    <div className="relation-view">
      <GraphEditor
        ref={(ref) => (refGraphEditor.current = ref)}
        rootCell={refCenterCell.current}
        loading={loading}
        name={modelDetail.modelName}
        onInit={handleInit}
        alignCenter={alignCenter}
      />
    </div>
  );
};

export default RelationView;
