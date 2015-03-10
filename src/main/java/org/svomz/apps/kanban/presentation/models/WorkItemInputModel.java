package org.svomz.apps.kanban.presentation.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class WorkItemInputModel {

  @NotNull
  @Size(min=1, max=255)
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
    return this.text;
  }

  public long getStageId() {
    return this.stageId;
  }

}
