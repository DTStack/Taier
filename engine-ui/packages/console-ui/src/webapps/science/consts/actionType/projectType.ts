import mc from 'mirror-creator';

export default mc([
    'UPDATE_PROJECT_LIST', // 获取当前用户信息
    'SET_CURRENT_PROJECT' // 设置当前项目列表
], { prefix: 'project/' });
