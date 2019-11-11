package com.dtstack.engine.dtscript.am;

import com.dtstack.engine.dtscript.api.ApplicationContext;
import com.dtstack.engine.dtscript.common.DtContainerStatus;
import com.dtstack.engine.dtscript.common.Message;
import com.dtstack.engine.dtscript.container.DtContainerId;
import com.dtstack.engine.dtscript.common.LocalRemotePath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;


public class RunningAppContext implements ApplicationContext {

    private static final Log LOG = LogFactory.getLog(RunningAppContext.class);

    final ApplicationMaster appMaster;

    public RunningAppContext(ApplicationMaster appMaster) {
        this.appMaster = appMaster;
    }

    @Override
    public ApplicationId getApplicationID() {
        return appMaster.applicationAttemptID.getApplicationId();
    }

    @Override
    public int getWorkerNum() {
        return 1;
    }

    @Override
    public int getPsNum() {
        return 0;
    }

    @Override
    public List<Container> getWorkerContainers() {
        return null;
    }

    @Override
    public List<Container> getPsContainers() {
        return null;
    }

    @Override
    public DtContainerStatus getContainerStatus(DtContainerId containerId) {
        return null;
    }

    @Override
    public LinkedBlockingQueue<Message> getMessageQueue() {
        return null;
    }

    @Override
    public List<LocalRemotePath> getInputs(DtContainerId containerId) {
        return appMaster.appArguments.inputInfos;
    }

    @Override
    public List<InputSplit> getStreamInputs(DtContainerId containerId) {
        return null;
    }

    @Override
    public List<LocalRemotePath> getOutputs() {
        return appMaster.appArguments.outputInfos;
    }

    @Override
    public String getTensorBoardUrl() {
        //return appMaster.containerListener.getTensorboardUrl();
        return null;
    }

    @Override
    public Map<DtContainerId, String> getReporterProgress() {
        //return appMaster.containerListener.getReporterProgress();
        return null;
    }

    @Override
    public Map<DtContainerId, String> getContainersAppStartTime() {
        //return appMaster.containerListener.getContainersAppStartTime();
        return null;
    }

    @Override
    public Map<DtContainerId, String> getContainersAppFinishTime() {
        //return appMaster.containerListener.getContainersAppFinishTime();
        return null;
    }

    @Override
    public Map<DtContainerId, String> getMapedTaskID() {
        //return appMaster.containerListener.getMapedTaskID();
        return null;
    }

    @Override
    public Map<DtContainerId, ConcurrentHashMap<String, LinkedBlockingDeque<Object>>> getContainersCpuMetrics() {
        //return appMaster.containerListener.getContainersCpuMetrics();
        return null;
    }

    @Override
    public int getSavingModelStatus() {
        return -1;
        //return appMaster.containerListener.interResultCompletedNum(appMaster.containerListener.interResultTimeStamp());
    }

    @Override
    public Boolean getStartSavingStatus() {
        //return appMaster.startSavingModel;
        return null;
    }

    @Override
    public int getSavingModelTotalNum() {
        //return appMaster.containerListener.getInnerSavingContainerNum();
        return -1;
    }

    @Override
    public void startSavingModelStatus(Boolean flag) {
        LOG.info("current savingModelStatus is " + flag);
        //appMaster.startSavingModel = flag;
    }

    @Override
    public Boolean getLastSavingStatus() {
        //return appMaster.lastSavingStatus;
        return false;
    }

    @Override
    public List<Long> getModelSavingList() {
        //return appMaster.savingModelList;
        return null;
    }

}
