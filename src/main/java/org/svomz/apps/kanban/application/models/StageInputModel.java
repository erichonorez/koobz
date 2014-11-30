package org.svomz.apps.kanban.application.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StageInputModel {

  private final String name;

  @JsonCreator
  public StageInputModel(@JsonProperty("name") final String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
}
