import { useEffect } from 'react';
import molecule from '@dtinsight/molecule';
import { EventBus } from '@dtinsight/molecule/esm/common/event';
import { connect } from '@dtinsight/molecule/esm/react';

import { ID_COLLECTIONS } from '@/constant';

/**
 * For get the latest current value after current changed and notice the non-jsx component
 */
export default connect(molecule.editor, ({ current }: molecule.model.IEditor) => {
    useEffect(() => {
        EventBus.emit(ID_COLLECTIONS.TASK_SWITCH_EVENT);
    }, [current?.id, current?.activeTab]);
    return <></>;
});
