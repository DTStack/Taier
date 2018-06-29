package com.dtstack.learning.api;

import com.dtstack.learning.container.LearningContainerId;

public interface ContainerListener {

  void registerContainer(LearningContainerId xlearningContainerId, String role);

  boolean isAllPsContainersFinished();

  boolean isTrainCompleted();

  boolean isAllWorkerContainersSucceeded();

  int interResultCompletedNum(Long lastInnerModel);
}
