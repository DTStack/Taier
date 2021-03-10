//每个模块定义的相关接口
export default {
  post1: {
    method: 'post',
    url: '/foo/bar',
  },
  get2: {
    method: 'get',
    url: '/foo/bas',
  },
  //数据源列表分页信息
  dataSourcepage:{
    method: 'post',
    url: '/publicService/v1/dataSource/page',
  },
  //数据源类型下拉列表
  typeList:{
    method: 'get',
    url: '/publicService/v1/dataSource/type/list',
  },
  // 授权产品下拉列表
  productList:{
    method: 'get',
    url: '/publicService/v1/dataSource/product/list',
  },
};
