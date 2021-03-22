import React, { useState, useEffect, useCallback } from 'react';
import Container from 'pages/DataModel/components/Container';
import { Breadcrumb, Steps, Button, Form, Modal } from 'antd';
import './style';
import FormRender from './FormRender';
import RelationTableModal from './RelationListModal';
import { relationFormListGenerator, basicInfoFormListGenerator } from './constants';
import { API } from '@/services';
import Message from 'pages/DataModel/components/Message';

enum EnumModifyStep {
  BASIC_STEP = 0,
  RELATION_TABLE_STEP = 1,
  DIMENSION_STEP = 2,
  METRIC_STRP = 3,
  SETTING_STRP = 4,
}

interface IPropsModify {
  form: any;
}

const formItemLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
}
const { Step } = Steps;

const stepContentRender = (step: EnumModifyStep, props: any) => {
  const { form } = props;
  switch(step) {
    case EnumModifyStep.BASIC_STEP:
      return (
        <FormRender form={form} formList={props.formList || []} />
      );
    case EnumModifyStep.RELATION_TABLE_STEP:
      return (
        <FormRender form={form} formList={props.formList || []} />
      );
    case EnumModifyStep.DIMENSION_STEP:
      return (
        <div>this is dimension....</div>
      );
    case EnumModifyStep.METRIC_STRP:
      return (
        <div>this is metric....</div>
      );
    case EnumModifyStep.SETTING_STRP:
      return (
        <div>hello, this is setting step....</div>
      )
  }
}

const Modify = (props: IPropsModify) => {
  const [current, setCurrent] = useState<EnumModifyStep>(EnumModifyStep.BASIC_STEP);
  const [visibleRelationModal, setVisibleRelationModal] = useState(false);
  const { validateFields, getFieldsValue } = props.form;
  const [formValue, setFormValue] = useState<any>({});
  const [dataSourceList, setdataSourceList] = useState([]);
  const [schemaList, setSchemaList] = useState([]);
  const [tableList, setTableList] = useState([]);
  const [updateTypeList, setUpdateTypeList] = useState([]);


  const getSchemaList = async (datasourceId: number) => {
    if(!datasourceId) return;
    try {
      const { success, data, message } = await API.getDataModelSchemaList({ datasourceId: datasourceId });
      if(success) {
        setSchemaList(data.map(item => ({
          key: item,
          value: item,
          label: item,
        })))
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }

  const getUpdataTypeEnum = async () => {
    try {
      const { success, message, data } = await API.getDataModelUpdateTypeList();
      if(success) {
        setUpdateTypeList(
          data.map(item => ({
            key: item.leftValue,
            value: item.leftValue,
            label: item.rightValue,
          }))
        );
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }

  const getTableList = async (datasourceId: number, schema: string) => {
    if(!datasourceId || !schema) return;
    try {
      const  { success, data, message } = await API.getDataModelTableList({ datasourceId, schema })
      if(success) {
        setTableList(
          data.map(item => ({
            key: item.tableName,
            value: item.tableName,
            label: item.tableName,
            partition: item.partition,
          }))
        )
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }

  // 获取所有可用的数据源列表
  const getAllDataSourceList = useCallback(async () => {
    try {
      const { success, data, message } = await API.getDataModelUsedDataSourceList();
      if(success) {
        const dataSourceList = data.map(item => ({
          key: item.id,
          value: item.id,
          label: `${item.name}(${item.dsTypeName})`,
        }))
        setdataSourceList(dataSourceList);
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }, [])

  const restoreFormValue = (keys: string[]) => {
    props.form.setFieldsValue(keys.reduce((temp, key) => {
      temp[key] = formValue[key]
      return temp;
    }, {}))
  }

  // 恢复对应的form value
  useEffect(() => {
    switch(current) {
      case EnumModifyStep.BASIC_STEP:
        restoreFormValue(['modelName', 'modelEnName', 'dataSource', 'remark']);
        break;
      case EnumModifyStep.RELATION_TABLE_STEP:
        restoreFormValue(['schema', 'table', 'updateType']);
        break;
      default:
        props.form.setFieldsValue({});
    }
  }, [current]);

  useEffect(() => {
    getSchemaList(formValue.dataSource)
  }, [])

  useEffect(() => {
    getAllDataSourceList();
    getUpdataTypeEnum();
  }, []);
  
  const currentFormValue = getFieldsValue();

  useEffect(() => {
    getTableList(formValue.dataSource, currentFormValue.schema);
  }, [formValue.dataSource, currentFormValue.schema]);

  useEffect(() => {
    getSchemaList(formValue.dataSource);
  }, [formValue.dataSource])

  return (
    <Container>
      <div className="dm-model-modify">
        <div className="breadcrumb-area">
          <Breadcrumb>
            <Breadcrumb.Item>
              <a href="/data-model/list">数据模型</a>
            </Breadcrumb.Item>
            <Breadcrumb.Item>
              <a href="">新建模型</a>
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
              <Form layout="horizontal" className="form-area" {...formItemLayout}>
                {
                  stepContentRender(current, {
                    formList: current === EnumModifyStep.BASIC_STEP ? (
                      basicInfoFormListGenerator(dataSourceList)
                    ) : (
                      relationFormListGenerator({
                        handleClick: () => {setVisibleRelationModal(true)},
                        updateTyleListOptions: updateTypeList,
                        schemaListOptions: schemaList,
                        tableListOptions: tableList,
                      })
                    ),
                    form: props.form,
                  })
                }
              </Form>
              <Modal
                title="添加关联表"
                visible={visibleRelationModal}
                onCancel={() => setVisibleRelationModal(false)}
              >
                <RelationTableModal />
              </Modal>
            </div>
          </div>
          <footer className="step-footer">
              <div className="button-area">
                { current === EnumModifyStep.BASIC_STEP ? <Button className="margin-right-8 width-80">取消</Button> : null }
                { current !== EnumModifyStep.BASIC_STEP ? <Button className="margin-right-8 width-80" onClick={() => {
                  setCurrent(prev => prev - 1)
                }}>上一步</Button> : null }
                <Button className="margin-right-8 width-80" type="primary" onClick={() => {
                  validateFields((err, data) => {
                    if(err) {
                      return;
                    }
                    setCurrent(prev => prev + 1);
                    setFormValue(prev => ({
                      ...prev,
                      ...props.form.getFieldsValue(),
                    }))
                  })
                }}>下一步</Button>
                <Button onClick={() => {

                }} type="primary">保存并退出</Button>
              </div>
          </footer>
        </div>
      </div>
    </Container>
  )
}

export default Form.create()(Modify);
