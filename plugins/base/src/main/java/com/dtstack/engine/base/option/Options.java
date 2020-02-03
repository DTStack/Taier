package com.dtstack.engine.base.option;

public class Options {

    @OptionRequired(description = "jobid")
    private  String jobid = "jobid";

    @OptionRequired(description = "Running mode")
    private  String mode = "local";

    @OptionRequired(required = true,description = "Job name")
    private  String name;

    @OptionRequired(required = true,description = "Job sql file")
    private  String sql;

    @OptionRequired(description = "Flink configuration directory")
    private  String flinkconf;

    @OptionRequired(description = "Yarn and Hadoop configuration directory")
    private  String yarnconf;

    @OptionRequired(required = true,description = "Sql local plugin root")
    private  String localSqlPluginPath;

    @OptionRequired(required = true,description = "Sql remote plugin root")
    private  String remoteSqlPluginPath ;

    @OptionRequired(description = "sql ext jar,eg udf jar")
    private  String addjar;

    @OptionRequired(description = "sql ref prop,eg specify event time")
    private  String confProp = "{}";

    @OptionRequired(description = "Savepoint restore path")
    private  String savePointPath;

    @OptionRequired(description = "Flag indicating whether non restored state is allowed if the savepoint")
    private  String allowNonRestoredState = "false";

    @OptionRequired(description = "flink jar path for submit of perjob mode")
    private String flinkJarPath;

    @OptionRequired(description = "yarn queue")
    private String queue = "default";

    @OptionRequired(description = "yarn session configuration,such as yid")
    private String yarnSessionConf = "{}";

    @OptionRequired(description = "plugin load mode, by classpath or shipfile")
    private String pluginLoadMode = "CLASSPATH";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFlinkconf() {
        return flinkconf;
    }

    public void setFlinkconf(String flinkconf) {
        this.flinkconf = flinkconf;
    }

    public String getYarnconf() {
        return yarnconf;
    }

    public void setYarnconf(String yarnconf) {
        this.yarnconf = yarnconf;
    }

    public String getLocalSqlPluginPath() {
        return localSqlPluginPath;
    }

    public void setLocalSqlPluginPath(String localSqlPluginPath) {
        this.localSqlPluginPath = localSqlPluginPath;
    }

    public String getRemoteSqlPluginPath() {
        return remoteSqlPluginPath;
    }

    public void setRemoteSqlPluginPath(String remoteSqlPluginPath) {
        this.remoteSqlPluginPath = remoteSqlPluginPath;
    }

    public String getAddjar() {
        return addjar;
    }

    public void setAddjar(String addjar) {
        this.addjar = addjar;
    }

    public String getConfProp() {
        return confProp;
    }

    public void setConfProp(String confProp) {
        this.confProp = confProp;
    }

    public String getSavePointPath() {
        return savePointPath;
    }

    public void setSavePointPath(String savePointPath) {
        this.savePointPath = savePointPath;
    }

    public String getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public void setAllowNonRestoredState(String allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
    }

    public String getFlinkJarPath() {
        return flinkJarPath;
    }

    public void setFlinkJarPath(String flinkJarPath) {
        this.flinkJarPath = flinkJarPath;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getYarnSessionConf() {
        return yarnSessionConf;
    }

    public void setYarnSessionConf(String yarnSessionConf) {
        this.yarnSessionConf = yarnSessionConf;
    }

    public String getPluginLoadMode() {
        return pluginLoadMode;
    }

    public void setPluginLoadMode(String pluginLoadMode) {
        this.pluginLoadMode = pluginLoadMode;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }
}
