package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.Component;
import io.swagger.annotations.ApiModel;

/**
 * @author yuebai
 * @date 2020-05-08
 */
@ApiModel
public class ComponentDTO extends Component {

    private String hadoopVersion;

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }
}
