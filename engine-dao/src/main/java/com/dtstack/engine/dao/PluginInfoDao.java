package com.dtstack.engine.dao;

import com.dtstack.engine.domain.PluginInfo;
import org.apache.ibatis.annotations.Param;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/02/12
 */
public interface PluginInfoDao {

    Integer replaceInto(PluginInfo pluginInfo);

    PluginInfo getByKey(@Param("pluginKey") String pluginKey);

    String getPluginInfo(@Param("id") Long id);

//    public Long replaceInto(String pluginInfo, int type){
//
//        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){
//
//            @Override
//            public Long execute(SqlSession sqlSession) throws Exception {
//                String pluginKey = MD5Util.getMD5String(pluginInfo);
//                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
//                PluginInfo pluginInfo = new PluginInfo();
//                pluginInfo.setPluginKey(pluginKey);
//                pluginInfo.setPluginInfo(pluginInfo);
//                pluginInfo.setType(type);
//                pluginInfoMapper.replaceInto(pluginInfo);
//                return pluginInfo.getId();
//            }
//        });
//    }
//
//    public PluginInfo getByPluginInfo(String pluginInfo){
//        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<PluginInfo>(){
//
//            @Override
//            public PluginInfo execute(SqlSession sqlSession) throws Exception {
//                String pluginKey = MD5Util.getMD5String(pluginInfo);
//                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
//                return pluginInfoMapper.getByKey(pluginKey);
//            }
//        });
//    }
//
//    public String getPluginInfo(long id){
//        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<String>(){
//
//            @Override
//            public String execute(SqlSession sqlSession) throws Exception {
//                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
//                return pluginInfoMapper.getPluginInfo(id);
//            }
//        });
//    }
}
