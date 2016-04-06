package org.svomz.apps.koobz.domain.model;

import com.google.common.base.Preconditions;

public final class WorkflowId {

  private final String workflowId;

  public WorkflowId(final String aBoardId) {
    Preconditions.checkNotNull(aBoardId);

    this.workflowId = aBoardId;
  }

  public String value() {
    return this.workflowId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WorkflowId workflowId1 = (WorkflowId) o;

    return workflowId.equals(workflowId1.workflowId);

  }

  @Override
  public int hashCode() {
    return workflowId.hashCode();
  }
}
