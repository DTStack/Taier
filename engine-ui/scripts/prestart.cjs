const path = require("path");
const fs = require("fs");
const {
  resetContent,
  isDirectory,
  microAdaptation,
  logger,
  packagesPath,
} = require("./base.js");

const fastStart = async () => {
  try {
    const files = fs.readdirSync(packagesPath);
    const projects = files.filter((item) => {
      return isDirectory(path.join(packagesPath, item));
    });

    /**
     * process webpack to add a common prefix and process iconfont
     */
    microAdaptation(projects);

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
