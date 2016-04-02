package org.svomz.apps.koobz.board.ports.adapters.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class WorkItemMoveInputModel {

  @NotNull
  private String to;

  public WorkItemMoveInputModel(@JsonProperty("to") final String to) {
    this.to = to;
  }

  public String getTo() {
    return to;
  }
}
