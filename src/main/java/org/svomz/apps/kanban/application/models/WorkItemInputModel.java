package org.svomz.apps.kanban.application.models;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class WorkItemInputModel {

  @NotNull
  @Size(min=1, max=255)
  private final String title;
  
  private final long stageId;

  private Integer order;

  @JsonCreator
  public WorkItemInputModel(@JsonProperty("title") final String title,
      @JsonProperty("stageId") final long stageId,
      @Nullable @JsonProperty("order") final Integer order) {
    Preconditions.checkNotNull(title);

    this.title = title;
    this.stageId = stageId;
    this.order = order;
  }

  public String getTitle() {
    return this.title;
  }

  public long getStageId() {
    return this.stageId;
  }
  
  @Nullable
  public Integer getOrder() {
    return this.order;
  }

}
