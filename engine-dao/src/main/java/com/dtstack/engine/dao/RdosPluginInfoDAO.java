package com.dtstack.engine.dao;

import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.callback.MybatisSessionCallback;
import com.dtstack.engine.callback.MybatisSessionCallbackMethod;
import com.dtstack.engine.domain.RdosPluginInfo;
import com.dtstack.engine.mapper.RdosPluginInfoMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * Reason:
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosPluginInfoDAO {

    public Long replaceInto(String pluginInfo, int type){

        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                String pluginKey = MD5Util.getMD5String(pluginInfo);
                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
                RdosPluginInfo rdosPluginInfo = new RdosPluginInfo();
                rdosPluginInfo.setPluginKey(pluginKey);
                rdosPluginInfo.setPluginInfo(pluginInfo);
                rdosPluginInfo.setType(type);
                pluginInfoMapper.replaceInto(rdosPluginInfo);
                return rdosPluginInfo.getId();
            }
        });
    }

    public RdosPluginInfo getByPluginInfo(String pluginInfo){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<RdosPluginInfo>(){

            @Override
            public RdosPluginInfo execute(SqlSession sqlSession) throws Exception {
                String pluginKey = MD5Util.getMD5String(pluginInfo);
                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
                return pluginInfoMapper.getByKey(pluginKey);
            }
        });
    }

    public String getPluginInfo(long id){
        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<String>(){

            @Override
            public String execute(SqlSession sqlSession) throws Exception {
                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
                return pluginInfoMapper.getPluginInfo(id);
            }
        });
    }
}
