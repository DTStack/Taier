 const path = require('path');
 const fs = require('fs');
 const {
     packagesPath,
     scriptsPath,
     isDirectory,
     useSpawn,
     logger,
     microAdaptation,
     resetContent
 } = require('./base.js');
 
 const fastBuild = async () => {
     try {
         const files = fs.readdirSync(packagesPath);
         const projects = files.filter((item) => {
             return isDirectory(path.join(packagesPath, item));
         });
 
         /**
          * git synchronizes the latest code
          */
        //  await useSpawn('yarn', ['pull'], true);
 
         /**
          * update project dependencies
          */
         await useSpawn('lerna', ['bootstrap'], true);
 
         /**
          * process webpack to add a common prefix and process iconfont
          */
         microAdaptation(projects);
 
         /**
          * lerna build
          */
         await useSpawn('sh', [`${scriptsPath}/build.sh`], true);
 
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
 
 fastBuild();
 