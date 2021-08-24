//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.apache.hadoop.mapred;

import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.mapred.TaskLog.LogName;
import org.apache.hadoop.mapreduce.ClusterMetrics;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.QueueAclsInfo;
import org.apache.hadoop.mapreduce.QueueInfo;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.TaskReport;
import org.apache.hadoop.mapreduce.TaskTrackerInfo;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.Cluster.JobTrackerStatus;
import org.apache.hadoop.mapreduce.JobStatus.State;
import org.apache.hadoop.mapreduce.protocol.ClientProtocol;
import org.apache.hadoop.mapreduce.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.mapreduce.v2.LogParams;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDelegationTokenRequest;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.URL;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.security.client.RMDelegationTokenSelector;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.UnitsConversionUtil;
import org.apache.hadoop.yarn.util.resource.ResourceUtils;

public class YARNRunner implements ClientProtocol {
    private static final Log LOG = LogFactory.getLog(YARNRunner.class);
    private static final String RACK_GROUP = "rack";
    private static final String NODE_IF_RACK_GROUP = "node1";
    private static final String NODE_IF_NO_RACK_GROUP = "node2";
    private static final Pattern RACK_NODE_PATTERN = Pattern.compile(String.format("(?<%s>[^/]+?)|(?<%s>/[^/]+?)(?:/(?<%s>[^/]+?))?", new Object[]{"node2", "rack", "node1"}));
    private static final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory((Configuration)null);
    public static final Priority AM_CONTAINER_PRIORITY;
    private ResourceMgrDelegate resMgrDelegate;
    private ClientCache clientCache;
    private Configuration conf;
    private final FileContext defaultFileContext;

    public YARNRunner(Configuration conf) {
        this(conf, new ResourceMgrDelegate(new YarnConfiguration(conf)));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate) {
        this(conf, resMgrDelegate, new ClientCache(conf, resMgrDelegate));
    }

    public YARNRunner(Configuration conf, ResourceMgrDelegate resMgrDelegate, ClientCache clientCache) {
        this.conf = conf;

        try {
            this.resMgrDelegate = resMgrDelegate;
            this.clientCache = clientCache;
            this.defaultFileContext = FileContext.getFileContext(this.conf);
        } catch (UnsupportedFileSystemException var5) {
            throw new RuntimeException("Error in instantiating YarnClient", var5);
        }
    }

    @Private
    public void setResourceMgrDelegate(ResourceMgrDelegate resMgrDelegate) {
        this.resMgrDelegate = resMgrDelegate;
    }

    public void cancelDelegationToken(Token<DelegationTokenIdentifier> arg0) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public TaskTrackerInfo[] getActiveTrackers() throws IOException, InterruptedException {
        return this.resMgrDelegate.getActiveTrackers();
    }

    public JobStatus[] getAllJobs() throws IOException, InterruptedException {
        return this.resMgrDelegate.getAllJobs();
    }

    public TaskTrackerInfo[] getBlacklistedTrackers() throws IOException, InterruptedException {
        return this.resMgrDelegate.getBlacklistedTrackers();
    }

    public ClusterMetrics getClusterMetrics() throws IOException, InterruptedException {
        return this.resMgrDelegate.getClusterMetrics();
    }

    @VisibleForTesting
    void addHistoryToken(Credentials ts) throws IOException, InterruptedException {
        MRClientProtocol hsProxy = this.clientCache.getInitializedHSProxy();
        if(UserGroupInformation.isSecurityEnabled() && hsProxy != null) {
            RMDelegationTokenSelector tokenSelector = new RMDelegationTokenSelector();
            Text service = this.resMgrDelegate.getRMDelegationTokenService();
            if(tokenSelector.selectToken(service, ts.getAllTokens()) != null) {
                Text hsService = SecurityUtil.buildTokenService(hsProxy.getConnectAddress());
                if(ts.getToken(hsService) == null) {
                    ts.addToken(hsService, this.getDelegationTokenFromHS(hsProxy));
                }
            }
        }

    }

    @VisibleForTesting
    Token<?> getDelegationTokenFromHS(MRClientProtocol hsProxy) throws IOException, InterruptedException {
        GetDelegationTokenRequest request = (GetDelegationTokenRequest)recordFactory.newRecordInstance(GetDelegationTokenRequest.class);
        request.setRenewer(Master.getMasterPrincipal(this.conf));
        org.apache.hadoop.yarn.api.records.Token mrDelegationToken = hsProxy.getDelegationToken(request).getDelegationToken();
        return ConverterUtils.convertFromYarn(mrDelegationToken, hsProxy.getConnectAddress());
    }

    public Token<DelegationTokenIdentifier> getDelegationToken(Text renewer) throws IOException, InterruptedException {
        return this.resMgrDelegate.getDelegationToken(renewer);
    }

    public String getFilesystemName() throws IOException, InterruptedException {
        return this.resMgrDelegate.getFilesystemName();
    }

    public JobID getNewJobID() throws IOException, InterruptedException {
        return this.resMgrDelegate.getNewJobID();
    }

    public QueueInfo getQueue(String queueName) throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueue(queueName);
    }

    public QueueAclsInfo[] getQueueAclsForCurrentUser() throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueueAclsForCurrentUser();
    }

    public QueueInfo[] getQueues() throws IOException, InterruptedException {
        return this.resMgrDelegate.getQueues();
    }

    public QueueInfo[] getRootQueues() throws IOException, InterruptedException {
        return this.resMgrDelegate.getRootQueues();
    }

    public QueueInfo[] getChildQueues(String parent) throws IOException, InterruptedException {
        return this.resMgrDelegate.getChildQueues(parent);
    }

    public String getStagingAreaDir() throws IOException, InterruptedException {
        return this.resMgrDelegate.getStagingAreaDir();
    }

    public String getSystemDir() throws IOException, InterruptedException {
        return this.resMgrDelegate.getSystemDir();
    }

    public long getTaskTrackerExpiryInterval() throws IOException, InterruptedException {
        return this.resMgrDelegate.getTaskTrackerExpiryInterval();
    }

    public JobStatus submitJob(JobID jobId, String jobSubmitDir, Credentials ts) throws IOException, InterruptedException {
        this.addHistoryToken(ts);
        ApplicationSubmissionContext appContext = this.createApplicationSubmissionContext(this.conf, jobSubmitDir, ts);

        try {
            ApplicationId applicationId = this.resMgrDelegate.submitApplication(appContext);
            ApplicationReport appMaster = this.resMgrDelegate.getApplicationReport(applicationId);
            String diagnostics = appMaster == null?"application report is null":appMaster.getDiagnostics();
            if(appMaster != null && appMaster.getYarnApplicationState() != YarnApplicationState.FAILED && appMaster.getYarnApplicationState() != YarnApplicationState.KILLED) {
                return this.clientCache.getClient(jobId).getJobStatus(jobId);
            } else {
                throw new IOException("Failed to run job : " + diagnostics);
            }
        } catch (YarnException var8) {
            throw new IOException(var8);
        }
    }

    private LocalResource createApplicationResource(FileSystem fs, Path p, LocalResourceType type) throws IOException {
        return this.createApplicationResource(fs, p, (String)null, type, LocalResourceVisibility.APPLICATION, Boolean.valueOf(false));
    }

    private LocalResource createApplicationResource(FileSystem fs, Path p, String fileSymlink, LocalResourceType type, LocalResourceVisibility viz, Boolean uploadToSharedCache) throws IOException {
        LocalResource rsrc = (LocalResource)recordFactory.newRecordInstance(LocalResource.class);
        FileStatus rsrcStat = fs.getFileStatus(p);
        Path qualifiedPath = fs.resolvePath(rsrcStat.getPath());
        URI uriWithFragment = null;
        boolean useFragment = fileSymlink != null && !fileSymlink.equals("");

        try {
            if(useFragment) {
                uriWithFragment = new URI(qualifiedPath.toUri() + "#" + fileSymlink);
            } else {
                uriWithFragment = qualifiedPath.toUri();
            }
        } catch (URISyntaxException var13) {
            throw new IOException("Error parsing local resource path. Path was not able to be converted to a URI: " + qualifiedPath, var13);
        }

        rsrc.setResource(URL.fromURI(uriWithFragment));
        rsrc.setSize(rsrcStat.getLen());
        rsrc.setTimestamp(rsrcStat.getModificationTime());
        rsrc.setType(type);
        rsrc.setVisibility(viz);
        rsrc.setShouldBeUploadedToSharedCache(uploadToSharedCache.booleanValue());
        return rsrc;
    }

    private Map<String, LocalResource> setupLocalResources(Configuration jobConf, String jobSubmitDir) throws IOException {
        Map<String, LocalResource> localResources = new HashMap();
        Path jobConfPath = new Path(jobSubmitDir, "job.xml");
        URL yarnUrlForJobSubmitDir = URL.fromPath(FileSystem.get(jobConf).resolvePath(FileSystem.get(jobConf).makeQualified(new Path(jobSubmitDir))));
        LOG.debug("Creating setup context, jobSubmitDir url is " + yarnUrlForJobSubmitDir);
        localResources.put("job.xml", this.createApplicationResource(FileSystem.get(jobConf), jobConfPath, LocalResourceType.FILE));
        if(jobConf.get("mapreduce.job.jar") != null) {
            Path jobJarPath = new Path(jobConf.get("mapreduce.job.jar"));
            FileContext fccc = FileContext.getFileContext(jobJarPath.toUri(), jobConf);
            LocalResourceVisibility jobJarViz = jobConf.getBoolean("mapreduce.job.jobjar.visibility", false)?LocalResourceVisibility.PUBLIC:LocalResourceVisibility.APPLICATION;
            LocalResource rc = this.createApplicationResource(FileSystem.get(jobConf), jobJarPath, "job.jar", LocalResourceType.PATTERN, jobJarViz, Boolean.valueOf(jobConf.getBoolean("mapreduce.job.jobjar.sharedcache.uploadpolicy", false)));
            String pattern = this.conf.getPattern("mapreduce.job.jar.unpack.pattern", JobConf.UNPACK_JAR_PATTERN_DEFAULT).pattern();
            rc.setPattern(pattern);
            localResources.put("job.jar", rc);
        } else {
            LOG.info("Job jar is not present. Not adding any jar to the list of resources.");
        }

        String[] var11 = new String[]{"job.split", "job.splitmetainfo"};
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            String s = var11[var13];
            localResources.put("jobSubmitDir/" + s, this.createApplicationResource(FileSystem.get(jobConf), new Path(jobSubmitDir, s), LocalResourceType.FILE));
        }

        return localResources;
    }

    private List<String> setupAMCommand(Configuration jobConf) {
        List<String> vargs = new ArrayList(8);
        vargs.add(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME) + "/bin/java");
        Path amTmpDir = new Path(MRApps.crossPlatformifyMREnv(this.conf, Environment.PWD), "./tmp");
        vargs.add("-Djava.io.tmpdir=" + amTmpDir);
        MRApps.addLog4jSystemProperties((Task)null, vargs, this.conf);
        warnForJavaLibPath(this.conf.get("mapreduce.map.java.opts", ""), "map", "mapreduce.map.java.opts", "mapreduce.map.env");
        warnForJavaLibPath(this.conf.get("mapreduce.admin.map.child.java.opts", ""), "map", "mapreduce.admin.map.child.java.opts", "mapreduce.admin.user.env");
        warnForJavaLibPath(this.conf.get("mapreduce.reduce.java.opts", ""), "reduce", "mapreduce.reduce.java.opts", "mapreduce.reduce.env");
        warnForJavaLibPath(this.conf.get("mapreduce.admin.reduce.child.java.opts", ""), "reduce", "mapreduce.admin.reduce.child.java.opts", "mapreduce.admin.user.env");
        String mrAppMasterAdminOptions = this.conf.get("yarn.app.mapreduce.am.admin-command-opts", "");
        warnForJavaLibPath(mrAppMasterAdminOptions, "app master", "yarn.app.mapreduce.am.admin-command-opts", "yarn.app.mapreduce.am.admin.user.env");
        vargs.add(mrAppMasterAdminOptions);
        String mrAppMasterUserOptions = this.conf.get("yarn.app.mapreduce.am.command-opts", "-Xmx1024m");
        warnForJavaLibPath(mrAppMasterUserOptions, "app master", "yarn.app.mapreduce.am.command-opts", "yarn.app.mapreduce.am.env");
        vargs.add(mrAppMasterUserOptions);
        if(jobConf.getBoolean("yarn.app.mapreduce.am.profile", false)) {
            String profileParams = jobConf.get("yarn.app.mapreduce.am.profile.params", "-agentlib:hprof=cpu=samples,heap=sites,force=n,thread=y,verbose=n,file=%s");
            if(profileParams != null) {
                vargs.add(String.format(profileParams, new Object[]{"<LOG_DIR>/" + LogName.PROFILE}));
            }
        }

        vargs.add("org.apache.hadoop.mapreduce.v2.app.MRAppMaster");
        vargs.add("1><LOG_DIR>/stdout");
        vargs.add("2><LOG_DIR>/stderr");
        return vargs;
    }

    private ContainerLaunchContext setupContainerLaunchContextForAM(Configuration jobConf, Map<String, LocalResource> localResources, ByteBuffer securityTokens, List<String> vargs) throws IOException {
        Vector<String> vargsFinal = new Vector(8);
        StringBuilder mergedCommand = new StringBuilder();
        Iterator var7 = vargs.iterator();

        while(var7.hasNext()) {
            CharSequence str = (CharSequence)var7.next();
            mergedCommand.append(str).append(" ");
        }

        vargsFinal.add(mergedCommand.toString());
        LOG.debug("Command to launch container for ApplicationMaster is : " + mergedCommand);
        Map<String, String> environment = new HashMap();
        MRApps.setClasspath(environment, this.conf);
        environment.put(Environment.SHELL.name(), this.conf.get("mapreduce.admin.user.shell", "/bin/bash"));
        MRApps.addToEnvironment(environment, Environment.LD_LIBRARY_PATH.name(), MRApps.crossPlatformifyMREnv(this.conf, Environment.PWD), this.conf);
        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.admin.user.env", MRJobConfig.DEFAULT_MR_AM_ADMIN_USER_ENV), this.conf);
        MRApps.setEnvFromInputString(environment, this.conf.get("yarn.app.mapreduce.am.env"), this.conf);
        MRApps.setupDistributedCache(jobConf, localResources);
        Map<ApplicationAccessType, String> acls = new HashMap(2);
        acls.put(ApplicationAccessType.VIEW_APP, jobConf.get("mapreduce.job.acl-view-job", " "));
        acls.put(ApplicationAccessType.MODIFY_APP, jobConf.get("mapreduce.job.acl-modify-job", " "));
        return ContainerLaunchContext.newInstance(localResources, environment, vargsFinal, (Map)null, securityTokens, acls);
    }

    public ApplicationSubmissionContext createApplicationSubmissionContext(Configuration jobConf, String jobSubmitDir, Credentials ts) throws IOException {
        ApplicationId applicationId = this.resMgrDelegate.getApplicationId();
        Map<String, LocalResource> localResources = this.setupLocalResources(jobConf, jobSubmitDir);
        DataOutputBuffer dob = new DataOutputBuffer();
        ts.writeTokenStorageToStream(dob);
        ByteBuffer securityTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        List<String> vargs = this.setupAMCommand(jobConf);
        ContainerLaunchContext amContainer = this.setupContainerLaunchContextForAM(jobConf, localResources, securityTokens, vargs);
        String regex = this.conf.get("mapreduce.job.send-token-conf");
        if(regex != null && !regex.isEmpty()) {
            this.setTokenRenewerConf(amContainer, this.conf, regex);
        }

        Collection<String> tagsFromConf = jobConf.getTrimmedStringCollection("mapreduce.job.tags");
        ApplicationSubmissionContext appContext = (ApplicationSubmissionContext)recordFactory.newRecordInstance(ApplicationSubmissionContext.class);
        appContext.setApplicationId(applicationId);
        appContext.setQueue(jobConf.get("mapreduce.job.queuename", "default"));
        ReservationId reservationID = null;

        String amNodelabelExpression;
        try {
            reservationID = ReservationId.parseReservationId(jobConf.get("mapreduce.job.reservation.id"));
        } catch (NumberFormatException var20) {
            amNodelabelExpression = "Invalid reservationId: " + jobConf.get("mapreduce.job.reservation.id") + " specified for the app: " + applicationId;
            LOG.warn(amNodelabelExpression);
            throw new IOException(amNodelabelExpression);
        }

        if(reservationID != null) {
            appContext.setReservationID(reservationID);
            LOG.info("SUBMITTING ApplicationSubmissionContext app:" + applicationId + " to queue:" + appContext.getQueue() + " with reservationId:" + appContext.getReservationID());
        }

        appContext.setApplicationName(jobConf.get("mapreduce.job.name", "N/A"));
        appContext.setCancelTokensWhenComplete(this.conf.getBoolean("mapreduce.job.complete.cancel.delegation.tokens", true));
        appContext.setAMContainerSpec(amContainer);
        appContext.setMaxAppAttempts(this.conf.getInt("mapreduce.am.max-attempts", 2));
        List<ResourceRequest> amResourceRequests = this.generateResourceRequests();
        appContext.setAMContainerResourceRequests(amResourceRequests);
        amNodelabelExpression = this.conf.get("mapreduce.job.am.node-label-expression");
        if(null != amNodelabelExpression && amNodelabelExpression.trim().length() != 0) {
            Iterator var16 = amResourceRequests.iterator();

            while(var16.hasNext()) {
                ResourceRequest amResourceRequest = (ResourceRequest)var16.next();
                amResourceRequest.setNodeLabelExpression(amNodelabelExpression.trim());
            }
        }

        appContext.setNodeLabelExpression(jobConf.get("mapreduce.job.node-label-expression"));
        appContext.setApplicationType("MAPREDUCE");
        if(tagsFromConf != null && !tagsFromConf.isEmpty()) {
            appContext.setApplicationTags(new HashSet(tagsFromConf));
        }

        String jobPriority = jobConf.get("mapreduce.job.priority");
        if(jobPriority != null) {
            int iPriority;
            try {
                iPriority = TypeConverter.toYarnApplicationPriority(jobPriority);
            } catch (IllegalArgumentException var19) {
                iPriority = Integer.parseInt(jobPriority);
            }

            appContext.setPriority(Priority.newInstance(iPriority));
        }

        return appContext;
    }

    private List<ResourceRequest> generateResourceRequests() throws IOException {
        Resource capability = (Resource)recordFactory.newRecordInstance(Resource.class);
        boolean memorySet = false;
        boolean cpuVcoresSet = false;
        List<ResourceInformation> resourceRequests = ResourceUtils.getRequestedResourcesFromConfig(this.conf, "yarn.app.mapreduce.am.resource.");
        Iterator var5 = resourceRequests.iterator();

        while(true) {
            while(var5.hasNext()) {
                ResourceInformation resourceReq = (ResourceInformation)var5.next();
                String resourceName = resourceReq.getName();
                if(!"memory".equals(resourceName) && !"memory-mb".equals(resourceName)) {
                    if("vcores".equals(resourceName)) {
                        capability.setVirtualCores((int)UnitsConversionUtil.convert(resourceReq.getUnits(), "", resourceReq.getValue()));
                        cpuVcoresSet = true;
                        if(this.conf.get("yarn.app.mapreduce.am.resource.cpu-vcores") != null) {
                            LOG.warn("Configuration yarn.app.mapreduce.am.resource." + resourceName + "=" + resourceReq.getValue() + resourceReq.getUnits() + " is overriding the " + "yarn.app.mapreduce.am.resource.cpu-vcores" + "=" + this.conf.get("yarn.app.mapreduce.am.resource.cpu-vcores") + " configuration");
                        }
                    } else if(!"yarn.app.mapreduce.am.resource.mb".equals("yarn.app.mapreduce.am.resource." + resourceName) && !"yarn.app.mapreduce.am.resource.cpu-vcores".equals("yarn.app.mapreduce.am.resource." + resourceName)) {
                        ResourceInformation resourceInformation = capability.getResourceInformation(resourceName);
                        resourceInformation.setUnits(resourceReq.getUnits());
                        resourceInformation.setValue(resourceReq.getValue());
                        capability.setResourceInformation(resourceName, resourceInformation);
                    }
                } else {
                    if(memorySet) {
                        throw new IllegalArgumentException("Only one of the following keys can be specified for a single job: memory-mb, memory");
                    }

                    String units = StringUtils.isEmpty(resourceReq.getUnits())?ResourceUtils.getDefaultUnit("memory-mb"):resourceReq.getUnits();
                    capability.setMemorySize(UnitsConversionUtil.convert(units, "Mi", resourceReq.getValue()));
                    memorySet = true;
                    if(this.conf.get("yarn.app.mapreduce.am.resource.mb") != null) {
                        LOG.warn("Configuration yarn.app.mapreduce.am.resource." + resourceName + "=" + resourceReq.getValue() + resourceReq.getUnits() + " is overriding the " + "yarn.app.mapreduce.am.resource.mb" + "=" + this.conf.get("yarn.app.mapreduce.am.resource.mb") + " configuration");
                    }
                }
            }

            if(!memorySet) {
                capability.setMemorySize((long)this.conf.getInt("yarn.app.mapreduce.am.resource.mb", 1536));
            }

            if(!cpuVcoresSet) {
                capability.setVirtualCores(this.conf.getInt("yarn.app.mapreduce.am.resource.cpu-vcores", 1));
            }

            if(LOG.isDebugEnabled()) {
                LOG.debug("AppMaster capability = " + capability);
            }

            List<ResourceRequest> amResourceRequests = new ArrayList();
            ResourceRequest amAnyResourceRequest = this.createAMResourceRequest("*", capability);
            Map<String, ResourceRequest> rackRequests = new HashMap();
            amResourceRequests.add(amAnyResourceRequest);
            Collection<String> amStrictResources = this.conf.getStringCollection("mapreduce.job.am.strict-locality");
            Iterator var9 = amStrictResources.iterator();

            while(var9.hasNext()) {
                String amStrictResource = (String)var9.next();
                amAnyResourceRequest.setRelaxLocality(false);
                Matcher matcher = RACK_NODE_PATTERN.matcher(amStrictResource);
                String nodeName;
                if(!matcher.matches()) {
                    nodeName = "Invalid resource name: " + amStrictResource + " specified.";
                    LOG.warn(nodeName);
                    throw new IOException(nodeName);
                }

                String rackName = matcher.group("rack");
                if(rackName == null) {
                    rackName = "/default-rack";
                    nodeName = matcher.group("node2");
                } else {
                    nodeName = matcher.group("node1");
                }

                ResourceRequest amRackResourceRequest = (ResourceRequest)rackRequests.get(rackName);
                if(amRackResourceRequest == null) {
                    amRackResourceRequest = this.createAMResourceRequest(rackName, capability);
                    amResourceRequests.add(amRackResourceRequest);
                    rackRequests.put(rackName, amRackResourceRequest);
                }

                if(nodeName != null) {
                    amRackResourceRequest.setRelaxLocality(false);
                    ResourceRequest amNodeResourceRequest = this.createAMResourceRequest(nodeName, capability);
                    amResourceRequests.add(amNodeResourceRequest);
                }
            }

            if(LOG.isDebugEnabled()) {
                var9 = amResourceRequests.iterator();

                while(var9.hasNext()) {
                    ResourceRequest amResourceRequest = (ResourceRequest)var9.next();
                    LOG.debug("ResourceRequest: resource = " + amResourceRequest.getResourceName() + ", locality = " + amResourceRequest.getRelaxLocality());
                }
            }

            return amResourceRequests;
        }
    }

    private ResourceRequest createAMResourceRequest(String resource, Resource capability) {
        ResourceRequest resourceRequest = (ResourceRequest)recordFactory.newRecordInstance(ResourceRequest.class);
        resourceRequest.setPriority(AM_CONTAINER_PRIORITY);
        resourceRequest.setResourceName(resource);
        resourceRequest.setCapability(capability);
        resourceRequest.setNumContainers(1);
        resourceRequest.setRelaxLocality(true);
        return resourceRequest;
    }

    private void setTokenRenewerConf(ContainerLaunchContext context, Configuration conf, String regex) throws IOException {
        DataOutputBuffer dob = new DataOutputBuffer();
        Configuration copy = new Configuration(false);
        copy.clear();
        int count = 0;
        Iterator var7 = conf.iterator();

        while(var7.hasNext()) {
            Entry<String, String> map = (Entry)var7.next();
            String key = (String)map.getKey();
            String val = (String)map.getValue();
            if(key.matches(regex)) {
                copy.set(key, val);
                ++count;
            }
        }

        copy.write(dob);
        ByteBuffer appConf = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
        LOG.info("Send configurations that match regex expression: " + regex + " , total number of configs: " + count + ", total size : " + dob.getLength() + " bytes.");
        if(LOG.isDebugEnabled()) {
            Iterator itor = copy.iterator();

            while(itor.hasNext()) {
                Entry<String, String> entry = (Entry)itor.next();
                LOG.info((String)entry.getKey() + " ===> " + (String)entry.getValue());
            }
        }

        context.setTokensConf(appConf);
    }

    public void setJobPriority(JobID arg0, String arg1) throws IOException, InterruptedException {
        ApplicationId appId = TypeConverter.toYarn(arg0).getAppId();

        try {
            this.resMgrDelegate.updateApplicationPriority(appId, Priority.newInstance(Integer.parseInt(arg1)));
        } catch (YarnException var5) {
            throw new IOException(var5);
        }
    }

    public long getProtocolVersion(String arg0, long arg1) throws IOException {
        return this.resMgrDelegate.getProtocolVersion(arg0, arg1);
    }

    public long renewDelegationToken(Token<DelegationTokenIdentifier> arg0) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Use Token.renew instead");
    }

    public Counters getJobCounters(JobID arg0) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0).getJobCounters(arg0);
    }

    public String getJobHistoryDir() throws IOException, InterruptedException {
        return JobHistoryUtils.getConfiguredHistoryServerDoneDirPrefix(this.conf);
    }

    public JobStatus getJobStatus(JobID jobID) throws IOException, InterruptedException {
        JobStatus status = this.clientCache.getClient(jobID).getJobStatus(jobID);
        return status;
    }

    public TaskCompletionEvent[] getTaskCompletionEvents(JobID arg0, int arg1, int arg2) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0).getTaskCompletionEvents(arg0, arg1, arg2);
    }

    public String[] getTaskDiagnostics(TaskAttemptID arg0) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0.getJobID()).getTaskDiagnostics(arg0);
    }

    public TaskReport[] getTaskReports(JobID jobID, TaskType taskType) throws IOException, InterruptedException {
        return this.clientCache.getClient(jobID).getTaskReports(jobID, taskType);
    }

    private void killUnFinishedApplication(ApplicationId appId) throws IOException {
        ApplicationReport application = null;

        try {
            application = this.resMgrDelegate.getApplicationReport(appId);
        } catch (YarnException var4) {
            throw new IOException(var4);
        }

        if(application.getYarnApplicationState() != YarnApplicationState.FINISHED && application.getYarnApplicationState() != YarnApplicationState.FAILED && application.getYarnApplicationState() != YarnApplicationState.KILLED) {
            this.killApplication(appId);
        }
    }

    private void killApplication(ApplicationId appId) throws IOException {
        try {
            this.resMgrDelegate.killApplication(appId);
        } catch (YarnException var3) {
            throw new IOException(var3);
        }
    }

    private boolean isJobInTerminalState(JobStatus status) {
        return status.getState() == State.KILLED || status.getState() == State.FAILED || status.getState() == State.SUCCEEDED;
    }

    public void killJob(JobID arg0) throws IOException, InterruptedException {
        JobStatus status = this.clientCache.getClient(arg0).getJobStatus(arg0);
        ApplicationId appId = TypeConverter.toYarn(arg0).getAppId();
        if(status == null) {
            this.killUnFinishedApplication(appId);
        } else if(status.getState() != State.RUNNING) {
            this.killApplication(appId);
        } else {
            try {
                this.clientCache.getClient(arg0).killJob(arg0);
                long currentTimeMillis = System.currentTimeMillis();
                long timeKillIssued = currentTimeMillis;
                long killTimeOut = this.conf.getLong("yarn.app.mapreduce.am.hard-kill-timeout-ms", 10000L);

                while(currentTimeMillis < timeKillIssued + killTimeOut && !this.isJobInTerminalState(status)) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException var11) {
                        break;
                    }

                    currentTimeMillis = System.currentTimeMillis();
                    status = this.clientCache.getClient(arg0).getJobStatus(arg0);
                    if(status == null) {
                        this.killUnFinishedApplication(appId);
                        return;
                    }
                }
            } catch (IOException var12) {
                LOG.debug("Error when checking for application status", var12);
            }

            if(status != null && !this.isJobInTerminalState(status)) {
                this.killApplication(appId);
            }

        }
    }

    public boolean killTask(TaskAttemptID arg0, boolean arg1) throws IOException, InterruptedException {
        return this.clientCache.getClient(arg0.getJobID()).killTask(arg0, arg1);
    }

    public AccessControlList getQueueAdmins(String arg0) throws IOException {
        return new AccessControlList("*");
    }

    public JobTrackerStatus getJobTrackerStatus() throws IOException, InterruptedException {
        return JobTrackerStatus.RUNNING;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return ProtocolSignature.getProtocolSignature(this, protocol, clientVersion, clientMethodsHash);
    }

    public LogParams getLogFileParams(JobID jobID, TaskAttemptID taskAttemptID) throws IOException {
        return this.clientCache.getClient(jobID).getLogFilePath(jobID, taskAttemptID);
    }

    private static void warnForJavaLibPath(String opts, String component, String javaConf, String envConf) {
        if(opts != null && opts.contains("-Djava.library.path")) {
            LOG.warn("Usage of -Djava.library.path in " + javaConf + " can cause programs to no longer function if hadoop native libraries are used. These values should be set as part of the LD_LIBRARY_PATH in the " + component + " JVM env using " + envConf + " config settings.");
        }

    }

    public void close() throws IOException {
        if(this.resMgrDelegate != null) {
            this.resMgrDelegate.close();
            this.resMgrDelegate = null;
        }

        if(this.clientCache != null) {
            this.clientCache.close();
            this.clientCache = null;
        }

    }

    static {
        AM_CONTAINER_PRIORITY = (Priority)recordFactory.newRecordInstance(Priority.class);
        AM_CONTAINER_PRIORITY.setPriority(0);
    }
}
