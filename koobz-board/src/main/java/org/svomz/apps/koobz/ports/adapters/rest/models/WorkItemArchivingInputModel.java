package org.svomz.apps.koobz.ports.adapters.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class WorkItemArchivingInputModel {

  @NotNull
  private final boolean archived;

  public WorkItemArchivingInputModel(@JsonProperty("archived") boolean archived) {
    this.archived = archived;
  }

  public boolean isArchived() {
    return this.archived;
  }
}
