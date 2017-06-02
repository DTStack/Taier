package com.dtstack.rdos.engine.db.dataobject;

import com.dtstack.rdos.engine.db.dataobject.base.ActionLog;

/**
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class RdosStreamActionLog extends ActionLog {

    /**
     * 是否恢复最近一次的点位
     */
    private Byte isRestoration;

    public Byte getIsRestoration() {
        return isRestoration;
    }

    public void setIsRestoration(Byte isRestoration) {
        this.isRestoration = isRestoration;
    }
}
