package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.Component;

/**
 * @author yuebai
 * @date 2020-05-08
 */
public class ComponentDTO extends Component {

    private String hadoopVersion;

    public String getHadoopVersion() {
        return hadoopVersion;
    }

    public void setHadoopVersion(String hadoopVersion) {
        this.hadoopVersion = hadoopVersion;
    }
}
