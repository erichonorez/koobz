package org.svomz.apps.kanban.application.models;

import jersey.repackaged.com.google.common.base.Preconditions;

import org.svomz.apps.kanban.domain.entities.WorkItem;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkItemViewModel {

  private WorkItem workItem;

  public WorkItemViewModel(final WorkItem workItem) {
    Preconditions.checkNotNull(workItem);
    
    this.workItem = workItem;
  }
  
  @JsonProperty("id")
  public long getId() {
    return this.workItem.getId();
  }
  
  @JsonProperty("text")
  public String getText() {
    return this.workItem.getText();
  }
  
  @JsonProperty("stageId")
  public long getStageId() {
    return this.workItem.getStage().getId();
  }
  
}
