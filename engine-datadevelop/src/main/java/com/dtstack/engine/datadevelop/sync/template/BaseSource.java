package com.dtstack.batch.sync.template;

import java.util.List;

public abstract class BaseSource extends ExtralConfig{

    protected List<Long> sourceIds;

    public List<Long> getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(List<Long> sourceIds) {
        this.sourceIds = sourceIds;
    }
}
