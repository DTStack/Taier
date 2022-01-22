 const path = require('path');
 const {
     scriptsPath,
     useSpawn,
     logger,
 } = require('./base.js');
 
 const fastProvision = async () => {
     try {
         /**
          * pm2 control
          */
         await useSpawn(
             'pm2',
             [`start`, `${path.join(scriptsPath, './start.sh')}`],
             true
         );
     } catch (error) {
         return logger(error);
     }
 };
 
 fastProvision();
 