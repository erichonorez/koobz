package org.svomz.apps.koobz.board.ports.adapters.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by eric on 02/04/16.
 */
public class WorkItemPositionInputModel {

  @NotNull
  @Min(0)
  private final int newPosition;

  public WorkItemPositionInputModel(@JsonProperty("newPosition") int newPosition) {
    this.newPosition = newPosition;
  }

  public int getNewPosition() {
    return this.newPosition;
  }
}
