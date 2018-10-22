import database from '../api/database';
import datamap from '../api/database';
import user from '../api/database';
import table from '../api/table';

const Apis = Object.assign(
    user,
    datamap,
    database,
    table,
)

export default Apis;
