package org.svomz.apps.koobz.ports.adapters.rest.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class WorkflowInputModel {

  @NotNull
  @Size(min = 1, max = 255)
  private final String name;

  @JsonCreator
  public WorkflowInputModel(@JsonProperty("name") final String name) {
    Preconditions.checkNotNull(name);

    this.name = name;
  }

  public String getName() {
    return name;
  }

}
