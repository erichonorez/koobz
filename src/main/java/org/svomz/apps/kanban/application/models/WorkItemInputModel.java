package org.svomz.apps.kanban.application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class WorkItemInputModel {

  private final String text;
  private final long stageId;

  @JsonCreator
  public WorkItemInputModel(@JsonProperty("text") final String text,
      @JsonProperty("stage_id") final long stageId) {
    Preconditions.checkNotNull(text);
    
    this.text = text;
    this.stageId = stageId;
  }
  
  public String getText() {
    return text;
  }

  public long getStageId() {
    return stageId;
  }

}
