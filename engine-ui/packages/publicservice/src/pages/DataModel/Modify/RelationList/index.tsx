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

interface IPropsRelationList {
  updateTypeList: any[];
  modelDetail: Partial<IModelDetail>;
  cref: any;
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

  const tableList = useMemo(() => {
    const tables = [];
    if (modelDetail.tableName && modelDetail.schema) {
      tables.push({
        dsId: modelDetail.dsId,
        schema: modelDetail.schema,
        tableName: modelDetail.tableName,
        tableAlias: modelDetail.tableName,
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
          title="添加关联表"
          visible={modifyType.visible}
          onOk={() => {
            refRelationModal.current.validate().then((data) => {
              let next = [];
              if (modifyType.mode === Mode.ADD) {
                data.id = identifyJoinList();
                next = [...relationList, data];
                window.localStorage.setItem('refreshColumns', 'true');
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

export default React.forwardRef(RelationList);
