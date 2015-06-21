package org.svomz.apps.kanban.application.models;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorModel {

  private final String message;

  public ErrorModel(@Nullable final String message) {
    this.message = message;
  }

  @JsonProperty("message")
  public String getMessage() {
    return this.message;
  }

}
