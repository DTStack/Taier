package com.dtstack.learning.api;

import com.dtstack.learning.container.XLearningContainerId;

public interface ContainerListener {

  void registerContainer(XLearningContainerId xlearningContainerId, String role);

  boolean isAllPsContainersFinished();

  boolean isTrainCompleted();

  boolean isAllWorkerContainersSucceeded();

  int interResultCompletedNum(Long lastInnerModel);
}
