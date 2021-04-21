import React, {
  useState,
  useMemo,
  useImperativeHandle,
  useRef,
  useEffect,
} from 'react';
import { Table, Modal } from 'antd';
import { columnsGenerator } from './constants';
import { TableJoinInfo, IModelDetail } from 'pages/DataModel/types';
import RelationTableModal from '../RelationTableModal';
import _ from 'lodash';
import { relationListRemove } from './utils';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
import { emit } from 'node:process';

interface IPropsRelationList {
  updateTypeList: any[];
  modelDetail: Partial<IModelDetail>;
  cref: any;
  updateModelDetail: Function;
}

enum Mode {
  ADD = 'ADD',
  EDIT = 'EDIT',
}

const idGenerator = () => {
  let _id = 0;
  return () => 'cus_' + ++_id;
};

const identifyJoinList = idGenerator();
const identifyColumns = idGenerator();

const RelationList = (props: IPropsRelationList) => {
  const { updateTypeList, modelDetail, cref } = props;
  const [modifyType, setModifyType] = useState<{
    visible: boolean;
    mode: Mode;
    value: any;
  }>({ visible: false, mode: Mode.ADD, value: {} });
  const [relationList, setRelationList] = useState<TableJoinInfo[]>([]);
  const refRelationModal = useRef(null);
  useImperativeHandle(cref, () => {
    return {
      validate: () =>
        new Promise((resolve) => {
          return resolve(relationList);
        }),
      getValue: () => {
        return relationList;
      },
    };
  });

  useEffect(() => {
    setRelationList(modelDetail.joinList || []);
  }, [modelDetail]);

  // 将远程获取的columnList和当前勾选的恶columnList进行整合
  const combineColumnList = async (joinList: any[]) => {
    const columns = modelDetail.columns || [];
    try {
      const { success, message, data } = await API.getDataModelColumns(
        joinList
      );
      if (success) {
        const col = data.map((col) => {
          const target = columns.find((item) => {
            return (
              item.tableName === col.tableName &&
              item.schema === col.schema &&
              item.columnName === col.columnName
            );
          });
          if (!target)
            return {
              ...col,
              id: `col_${identifyColumns()}`,
            };
          return {
            ...col,
            metric: target.metric,
            dimension: target.dimension,
            id: target.id || `col_${identifyColumns()}`,
          };
        });
        window.localStorage.setItem('refreshColumns', 'false');
        props.updateModelDetail((modelDetail) => ({
          ...modelDetail,
          columns: col,
        }));
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const onRelationListDelete = (id: number | string) => {
    Modal.confirm({
      title: (
        <span className="cus-modal margin-left-40">
          确认删除该条关联记录吗？
        </span>
      ),
      content: (
        <span className="cus-modal margin-left-40">
          删除后，所有相关的关联关系将会被移除！
        </span>
      ),
      onOk() {
        const list = relationListRemove(relationList, id, {
          schema: modelDetail.schema,
          tableName: modelDetail.tableName,
        });
        setRelationList(list);

        const params = tableListGen(
          modelDetail.dsId,
          {
            tableName: modelDetail.tableName,
            schema: modelDetail.schema,
          },
          list
        ).map((item) => ({
          datasourceId: item.dsId,
          schema: item.schema,
          tableName: item.tableName,
        }));
        // TODO:删除关联表后更新列表
        combineColumnList(params);
      },
      okText: '删除',
      cancelText: '取消',
      okButtonProps: {
        className: 'cus-modal btn-delete',
      },
      icon: (
        <i className="cus-modal icon iconfont2 iconFilltianchong_Close-Circle-Fill" />
      ),
    });
  };

  const onRelationListEdit = (id: number | string) => {
    setModifyType({
      visible: true,
      mode: Mode.EDIT,
      value: relationList.find((item) => item.id === id),
    });
  };

  const columns = columnsGenerator({
    onDelete: onRelationListDelete,
    onEdit: onRelationListEdit,
  });

  const onClick = () => {
    setModifyType({
      visible: true,
      mode: Mode.ADD,
      value: {
        joinPairs: [
          {
            leftValue: {},
            rightValue: {},
          },
        ],
      },
    });
  };

  const tableListGen = (dsId, mainTable, relationList) => {
    const tables = [];
    if (mainTable.tableName && mainTable.schema) {
      tables.push({
        dsId,
        schema: modelDetail.schema,
        tableName: modelDetail.tableName,
        tableAlias: undefined,
      });
    }
    tables.push(
      ..._.uniqBy(relationList, (item) => item.schema + item.table).map(
        (table) => ({
          dsId: modelDetail.dsId,
          schema: table.schema,
          tableName: table.table,
          tableAlias: table.tableAlias,
        })
      )
    );
    return tables;
  };

  // TODO: 逻辑已抽离，待调整
  const tableList = useMemo(() => {
    const tables = [];
    if (modelDetail.tableName && modelDetail.schema) {
      tables.push({
        dsId: modelDetail.dsId,
        schema: modelDetail.schema,
        tableName: modelDetail.tableName,
        tableAlias: undefined,
      });
    }
    tables.push(
      ..._.uniqBy(relationList, (item) => item.schema + item.table).map(
        (table) => ({
          dsId: modelDetail.dsId,
          schema: table.schema,
          tableName: table.table,
          tableAlias: table.tableAlias,
        })
      )
    );
    return tables;
  }, [modelDetail.tableName, modelDetail.schema, relationList]);

  return (
    <div ref={cref}>
      {modifyType.visible ? (
        <Modal
          title={modifyType.mode === Mode.ADD ? '添加关联表' : '编辑关联表'}
          visible={modifyType.visible}
          onOk={() => {
            refRelationModal.current.validate().then((data) => {
              let next = [];
              if (modifyType.mode === Mode.ADD) {
                data.id = identifyJoinList();
                next = [...relationList, data];
                window.localStorage.setItem('refreshColumns', 'true');
                // TODO:拿到数据后请求更新columnList
                const params = tableListGen(
                  modelDetail.dsId,
                  {
                    tableName: modelDetail.tableName,
                    schema: modelDetail.schema,
                  },
                  next
                ).map((item) => ({
                  datasourceId: item.dsId,
                  schema: item.schema,
                  tableName: item.tableName,
                }));
                combineColumnList(params);
              } else {
                const id = modifyType.value.id;
                next = relationList.map((item) => {
                  if (item.id === id) {
                    return {
                      ...data,
                      id,
                    };
                  } else {
                    return item;
                  }
                });
              }
              setRelationList(next);
              setModifyType((modifyType) => ({
                ...modifyType,
                visible: false,
              }));
            });
          }}
          onCancel={() =>
            setModifyType((modifyType) => ({ ...modifyType, visible: false }))
          }>
          <RelationTableModal
            cref={refRelationModal}
            updateTypeList={updateTypeList}
            tableList={tableList}
            mode={modifyType.mode}
            value={modifyType.value}
            modelDetail={modelDetail}
          />
        </Modal>
      ) : null}
      <span className="btn-link" onClick={onClick}>
        + 添加关联表
      </span>
      <Table
        rowKey="id"
        className="relation-list dt-table-border"
        columns={columns}
        dataSource={relationList}
        pagination={false}
        scroll={{ x: 600, y: 300 }}
      />
    </div>
  );
};

export default React.memo(
  React.forwardRef(RelationList),
  (curProps, nextProps) => {
    const curDetail = curProps.modelDetail;
    const nextDetail = nextProps.modelDetail;
    // 仅在dsId，schema，tableName字段更新后，渲染组件
    if (
      curDetail.dsId === nextDetail.dsId &&
      curDetail.schema === nextDetail.schema &&
      curDetail.tableName === nextDetail.tableName &&
      curProps.updateTypeList === nextProps.updateTypeList
    ) {
      return true;
    } else {
      return false;
    }
  }
);
