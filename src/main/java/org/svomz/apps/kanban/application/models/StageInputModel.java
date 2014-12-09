package org.svomz.apps.kanban.application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class StageInputModel {

  private final String name;

  @JsonCreator
  public StageInputModel(@JsonProperty("name") final String name) {
    Preconditions.checkNotNull(name);

    this.name = name;
  }

  public String getName() {
    return name;
  }

}
