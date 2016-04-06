package org.svomz.apps.koobz.application;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkflowIdentityService {

  public String nextStageIdentity() {
    return UUID.randomUUID().toString();
  }

  public String nextBoardIdentity() {
    return UUID.randomUUID().toString();
  }

  public String nextWorkItemIdentity() {
    return UUID.randomUUID().toString();
  }
}
