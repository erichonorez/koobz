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

  @NotNull
  private final String stageId;

  private Integer order;

  @Size(max = 2048)
  private String description;

  @JsonCreator
  public WorkItemInputModel(@JsonProperty("title") final String title,
      @JsonProperty("stageId") final String stageId,
      @Nullable @JsonProperty("order") final Integer order,
      @Nullable @JsonProperty("description") final String description) {
    Preconditions.checkNotNull(stageId);
    Preconditions.checkNotNull(title);

    this.title = title;
    this.stageId = stageId;
    this.order = order;
    this.description = description;
  }

  public String getTitle() {
    return this.title;
  }

  public String getStageId() {
    return this.stageId;
  }
  
  @Nullable
  public Integer getOrder() {
    return this.order;
  }

  @Nullable
  public String getDescription() {
    return this.description;
  }

}
