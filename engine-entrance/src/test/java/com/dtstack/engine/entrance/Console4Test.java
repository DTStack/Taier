package com.dtstack.engine.entrance;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ComponentDTO;
import com.dtstack.engine.api.vo.TemplateVo;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-08
 */
public class Console4Test extends BaseTest {


    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Test
    @Transactional
    @Rollback
    public void TestCreateCluster() {
        String name = "createClusterTest";
        ClusterDTO clusterDTO = new ClusterDTO();
        clusterDTO.setClusterName(name);
        clusterService.addCluster(clusterDTO);
        Assert.notNull(clusterService.getClusterByName(name));
    }


    @Test
    @Transactional
    @Rollback
    public void TestDeleteCluster() {
        clusterService.deleteCluster(19l);
    }

    @Test
    @Transactional
    @Rollback
    public void TestCreateComponent(){
        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentName("SFTP");
        componentDTO.setComponentConfig("{\"path\":\"/data/sftp\",\"password\":\"abc123\",\"port\":\"22\",\"auth\":\"1\",\"host\":\"172.16.100.216\",\"username\":\"root\"}");
        componentDTO.setHadoopVersion("hadoop2");
        componentDTO.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        System.out.println(JSONObject.toJSONString(componentDTO));
        componentService.addOrUpdateComponent(-1L,"testCompoennt",componentDTO.getComponentConfig(),null,null,null
        ,"",10);
    }


    @Test
    @Transactional
    @Rollback
    public void testLoadTemplate() {
        List<TemplateVo> templateVos = componentService.loadTemplate(EComponentType.SFTP.getTypeCode());
        Assert.notEmpty(templateVos);
    }
}
