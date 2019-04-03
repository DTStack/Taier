import mc from 'mirror-creator';

export default mc([
    'UPDATE_PROJECT_LIST', // 获取当前用户信息
    'SET_CURRENT_PROJECT' // 非项目用户
], { prefix: 'project/' });
