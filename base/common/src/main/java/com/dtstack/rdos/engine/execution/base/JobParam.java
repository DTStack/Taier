package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchAddJarOperator;
import com.dtstack.rdos.engine.execution.base.operator.stream.AddJarOperator;
import com.google.common.base.Preconditions;

/**
 * TODO 改造
 * Reason:
 * Date: 2018/5/4
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobParam {

    private String jarPath;

    private String mainClass;

    private String jobName;

    private String classArgs;

    public JobParam(JobClient jobClient){
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                AddJarOperator addjarOperator = (AddJarOperator) operator;
                jarPath =  addjarOperator.getJarPath();

                if(addjarOperator.getMainClass() != null){
                    mainClass = addjarOperator.getMainClass();
                }
                break;
            }else if(operator instanceof BatchAddJarOperator){
                BatchAddJarOperator addJarOperator = (BatchAddJarOperator) operator;
                jarPath =  addJarOperator.getJarPath();
                if(addJarOperator.getMainClass() != null){
                    mainClass = addJarOperator.getMainClass();
                }

                break;
            }
        }

        Preconditions.checkNotNull(jarPath, "submit type of MR need to add jar operator.");

        jobName = jobClient.getJobName();
        classArgs = jobClient.getClassArgs();
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }
}
