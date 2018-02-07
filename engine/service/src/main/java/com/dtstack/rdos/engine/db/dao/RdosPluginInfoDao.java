package com.dtstack.rdos.engine.db.dao;

import com.dtstack.rdos.common.util.MD5Util;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallback;
import com.dtstack.rdos.engine.db.callback.MybatisSessionCallbackMethod;
import com.dtstack.rdos.engine.db.dataobject.RdosPluginInfo;
import com.dtstack.rdos.engine.db.mapper.RdosPluginInfoMapper;
import com.dtstack.rdos.engine.db.mapper.RdosStreamTaskCheckpointMapper;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;

/**
 * Reason:
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosPluginInfoDao {

    public Long replaceInto(String pluginInfo, int type){

        return MybatisSessionCallbackMethod.doCallback(new MybatisSessionCallback<Long>(){

            @Override
            public Long execute(SqlSession sqlSession) throws Exception {
                String pluginKey = MD5Util.getMD5String(pluginInfo);
                RdosPluginInfoMapper pluginInfoMapper = sqlSession.getMapper(RdosPluginInfoMapper.class);
                return pluginInfoMapper.replaceInto(pluginKey, pluginInfo, type);
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
