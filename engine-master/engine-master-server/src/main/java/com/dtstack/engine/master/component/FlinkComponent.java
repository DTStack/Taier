package com.dtstack.engine.master.component;

import java.util.Map;

public class FlinkComponent extends BaseComponent {
    public FlinkComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    @Override
    public void testConnection() throws Exception {

    }

   /* private static final String APP_TYPE = "Apache Flink";
    private static final String DEFAULT_APP_NAME_PREFIX = "Flink session";
    private static final String FLINK_URL_FORMAT = "http://%s/proxy/%s/taskmanagers";
    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";
    private static final long ONE_MEGABYTE = 1024*1024;

    private static List<String> FLINK_KERBEROS_CONF = Arrays.asList(
            "flinkPrincipal",
            "flinkKeytabPath",
            "flinkKrb5ConfPath",
            "zkPrincipal",
            "zkKeytabPath",
            "zkLoginName"
    );

    private List<TaskManagerDescription> taskManagerDescriptions = new ArrayList<>();

    public FlinkComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    public void initTaskManagerResource(YarnClient yarnClient) throws Exception{
        List<ApplicationId> applicationIds = acquireApplicationIds(yarnClient);

        Field rmClientField = yarnClient.getClass().getDeclaredField("rmClient");
        rmClientField.setAccessible(true);
        Object rmClient = rmClientField.get(yarnClient);

        Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
        hField.setAccessible(true);
        //获取指定对象中此字段的值
        Object h = hField.get(rmClient);

        Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
        currentProxyField.setAccessible(true);
        Object currentProxy = currentProxyField.get(h);

        Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
        proxyInfoField.setAccessible(true);
        String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

        String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
        String addr = yarnClient.getConfig().get(key);

        for (ApplicationId applicationId : applicationIds) {
            String url = String.format(FLINK_URL_FORMAT, addr, applicationId.toString());
            String msg = PoolHttpClient.get(url, null);
            if (msg == null) {
                continue;
            }

            JSONObject taskManagerInfo = JSONObject.parseObject(msg);
            if (!taskManagerInfo.containsKey("taskmanagers")){
                continue;
            }

            JSONArray taskManagers = taskManagerInfo.getJSONArray("taskmanagers");
            for (int i = 0; i < taskManagers.size(); i++) {
                JSONObject jsonObject = taskManagers.getJSONObject(i);
                if(jsonObject.containsKey("hardware")){
                    jsonObject.putAll(jsonObject.getJSONObject("hardware"));
                }

                TaskManagerDescription description = TypeUtils.castToJavaBean(jsonObject, TaskManagerDescription.class);
                description.setFreeMemory(description.getFreeMemory() / ONE_MEGABYTE);
                description.setPhysicalMemory(description.getPhysicalMemory() / ONE_MEGABYTE);
                description.setManagedMemory(description.getManagedMemory() / ONE_MEGABYTE);
                taskManagerDescriptions.add(description);
            }
        }
    }

    private List<ApplicationId> acquireApplicationIds(YarnClient yarnClient) {
        try {
            Set<String> set = new HashSet<>();
            set.add(APP_TYPE);
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.RUNNING);
            List<ApplicationReport> reportList = yarnClient.getApplications(set, enumSet);
            List<ApplicationId> applicationIds = new ArrayList<>();
            for (ApplicationReport report : reportList) {
                if (!report.getName().startsWith(DEFAULT_APP_NAME_PREFIX)) {
                    continue;
                }
                applicationIds.add(report.getApplicationId());
            }
            return applicationIds;
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    @Override
    protected List<String> getKerberosKey(){
        return FLINK_KERBEROS_CONF;
    }

    @Override
    public void testConnection() throws Exception {

    }

    public List<TaskManagerDescription> getTaskManagerDescriptions() {
        return taskManagerDescriptions;
    }

    public void setTaskManagerDescriptions(List<TaskManagerDescription> taskManagerDescriptions) {
        this.taskManagerDescriptions = taskManagerDescriptions;
    }
*/
}

class TaskManagerDescription {
    private String path;
    private int dataPort;
    private String id;
    private int freeSlots;
    private int cpuCores;
    private int slotsNumber;
    private long managedMemory;
    private long freeMemory;
    private long physicalMemory;

    public TaskManagerDescription() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getSlotsNumber() {
        return slotsNumber;
    }

    public void setSlotsNumber(int slotsNumber) {
        this.slotsNumber = slotsNumber;
    }

    public long getManagedMemory() {
        return managedMemory;
    }

    public void setManagedMemory(long managedMemory) {
        this.managedMemory = managedMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getPhysicalMemory() {
        return physicalMemory;
    }

    public void setPhysicalMemory(long physicalMemory) {
        this.physicalMemory = physicalMemory;
    }
}
