import database from '../api/database';
import datamap from '../api/database';
import user from '../api/database';

const Apis = Object.assign(
    user,
    datamap,
    database,
)

export default Apis;