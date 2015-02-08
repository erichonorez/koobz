package org.svomz.apps.kanban.application.models;

import java.util.ArrayList;
import java.util.List;

import jersey.repackaged.com.google.common.base.Preconditions;

import org.svomz.apps.kanban.domain.entities.Board;
import org.svomz.apps.kanban.domain.entities.Stage;
import org.svomz.apps.kanban.domain.entities.WorkItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents how a {@link Board} should be JSONified.
 */
public class BoardViewModel {

  private final Board board;

  public BoardViewModel(final Board board) {
    Preconditions.checkNotNull(board);
    
    this.board = board;
  }
  
  @JsonProperty("id")
  @JsonView(SimpleView.class)
  public long getId() {
    return this.board.getId();
  }
  
  @JsonProperty("name")
  @JsonView(SimpleView.class)
  public String getName() {
    return this.board.getName();
  }
  
  @JsonProperty("stages")
  @JsonView(FullView.class)
  public List<Stage> getStages() {
    return new ArrayList<>(this.board.getStages());
  }
  
  @JsonProperty("workItems")
  @JsonView(FullView.class)
  public List<WorkItem> getWorkItems() {
    return new ArrayList<>(this.board.getWorkItems());
  }
  
  /**
   * Specifies {@link BoardViewModel}'s attributes that must be JSONified to have a simple representation. 
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class SimpleView { }
  
  /**
   * Specifies {@link BoardViewModel}'s attributes that must be JSONified to have a full representation. 
   * 
   * This class is intended to be used as parameter of {@link JsonView} annotation.
   */
  public static class FullView extends SimpleView { }
  
}
