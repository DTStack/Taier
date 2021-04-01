// TODO:但文件代码量过多，待优化
import React, {
  useState,
  useEffect,
  useCallback,
  useRef,
  useMemo,
} from 'react';
import Container from 'pages/DataModel/components/Container';
import { Steps, Button, Form, Modal } from 'antd';
import './style';
import RelationTableModal from './RelationListModal';
import {
  relationFormListGenerator,
  basicInfoFormListGenerator,
  joinItemParser,
  stepContentRender,
  layoutGenerator,
  restoreKeysMap,
} from './constants';
import { joinPairsParser } from './utils';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
import _ from 'lodash';
import { IModelDetail, TableJoinInfo } from 'pages/DataModel/types';
import { EnumModifyStep, EnumModifyMode } from './types';
import BreadCrumbRender from './BreadCrumbRender';
import CodeBlock from 'pages/DataModel/components/CodeBlock';
const idGenerator = () => {
  let _id = 0;
  return () => ++_id + '';
};

const identifyColumns = idGenerator();
const identifyJoinList = idGenerator();
interface IPropsModify {
  form: any;
  router?: any;
  params: any;
}

const { Step } = Steps;

const Modify = (props: IPropsModify) => {
  const { form, router, params } = props;
  const _id = params.id;
  const modelId: number = _id ? parseInt(_id) : _id;
  const mode = modelId === undefined ? EnumModifyMode.ADD : EnumModifyMode.EDIT;
  const breadcrumTitle = mode === EnumModifyMode.ADD ? '新建模型' : '编辑模型';
  const { validateFields, getFieldsValue, setFieldsValue } = form;

  const [current, setCurrent] = useState<EnumModifyStep>(
    EnumModifyStep.BASIC_STEP
  );
  const [visibleRelationModal, setVisibleRelationModal] = useState(false);
  const [formValue, setFormValue] = useState<any>({});
  const [updateTypeList, setUpdateTypeList] = useState([]);
  const [visibleUpdateType, setVisibleUpdateType] = useState(false);
  const [editJoinItem, setEditJoinItem] = useState<null | TableJoinInfo>(null);

  const [dataSourceList, setDataSourceList] = useState([]);
  const [schemaList, setSchemaList] = useState([]);
  const [tableList, setTableList] = useState([]);
  const [visibleSqlpreview, setVisibleSqlPreview] = useState({
    visible: false,
    sql: '',
  });

  const firstRender = useRef(true);

  const formItemLayout = useMemo(() => layoutGenerator(current), [current]);

  const getSchemaList = async (datasourceId: number) => {
    if (!datasourceId) return;
    try {
      const { success, data, message } = await API.getDataModelSchemaList({
        datasourceId: datasourceId,
      });
      if (success) {
        setSchemaList(
          data.map((item) => ({
            key: item,
            value: item,
            label: item,
          }))
        );
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const getUpdataTypeEnum = async () => {
    try {
      const { success, message, data } = await API.getDataModelUpdateTypeList();
      if (success) {
        setUpdateTypeList(
          data.map((item) => ({
            key: item.leftValue,
            value: item.leftValue,
            label: item.rightValue,
          }))
        );
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const getTableList = async (datasourceId: number, schema: string) => {
    if (!datasourceId || !schema) return;
    try {
      const { success, data, message } = await API.getDataModelTableList({
        datasourceId,
        schema,
      });
      if (success) {
        setTableList(
          data.map((item) => ({
            key: item.tableName,
            value: item.tableName,
            label: item.tableName,
            ext: item.partition,
          }))
        );
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const getModelDetail = async (id: number) => {
    try {
      const { success, data, message } = await API.getModelDetail({ id });
      if (success) {
        const columns = data.columns.map((item) => ({
          ...item,
          id: identifyColumns(),
        }));
        const joinList = data.joinList.map((item) => ({
          ...item,
          id: identifyJoinList(),
        }));
        setFormValue({
          ...data,
          columns,
          joinList,
        });
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const saveDataModel = async (
    params?: Partial<IModelDetail>,
    callback?: Function
  ) => {
    try {
      const { success, message } = await API.saveDataModel(params);
      if (success) {
        Message.success('保存成功');
        if (typeof callback) {
          callback();
        }
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  // 是否为分区表
  const isPartition = async (
    datasourceId: number,
    tableName: string,
    schema: string
  ) => {
    try {
      const { success, data, message } = await API.isPartition({
        datasourceId,
        schema,
        tableName,
      });
      if (success) {
        setVisibleUpdateType(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  // 获取所有可用的数据源列表
  const getAllDataSourceList = useCallback(async () => {
    try {
      const { success, data, message } = await API.getAllDataSourceList();
      if (success) {
        const dataSourceList = data.map((item) => ({
          key: item.id,
          value: item.id,
          label: `${item.name}(${item.dsTypeName})`,
          ext: JSON.stringify(item),
        }));
        setDataSourceList(dataSourceList);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  }, []);

  const restoreFormValue = (keys: string[], target) => {
    form.setFieldsValue(
      keys.reduce((temp, key) => {
        temp[key] = target[key];
        return temp;
      }, {})
    );
  };

  const onRelationListDelete = useCallback((id: number) => {
    setFormValue((formValue) => {
      const joinList = formValue.joinList || [];
      return {
        ...formValue,
        joinList: joinList.filter((joinItem) => joinItem.id !== id),
      };
    });
  }, []);

  const onRelationListEdit = useCallback((id: number) => {
    let joinList = [];
    // TODO:获取当前的formValue值,触发了一次更新
    setFormValue((formValue) => {
      joinList = formValue.joinList || [];
      return {
        ...formValue,
      };
    });
    const joinItem = joinList.find((item) => item.id === id);
    if (!joinItem) return;
    setEditJoinItem(joinItem);
    setVisibleRelationModal(true);
  }, []);

  const onSchemaChange = () => {
    // 当schema变化时重置表
    setFieldsValue({
      tableName: undefined,
    });
  };

  const handlePrevStep = () => {
    switch (current) {
      case EnumModifyStep.BASIC_STEP:
      case EnumModifyStep.RELATION_TABLE_STEP:
      case EnumModifyStep.SETTING_STEP:
        setFormValue((prev) => ({
          ...prev,
          ...form.getFieldsValue(),
        }));
        setCurrent((prev) => prev - 1);
        break;
      case EnumModifyStep.DIMENSION_STEP:
      case EnumModifyStep.METRIC_STEP:
        const datasource = cref.current.getValue();
        setFormValue((formValue) => ({
          ...formValue,
          columns: datasource,
        }));
        setCurrent((current) => current - 1);
        break;
    }
  };

  const handleNextStep = () => {
    switch (current) {
      case EnumModifyStep.BASIC_STEP:
      case EnumModifyStep.RELATION_TABLE_STEP:
        validateFields((err, data) => {
          if (err) return;
          setFormValue((prev) => ({
            ...prev,
            ...data,
          }));
          setCurrent((prev) => prev + 1);
        });
        break;
      case EnumModifyStep.DIMENSION_STEP:
      case EnumModifyStep.METRIC_STEP:
        const datasource = cref.current.getValue();
        setFormValue((formValue) => ({
          ...formValue,
          columns: datasource,
        }));
        setCurrent((current) => current + 1);
        break;
      case EnumModifyStep.SETTING_STEP:
        break;
    }
  };

  const getColumnList = async (
    datasourceId: number,
    schema: string,
    tableNames: string[]
  ) => {
    try {
      const { success, data, message } = await API.getDataModelColumns({
        datasourceId,
        schema,
        tableNames,
      });
      if (success) {
        setFormValue((prev) => ({
          ...prev,
          columns: data.map((item) => ({
            ...item,
            id: identifyJoinList(),
          })),
        }));
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const getSql = async (params: any) => {
    try {
      const { success, data, message } = await API.previewSql(params);
      if (success) {
        setVisibleSqlPreview({
          visible: true,
          sql: data,
        });
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  const firstRenderCol = useRef(true);

  useEffect(() => {
    const { dsId, schema, tableName, joinList = [] } = formValue;
    if (!schema || !dsId || !tableName) return;
    if (firstRenderCol.current && mode === EnumModifyMode.EDIT) {
      // 编辑时初始化不调用columns接口
      firstRenderCol.current = false;
      return;
    }
    if (dsId === undefined || schema === undefined || tableName === undefined)
      return;
    getColumnList(
      dsId,
      schema,
      joinList.map((item) => item.leftTable).concat(tableName)
    );
  }, [
    formValue.schema,
    formValue.joinList,
    formValue.dsId,
    formValue.tableName,
  ]);

  // 恢复对应的form value
  useEffect(() => {
    const formKeys = restoreKeysMap.get(current);
    if (
      current === EnumModifyStep.DIMENSION_STEP ||
      current === EnumModifyStep.METRIC_STEP
    )
      return;
    restoreFormValue(formKeys, formValue);
  }, [current, formValue]);

  useEffect(() => {
    getSchemaList(formValue.dsId);
    getAllDataSourceList();
    getUpdataTypeEnum();
  }, []);

  useEffect(() => {
    if (mode === EnumModifyMode.ADD) return;
    getModelDetail(modelId);
  }, [modelId]);

  const currentFormValue = getFieldsValue();

  useEffect(() => {
    getTableList(formValue.dsId, currentFormValue.schema);
  }, [formValue.dsId, currentFormValue.schema]);

  useEffect(() => {
    const { dsId } = formValue;
    const { schema, tableName } = currentFormValue;
    if (!dsId || !schema || !tableName) return;
    isPartition(dsId, schema, tableName);
  }, [formValue.dsId, currentFormValue.schema, currentFormValue.tableName]);

  useEffect(() => {
    if (formValue.dsId === undefined) return;
    getSchemaList(formValue.dsId);
    if (firstRender.current) {
      firstRender.current = false;
      return;
    }
    setFormValue((prev) => ({
      ...prev,
      schema: undefined,
      tableName: undefined,
    }));
  }, [formValue.dsId]);

  const dsItem = useRef<any>({});

  const onDataSourceChange = (value, target) => {
    const ext = target.props['data-ext'];
    try {
      dsItem.current = JSON.parse(ext);
    } catch (err) {
      console.error(err.message);
    }
  };

  const cref = useRef(null);
  const childRef = useRef(null);

  const getFormList = (step: EnumModifyStep) => {
    console.log(formValue);
    switch (step) {
      case EnumModifyStep.BASIC_STEP:
        const id = mode === EnumModifyMode.ADD ? undefined : modelId;
        return basicInfoFormListGenerator({
          options: dataSourceList,
          onDataSourceChange,
          id,
          isDisabled:
            mode === EnumModifyMode.EDIT &&
            current >= EnumModifyStep.BASIC_STEP,
        });
      case EnumModifyStep.RELATION_TABLE_STEP:
        return relationFormListGenerator({
          handleClick: () => {
            // form数据同步至formValue
            const value = form.getFieldsValue();
            setFormValue((formValue) => ({
              ...formValue,
              ...value,
            }));
            setVisibleRelationModal(true);
          },
          updateTyleListOptions: updateTypeList,
          schemaListOptions: schemaList,
          tableListOptions: tableList,
          onSchemaChange,
          visibleUpdateType,
          joinList: formValue.joinList || [],
          onRelationListDelete,
          onRelationListEdit,
          // onMasterTableChange,
        });
    }
  };

  // 将主表以及关联表拼接
  const getRelationTableList = () => {
    const relationList = [];
    if (formValue.tableName && formValue.schema) {
      relationList.push({
        tableName: formValue.tableName,
        schema: formValue.schema,
        tableAlias: undefined,
        id: undefined,
      });
    }
    if (formValue.joinList) {
      formValue.joinList.forEach((joinItem) => {
        relationList.push({
          tableName: joinItem.table,
          schema: joinItem.schema,
          tableAlias: joinItem.tableAlias,
          id: joinItem.id,
        });
      });
    }
    return relationList;
  };
  // 关联表列表
  const relationTableList = getRelationTableList();

  const handleModalOk = () => {
    childRef.current.validate((err, data, id) => {
      if (err) return;
      // form数据转化
      const joinItem = joinItemParser(data);
      joinItem.joinPairs = joinItem.joinPairs.map(joinPairsParser.decode);
      let joinList = formValue.joinList || [];
      if (!id) {
        // 新增关联关系
        joinList.push(joinItem);
      } else {
        // 编辑关联关系
        joinList = joinList.map((item) => {
          if (item.id === id) {
            return joinItem;
          } else {
            return item;
          }
        });
      }
      setFormValue((formValue) => ({
        ...formValue,
        joinList,
      }));
      setVisibleRelationModal(false);
    });
  };

  return (
    <Container>
      <div className="dm-model-modify">
        <div className="breadcrumb-area">
          <BreadCrumbRender
            links={[
              { label: '数据模型', href: '/data-model/list' },
              { label: breadcrumTitle },
            ]}
          />
        </div>
        <div className="content">
          <header className="step-header">
            <Steps current={current}>
              <Step title="基础信息" />
              <Step title="表关联" />
              <Step title="选择维度" />
              <Step title="选择度量" />
              <Step title="设置" />
            </Steps>
          </header>
          <div className="step-content padding-tb-20">
            <div className="inner-container overflow-auto">
              <Form
                layout="horizontal"
                className="form-area"
                {...formItemLayout}>
                {stepContentRender(current, {
                  formList: getFormList(current),
                  form,
                  cref: (ref) => (cref.current = ref),
                  formValue,
                })}
              </Form>
              {visibleRelationModal ? (
                <Modal
                  title={editJoinItem ? '编辑关联表' : '添加关联表'}
                  visible={visibleRelationModal}
                  onCancel={() => {
                    setVisibleRelationModal(false);
                    setEditJoinItem(null);
                  }}
                  onOk={handleModalOk}>
                  <RelationTableModal
                    cref={(ref) => (childRef.current = ref)}
                    data={editJoinItem}
                    tables={relationTableList}
                    schemaList={schemaList}
                    dataSourceId={formValue.dsId}
                  />
                </Modal>
              ) : null}
              <Modal title="预览SQL" visible={visibleSqlpreview.visible}>
                <CodeBlock code={visibleSqlpreview.sql} />
              </Modal>
            </div>
          </div>
          <footer className="step-footer">
            <div className="button-area">
              {current === EnumModifyStep.BASIC_STEP ? (
                <Button
                  className="margin-right-8 width-80"
                  onClick={() => router.push('/data-model/list')}>
                  取消
                </Button>
              ) : null}
              {current > EnumModifyStep.BASIC_STEP ? (
                <Button
                  className="margin-right-8 width-80"
                  onClick={handlePrevStep}>
                  上一步
                </Button>
              ) : null}
              {current < EnumModifyStep.SETTING_STEP ? (
                <Button
                  className="margin-right-8 width-80"
                  type="primary"
                  onClick={handleNextStep}>
                  下一步
                </Button>
              ) : null}
              {current === EnumModifyStep.SETTING_STEP ? (
                <Button
                  className="margin-right-8 width-80"
                  type="primary"
                  onClick={() => {
                    form.validateFields((err, data) => {
                      if (err) return;
                      const params = {
                        ...formValue,
                        ...data,
                      };
                      getSql(params);
                    });
                  }}>
                  预览SQL
                </Button>
              ) : null}
              <Button
                onClick={() => {
                  form.validateFields((err, data) => {
                    if (err) return;
                    const {
                      dsType = formValue.dsType,
                      dsUrl = formValue.dsUrl,
                    } = dsItem.current;
                    console.log(dsType, dsUrl);
                    const params = {
                      ...formValue,
                      ...data,
                      dsType,
                      dsUrl,
                      step: current + 1,
                    };
                    saveDataModel(params, () =>
                      router.push('/data-model/list')
                    );
                  });
                }}
                type="primary">
                保存并退出
              </Button>
            </div>
          </footer>
        </div>
      </div>
    </Container>
  );
};

export default Form.create()(Modify);
