package org.svomz.apps.kanban.application.resources;

public class CreateOrUpdateWorkItemRequest {
  
  private long stageId;
  private String text;

  public long getStageId() {
    return stageId;
  }

  public String getText() {
    return text;
  }
  
}
