package com.dtstack.engine.master.send;

import com.dtstack.engine.common.RootUrls;

public interface Urls extends RootUrls {

    String MASTER_TRIGGER_NODE = String.format("%s/%s", NODE_RECOVER, "masterTriggerNode");

}
