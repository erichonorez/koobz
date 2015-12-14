package org.svomz.apps.kanban.application.models;


import org.svomz.apps.kanban.domain.WorkItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

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
  
  @JsonProperty("title")
  public String getTitle() {
    return this.workItem.getTitle();
  }
  
  @JsonProperty("order")
  public int getOrder() {
    return this.workItem.getOrder();
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.workItem.getDescription();
  }
}
