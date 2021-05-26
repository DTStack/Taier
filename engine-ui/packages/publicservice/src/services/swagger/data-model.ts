const prefix = '/api/publicService/model';
// 数据模型
export default {
  // 获取模型列表
  getModelList: {
    method: 'post',
    url: `${prefix}/list`,
  },
  // 获取模型详情
  getModelDetail: {
    method: 'get',
    url: `${prefix}/detail`,
  },
  // TODO: 修改接口参数，临时mock
  // 判断模型是否被下游引用
  isModelReferenced: {
    method: 'post',
    url: `${prefix}/checkRef`,
  },
  // 删除模型
  deleteModel: {
    method: 'post',
    url: `${prefix}/delete`,
  },
  // 发布模型
  releaseModel: {
    method: 'post',
    url: `${prefix}/release`,
  },
  // 模型下线
  unreleaseModel: {
    method: 'post',
    url: `${prefix}/offline`,
  },
  // 获取所有可用数据园
  getAllDataSourceList: {
    method: 'get',
    url: `${prefix}/data/sources`,
  },
  // 获取数据源类型，用于列表筛选
  getDataSourceTypeList: {
    method: 'get',
    url: `${prefix}/data/sourceType`,
  },
  // 获取更新方式枚举值
  getDataModelUpdateTypeList: {
    method: 'get',
    url: `${prefix}/data/updateType`,
  },
  // 根据数据源获取schema下拉列表
  getDataModelSchemaList: {
    method: 'get',
    url: `${prefix}/data/schemas`,
  },
  getDataModelTableList: {
    method: 'get',
    url: `${prefix}/data/tables`,
  },
  // SQL预览
  previewSql: {
    method: 'post',
    url: `${prefix}/sqlPreview`,
  },
  getDataModelColumns: {
    method: 'post',
    url: `${prefix}/data/columns`,
  },
  repeatValidate: {
    method: 'post',
    url: `/api/publicService/common/name/repeat`,
  },
  isPartition: {
    method: 'post',
    url: `${prefix}/data/isPart`,
  },
  saveDataModel: {
    method: 'post',
    url: `${prefix}/save`,
  },
};
