/**
 * Note: Before starting,
 * you need to global install pm2 and make sure that the node version is greater than v12.18.0
 */

 const path = require('path');
 const fs = require('fs');
 const {
    resetContent,
    isDirectory,
    microAdaptation,
    logger,
    automata,
    packagesPath,
    useSpawn
} = require('./base.js');
 
 const fastStart = async () => {
     try {
         const files = fs.readdirSync(packagesPath);
         const projects = files.filter((item) => {
             return isDirectory(path.join(packagesPath, item));
         });

         /**
          * update project dependencies
          */
          await useSpawn('lerna', ['bootstrap'], true);

         /**
          * process webpack to add a common prefix and process iconfont
          */
         microAdaptation(projects);
 
         /**
          * lerna run start
          */
         await automata(projects);
 
         /**
          * restore the scene
          */
         resetContent();
     } catch (error) {
         /**
          * restore the file contents and interrupt the process
          */
         resetContent();
         return logger(error);
     }
 };
 
 fastStart();
 