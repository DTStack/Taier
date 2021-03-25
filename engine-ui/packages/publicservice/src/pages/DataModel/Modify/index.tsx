import React, {
  useState,
  useEffect,
  useCallback,
  useRef,
  useMemo,
} from 'react';
import Container from 'pages/DataModel/components/Container';
import { Breadcrumb, Steps, Button, Form, Modal } from 'antd';
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
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';
import _ from 'lodash';
import { TableJoinInfo } from 'pages/DataModel/types';
import { EnumModifyStep } from './types';

interface IPropsModify {
  form: any;
  history?: any;
  match?: any;
}

const { Step } = Steps;

const Modify = (props: IPropsModify) => {
  const modelId = props.match.params.id;
  const mode = modelId === undefined ? 'ADD' : 'EDIT';
  const breadcrumTitle = mode === 'ADD' ? '新建模型' : '编辑模型';
  const [current, setCurrent] = useState<EnumModifyStep>(
    EnumModifyStep.BASIC_STEP
  );
  const [visibleRelationModal, setVisibleRelationModal] = useState(false);
  const { validateFields, getFieldsValue, setFieldsValue } = props.form;
  const [formValue, setFormValue] = useState<any>({});
  const [dataSourceList, setdataSourceList] = useState([]);
  const [schemaList, setSchemaList] = useState([]);
  const [tableList, setTableList] = useState([]);
  const [updateTypeList, setUpdateTypeList] = useState([]);
  const [visibleUpdateType] = useState(true);
  const [editJoinItem, setEditJoinItem] = useState<null | TableJoinInfo>(null);
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
            partition: item.partition,
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
        setFormValue({
          ...data,
          tableName: 'dim_coupon_record_df',
        });
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
      const {
        success,
        data,
        message,
      } = await API.getDataModelUsedDataSourceList();
      if (success) {
        const dataSourceList = data.map((item) => ({
          key: item.id,
          value: item.id,
          label: `${item.name}(${item.dsTypeName})`,
        }));
        setdataSourceList(dataSourceList);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  }, []);

  const restoreFormValue = (keys: string[], target) => {
    props.form.setFieldsValue(
      keys.reduce((temp, key) => {
        temp[key] = target[key];
        return temp;
      }, {})
    );
  };

  const onSchemaChange = () => {
    // 当schema变化时重置表
    setFieldsValue({
      tableName: undefined,
    });
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
    // TODO:获取当亲啊的formValue值,触发了一次更新
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

  const handleNextStep = () => {
    switch (current) {
      case EnumModifyStep.BASIC_STEP:
      case EnumModifyStep.RELATION_TABLE_STEP:
        validateFields((err, data) => {
          if (err) return;
          setCurrent((prev) => prev + 1);
          setFormValue((prev) => ({
            ...prev,
            ...props.form.getFieldsValue(),
          }));
        });
        break;
      case EnumModifyStep.DIMENSION_STEP:
      case EnumModifyStep.METRIC_STEP:
        const datasource = cref.current.getValue();
        const key =
          current === EnumModifyStep.DIMENSION_STEP
            ? 'dimensionColumns'
            : 'metricColumns';
        setFormValue((formValue) => ({
          ...formValue,
          [key]: datasource,
        }));
        setCurrent((current) => current + 1);
        break;
      case EnumModifyStep.SETTING_STEP:
        break;
    }
  };

  // 恢复对应的form value
  useEffect(() => {
    const formKeys = restoreKeysMap.get(current);
    if (
      current === EnumModifyStep.DIMENSION_STEP ||
      current === EnumModifyStep.METRIC_STEP
    )
      return;
    // 分区设置详情在内层
    const target =
      current === EnumModifyStep.SETTING_STEP
        ? formValue.modelPartition
        : formValue;
    restoreFormValue(formKeys, target);
  }, [current, formValue]);

  useEffect(() => {
    getSchemaList(formValue.dsId);
    getAllDataSourceList();
    getUpdataTypeEnum();
  }, []);

  useEffect(() => {
    if (mode === 'ADD') return;
    getModelDetail(modelId);
  }, [modelId]);

  const currentFormValue = getFieldsValue();

  useEffect(() => {
    getTableList(formValue.dsId, currentFormValue.schema);
  }, [formValue.dsId, currentFormValue.schema]);

  useEffect(() => {
    getSchemaList(formValue.dsId);
  }, [formValue.dsId]);

  const cref = useRef(null);
  const childRef = useRef(null);

  return (
    <Container>
      <div className="dm-model-modify">
        <div className="breadcrumb-area">
          <Breadcrumb>
            <Breadcrumb.Item>
              <a onClick={() => props.history.push('/data-model/list')}>数据模型</a>
            </Breadcrumb.Item>
            <Breadcrumb.Item>
              <a>{breadcrumTitle}</a>
            </Breadcrumb.Item>
          </Breadcrumb>
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
                  formList:
                    current === EnumModifyStep.BASIC_STEP
                      ? basicInfoFormListGenerator(dataSourceList)
                      : relationFormListGenerator({
                          handleClick: () => {
                            setVisibleRelationModal(true);
                          },
                          updateTyleListOptions: updateTypeList,
                          schemaListOptions: schemaList,
                          tableListOptions: tableList,
                          onSchemaChange,
                          visibleUpdateType,
                          joinList: formValue.joinList,
                          onRelationListDelete,
                          onRelationListEdit,
                        }),
                  form: props.form,
                  cref: (ref) => (cref.current = ref),
                  formValue,
                })}
              </Form>
              {visibleRelationModal ? (
                <Modal
                  title="添加关联表"
                  visible={visibleRelationModal}
                  onCancel={() => {
                    setVisibleRelationModal(false);
                    setEditJoinItem(null);
                  }}
                  onOk={() => {
                    childRef.current.validate((err, data) => {
                      if (err) return;
                      // form数据转化
                      const joinItem = joinItemParser(data);
                      const joinList = formValue.joinList || [];
                      joinList.push(joinItem);
                      setFormValue((formValue) => ({
                        ...formValue,
                        joinList,
                      }));
                      setVisibleRelationModal(false);
                    });
                  }}>
                  <RelationTableModal
                    cref={(ref) => (childRef.current = ref)}
                    data={editJoinItem}
                  />
                </Modal>
              ) : null}
            </div>
          </div>
          <footer className="step-footer">
            <div className="button-area">
              {current === EnumModifyStep.BASIC_STEP ? (
                <Button className="margin-right-8 width-80">取消</Button>
              ) : null}
              {current !== EnumModifyStep.BASIC_STEP ? (
                <Button
                  className="margin-right-8 width-80"
                  onClick={() => {
                    setCurrent((prev) => prev - 1);
                  }}>
                  上一步
                </Button>
              ) : null}
              <Button
                className="margin-right-8 width-80"
                type="primary"
                onClick={handleNextStep}>
                下一步
              </Button>
              <Button onClick={() => {}} type="primary">
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
