// 数据模型
export default {
  // 获取模型列表
  getModelList: {
    method: 'post',
    url: '/publicService/v1/model/list',
  },
  // 获取模型详情
  getModelDetail: {
    method: 'get',
    url: '/publicService/v1/model/detail',
  },
  // 删除模型
  deleteModel: {
    method: 'post',
    url: '/publicService/v1/model/delete',
  },
  // 发布模型
  releaseModel: {
    method: 'post',
    url: '/publicService/v1/model/release',
  },
  // 模型下线
  unreleaseModel: {
    method: 'post',
    url: '/publicService/v1/model/offline',
  },
};
